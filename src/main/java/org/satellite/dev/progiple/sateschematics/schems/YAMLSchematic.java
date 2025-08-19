package org.satellite.dev.progiple.sateschematics.schems;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.novasparkle.lunaspring.API.configuration.Configuration;
import org.satellite.dev.progiple.sateschematics.SateSchematics;
import org.satellite.dev.progiple.sateschematics.schems.events.PrePasteSchematicEvent;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedBlock;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class YAMLSchematic {
    private final Configuration config;
    private final Set<SchemBlock> schemBlocks;
    private final Vector minVector;
    private final Vector maxVector;
    private final String id;

    @Setter private Settings settings;
    private boolean maySaving;
    public YAMLSchematic(Location pos1, Location pos2, Location center, String id) {
        this.id = id;
        if (pos1.getY() > pos2.getY()) {
            Location memoryPos = pos1.clone();
            pos1 = pos2.clone();
            pos2 = memoryPos;
        }

        this.minVector = pos1.toVector().subtract(center.toVector());
        this.maxVector = pos2.toVector().subtract(center.toVector());

        File file = new File(SateSchematics.getINSTANCE().getDataFolder(), String.format("schematics/%s.yml", id));
        this.config = new Configuration(file);

        this.schemBlocks = SchematicManager.getBlocksBetween(pos1, pos2)
                .stream()
                .map(b -> new SchemBlock(b.getBlock(), center))
                .collect(Collectors.toSet());
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

        ConfigurationSection vectorSection = this.config.getSection("vectors");
        this.schemBlocks = vectorSection.getKeys(false)
                .stream()
                .map(k -> new SchemBlock(Objects.requireNonNull(vectorSection.getConfigurationSection(k))))
                .collect(Collectors.toSet());
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

        ConfigurationSection parentSection = this.config.getSection("vectors");
        if (parentSection == null) parentSection = this.config.createSection((String) null, "vectors");

        ConfigurationSection finalParentSection = parentSection;
        this.schemBlocks.forEach(s -> s.createSection(finalParentSection));
        this.settings.save(this.config.getSection("settings"));
        this.config.save();

        this.maySaving = false;
        return true;
    }

    public PastedSchematic paste(Location pasteLoc, Function<SchemBlock, Boolean> filter) {
        PrePasteSchematicEvent schematicEvent = new PrePasteSchematicEvent(this, pasteLoc, filter);
        Bukkit.getPluginManager().callEvent(schematicEvent);
        if (schematicEvent.isCancelled()) return null;

        Location location = this.getOffsetLocation(pasteLoc);
        Set<PastedBlock> blocks = this.schemBlocks
                .stream()
                .filter(b -> !(this.settings.isIgnoreAir() && b.getMaterial().isAir()) && (filter == null || filter.apply(b)))
                .map(s -> s.paste(location))
                .collect(Collectors.toSet());
        return new PastedSchematic(pasteLoc, blocks, this.id);
    }

    public Location getOffsetLocation(Location pasteLocation) {
        return pasteLocation.clone().add(this.settings.getOffsetX(), this.settings.getOffsetY(), this.settings.getOffsetZ());
    }

    public enum SaveMode {
        FROM_PLAYER,
        VECTOR_CENTER
    }
}
