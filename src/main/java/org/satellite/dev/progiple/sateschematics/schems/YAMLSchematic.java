package org.satellite.dev.progiple.sateschematics.schems;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateschematics.SateSchematics;
import org.satellite.dev.progiple.sateschematics.schems.events.PrePasteSchematicAsyncEvent;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class YAMLSchematic {
    public static final int PARALLELED_SIZE = 10000;

    private final Configuration config;
    private final Set<SchemBlock> schemBlocks;
    private final Vector minVector;
    private final Vector maxVector;
    private final String id;

    @Setter private Settings settings;
    private boolean maySaving;
    public YAMLSchematic(Location pos1, Location pos2, Location center, String id) {
        this(pos1, pos2, center, id, new HashSet<>());
        Iterator<Block> iterator = SchematicManager.getBlocksBetween(pos1, pos2);
        while (iterator.hasNext()) {
            Block block = iterator.next();
            this.schemBlocks.add(new SchemBlock(block, center));
        }
    }

    public YAMLSchematic(Location pos1, Location pos2, Location center, String id, Set<SchemBlock> schemBlocks) {
        this.id = id;
        if (pos1.getY() > pos2.getY()) {
            Location memoryPos = pos1.clone();
            pos1 = pos2.clone();
            pos2 = memoryPos;
        }

        this.minVector = pos1.toVector().subtract(center.toVector());
        this.maxVector = pos2.toVector().subtract(center.toVector());

        File file = new File(SateSchematics.getInstance().getDataFolder(), String.format("schematics/%s.yml", id));
        this.config = new Configuration(file);

        this.schemBlocks = schemBlocks;
        this.maySaving = true;
        this.settings = new Settings();
    }

    public YAMLSchematic(Location pos1, Location pos2, String id) {
        this(pos1, pos2, SchematicManager.getBlockCenter(pos1, pos2), id);
    }

    public YAMLSchematic(Location pos1, Location pos2, Player player, String id) {
        this(pos1, pos2, player.getLocation().getBlock().getLocation(), id);
    }

    public YAMLSchematic(File file) {
        this.id = file.getName().replace(".yml", "");
        this.config = new Configuration(file);

        this.minVector = SchematicManager.stringToVector(this.config.getString("minVector"));
        this.maxVector = SchematicManager.stringToVector(this.config.getString("maxVector"));

        ConfigurationSection palette = this.config.getSection("palette");
        this.schemBlocks = new HashSet<>();

        var blocks = this.config.getStringList("blocks");
        for (String block : blocks) {
            String[] split = block.split(";");
            if (split.length < 4) continue;

            String id = split[3];
            String strData = palette.getString(id);

            Vector vector = new Vector(
                    LunaMath.toInt(split[0]),
                    LunaMath.toInt(split[1]),
                    LunaMath.toInt(split[2]));

            EntityType et = split.length >= 5 ?
                    Utils.getEnumValue(EntityType.class, split[4]) :
                    null;
            SchemBlock schemBlock = new SchemBlock(strData, vector, et);
            this.schemBlocks.add(schemBlock);
        }

        this.maySaving = false;
        this.settings = new Settings().load(this.config.getSection("settings"));
    }

    public File getFile() {
        return this.config.getFile();
    }

    public Stream<Vector> getVectors() {
        return this.schemBlocks.stream().map(SchemBlock::getVector);
    }

    public Stream<Location> getPasteLocations(Location center) {
        return this.schemBlocks.stream().map(b -> b.getPasteLocation(center));
    }

    public SchemBlock getSchemBlock(Vector vector) {
        return this.schemBlocks.stream().filter(s -> s.getVector().equals(vector)).findFirst().orElse(null);
    }

    public boolean save() {
        if (!this.maySaving) return false;

        this.config.setString("minVector", SchematicManager.vectorToString(this.minVector));
        this.config.setString("maxVector", SchematicManager.vectorToString(this.maxVector));

        ConfigurationSection settings = this.config.createSection((String) null, "settings");
        this.settings.save(settings);

        ConfigurationSection palette = this.config.createSection((String) null, "palette");
        int paletteId = 0;
        Map<String, Integer> paletteBlockDates = new HashMap<>();

        List<String> blocks = new ArrayList<>();
        for (SchemBlock schemBlock : this.schemBlocks) {
            if (schemBlock.getBlockData() == null) continue;

            String strData = schemBlock.getBlockData().getAsString();
            int id = paletteBlockDates.getOrDefault(strData, -1);
            if (id == -1) {
                id = paletteId++;
                paletteBlockDates.put(strData, id);
                palette.set(String.valueOf(id), strData);
            }

            String line = schemBlock.vectorToString() + ";" + id;
            if (schemBlock.getSpawnerType() != null) line += ";" + schemBlock.getSpawnerType().name();

            blocks.add(line);
        }

        this.config.setStringList("blocks", blocks);
        this.config.save();

        this.maySaving = false;
        return true;
    }

    public CompletableFuture<Boolean> saveAsync() {
        return CompletableFuture.supplyAsync(this::save);
    }

    public Stream<SchemBlock> collectPasteBlocks(Function<SchemBlock, Boolean> filter) {
        boolean ignoreAir = this.settings != null && this.settings.isIgnoreAir();
        Set<Material> ignoredMaterials = this.settings == null ? null : this.settings.getIgnoredMaterials();

        boolean hasFilter = filter != null;
        boolean hasIgnoredMaterials = ignoredMaterials != null && !ignoredMaterials.isEmpty();

        Stream<SchemBlock> stream = this.schemBlocks.size() >= PARALLELED_SIZE ?
                this.schemBlocks.parallelStream() :
                this.schemBlocks.stream();
        return stream
                .filter(schemBlock -> {
                    Material material = schemBlock.getBlockData().getMaterial();
                    if (ignoreAir && material.isAir()) {
                        return false;
                    }

                    if (hasIgnoredMaterials && ignoredMaterials.contains(material)) {
                        return false;
                    }

                    return !hasFilter || filter.apply(schemBlock);
                });
    }

    protected boolean invokeEvent(Location pasteLoc, Function<SchemBlock, Boolean> filter) {
        PrePasteSchematicAsyncEvent schematicEvent = new PrePasteSchematicAsyncEvent(this, pasteLoc, filter);
        return schematicEvent.callEvent() && !schematicEvent.isCancelled();
    }

    @Nullable
    public PastedSchematic paste(Location pasteLoc, Function<SchemBlock, Boolean> filter) {
        if (!invokeEvent(pasteLoc, filter)) return null;
        return rawPaste(collectPasteBlocks(filter), pasteLoc);
    }

    @Nullable
    protected PastedSchematic rawPaste(Stream<SchemBlock> stream, Location pasteLoc) {
        Location offsetLocation = this.getOffsetLocation(pasteLoc);
        var blocks = stream
                .map(s -> s.paste(offsetLocation))
                .collect(Collectors.toCollection(HashSet::new));

        if (blocks.isEmpty()) {
            return null;
        }

        return new PastedSchematic(pasteLoc, blocks, this.id);
    }

    @NotNull
    public CompletableFuture<PastedSchematic> pasteAsync(Location pasteLoc,
                                                         Function<SchemBlock, Boolean> filter) {
        if (!invokeEvent(pasteLoc, filter)) return CompletableFuture.completedFuture(null);
        return CompletableFuture
                .supplyAsync(() -> collectPasteBlocks(filter))
                .thenApplyAsync(
                        s -> rawPaste(s, pasteLoc),
                        r -> Bukkit.getScheduler().runTask(SateSchematics.getInstance(), r));
    }

    public Location getOffsetLocation(Location pasteLocation) throws IllegalStateException {
        if (pasteLocation == null || pasteLocation.getWorld() == null) {
            throw new IllegalArgumentException("Invalid paste location");
        }

        return pasteLocation
                .clone()
                .add(
                        this.settings.getOffsetX(),
                        this.settings.getOffsetY(),
                        this.settings.getOffsetZ()
                );
    }

    public enum SaveMode {
        FROM_PLAYER,
        VECTOR_CENTER
    }
}
