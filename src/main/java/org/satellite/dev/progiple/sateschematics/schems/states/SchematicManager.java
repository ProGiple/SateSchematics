package org.satellite.dev.progiple.sateschematics.schems.states;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.novasparkle.lunaspring.API.util.utilities.LunaMap;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.satellite.dev.progiple.sateschematics.Config;
import org.satellite.dev.progiple.sateschematics.schems.SchemBlock;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.events.PlayerSelectPosEvent;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedManager;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class SchematicManager {
    @Getter
    private final LunaMap<Player, Location, Location> locationMap = new LunaMap<>();
    private final Set<YAMLSchematic> YAML_SCHEMATICS = new HashSet<>();

    public Stream<YAMLSchematic> getSchematics() {
        return YAML_SCHEMATICS.stream();
    }

    public Location getPos(Player player, boolean isFirst) {
        if (!locationMap.containsKey(player)) return null;
        return isFirst ? locationMap.getFirstValue(player) : locationMap.getSecondValue(player);
    }

    public boolean selectPos(Player player, Location location, boolean isFirst) {
        PlayerSelectPosEvent event = new PlayerSelectPosEvent(player, location, isFirst);

        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        if (locationMap.containsKey(player)) {
            if (isFirst) locationMap.replaceFirstValue(player, location);
            else locationMap.replaceSecondValue(player, location);
            return true;
        }

        locationMap.put(player, isFirst ? location : null, isFirst ? null : location);
        return true;
    }

    public boolean containsPoses(Player player) {
        if (!locationMap.containsKey(player)) return false;

        Location pos1 = getPos(player, true);
        Location pos2 = getPos(player, false);
        return pos1 != null && pos2 != null && pos2.getWorld().equals(pos1.getWorld());
    }

    public PastedSchematic paste(YAMLSchematic schematic, Location pasteLocation, Function<SchemBlock, Boolean> filter) {
        return PastedManager.paste(schematic, pasteLocation.getBlock().getLocation().clone(), filter);
    }

    public PastedSchematic paste(YAMLSchematic schematic, Location pasteLocation) {
        return SchematicManager.paste(schematic, pasteLocation, null);
    }

    public YAMLSchematic create(Location pos1, Location pos2, Location center, String id) {
        return new YAMLSchematic(pos1, pos2, center, id);
    }

    public YAMLSchematic create(Location pos1, Location pos2, String id) {
        return new YAMLSchematic(pos1, pos2, id);
    }

    public YAMLSchematic create(Player player, String id, YAMLSchematic.SaveMode saveMode) {
        if (!containsPoses(player)) return null;

        Location pos1 = getPos(player, true);
        Location pos2 = getPos(player, false);

        if (saveMode == null) {
            saveMode = YAMLSchematic.SaveMode.valueOf(Config.getString("default_save_mode"));
        }

        if (saveMode == YAMLSchematic.SaveMode.FROM_PLAYER) {
            return new YAMLSchematic(pos1, pos2, player, id);
        }

        return create(pos1, pos2, id);
    }

    public void load(YAMLSchematic schematic) {
        YAML_SCHEMATICS.add(schematic);
    }

    public void load(File file) {
        if (!file.getName().endsWith(".yml")) {
            //loadWorldEditFile(file);
            return;
        }

        YAML_SCHEMATICS.removeIf(s -> s.getFile().equals(file));
        load(new YAMLSchematic(file));
    }

//    public void loadWorldEditFile(File file) throws IOException {
//        if (!file.exists()) {
//            throw new IOException("File not found: " + file.getPath());
//        }
//
//        ClipboardFormat format = ClipboardFormats.findByFile(file);
//        if (format == null) {
//            throw new IOException("Unsupported or invalid schematic format: " + file.getName());
//        }
//
//        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
//            com.sk89q.worldedit.extent.clipboard.Clipboard clipboard = reader.read();
//
//            BlockVector3 origin = clipboard.getMinimumPoint();
//            BlockVector3 maxPoint = clipboard.getMaximumPoint();
//
//            int minX = origin.getX();
//            int minY = origin.getY();
//            int minZ = origin.getZ();
//            int maxX = maxPoint.getX();
//            int maxY = maxPoint.getY();
//            int maxZ = maxPoint.getZ();
//
//            World world = centerLocation.getWorld();
//            Location pos1 = new Location(world, minX, minY, minZ);
//            Location pos2 = new Location(world, maxX, maxY, maxZ);
//
//            // Создаем новый объект YAMLSchematic через существующий конструктор
//            YAMLSchematic schematic = new YAMLSchematic(pos1, pos2, centerLocation, id);
//
//            // Обновляем блоки из WorldEdit (перезаписываем то, что насчитал конструктор)
//            schematic.schemBlocks.clear();
//
//            // Обходим все блоки в регионе схемы
//            BlockVector3 originVec = clipboard.getOrigin();
//
//            for (int x = minX; x <= maxX; x++) {
//                for (int y = minY; y <= maxY; y++) {
//                    for (int z = minZ; z <= maxZ; z++) {
//                        BlockVector3 pos = BlockVector3.at(x, y, z);
//                        BlockState blockState = clipboard.getBlock(pos);
//                        Material material = BukkitAdapter.adapt(blockState.getBlockType());
//
//                        if (material == null || material == Material.AIR) continue;
//
//                        Location blockLoc = new Location(world, x, y, z);
//                        schematic.schemBlocks.add(new SchemBlock(blockLoc, centerLocation));
//                    }
//                }
//            }
//
//            schematic.maySaving = true;
//            return schematic;
//        } catch (Exception e) {
//            throw new IOException("Failed to read schematic file: " + e.getMessage(), e);
//        }
//    }

    public void unload(YAMLSchematic schematic) {
        YAML_SCHEMATICS.remove(schematic);

        Set<UUID> handlers = Clipboard.getHandlers(schematic).collect(Collectors.toSet());
        handlers.forEach(Clipboard::unloadSchem);
    }

    public YAMLSchematic getSchem(String id) {
        return YAML_SCHEMATICS.stream().filter(s -> s.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    public YAMLSchematic getSchem(File file) {
        return YAML_SCHEMATICS
                .stream()
                .filter(s -> s.getFile().getPath().equalsIgnoreCase(file.getPath()))
                .findFirst()
                .orElse(null);
    }

    public boolean containsSchem(YAMLSchematic schematic) {
        return YAML_SCHEMATICS.contains(schematic) ||
                getSchem(schematic.getFile()) != null ||
                getSchem(schematic.getId()) != null;
    }

    public boolean save(YAMLSchematic schematic) {
        load(schematic);
        return schematic.save();
    }

    public CompletableFuture<Boolean> saveAsync(YAMLSchematic schematic) {
        load(schematic);
        return schematic.saveAsync();
    }

    public Vector stringToVector(@NonNull String value) {
        String[] split = value.split(";");
        if (split.length < 3) return null;

        return new Vector(
                LunaMath.toInt(split[0]),
                LunaMath.toInt(split[1]),
                LunaMath.toInt(split[2]));
    }

    public Location getBlockCenter(Location loc1, Location loc2) {
        int centerX = (int) Math.floor((loc1.getBlockX() + loc2.getBlockX()) / 2.0);
        int centerY = (int) Math.floor((loc1.getBlockY() + loc2.getBlockY()) / 2.0);
        int centerZ = (int) Math.floor((loc1.getBlockZ() + loc2.getBlockZ()) / 2.0);

        return new Location(loc1.getWorld(), centerX, centerY, centerZ);
    }

    public Iterator<Block> getBlocksBetween(Location loc1, Location loc2) {
        World world = loc1.getWorld();
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        return new Iterator<>() {
            private int x = minX, y = minY, z = minZ;

            @Override
            public boolean hasNext() {
                return x <= maxX && y <= maxY && z <= maxZ;
            }

            @Override
            public Block next() {
                Block block = world.getBlockAt(x, y, z);
                z++;
                if (z > maxZ) {
                    z = minZ;
                    y++;
                    if (y > maxY) {
                        y = minY;
                        x++;
                    }
                }
                return block;
            }
        };
    }

    public String vectorToString(int x, int y, int z) {
        return String.format("%s;%s;%s", x, y, z);
    }

    public String vectorToString(Vector vector) {
        return SchematicManager.vectorToString(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }
}
