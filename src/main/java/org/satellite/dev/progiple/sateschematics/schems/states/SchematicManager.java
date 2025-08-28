package org.satellite.dev.progiple.sateschematics.schems.states;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
        YAML_SCHEMATICS.removeIf(s -> s.getFile().equals(file));
        load(new YAMLSchematic(file));
    }

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
        if (containsSchem(schematic)) return false;

        load(schematic);
        schematic.save();
        return true;
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

    public Set<Location> getBlocksBetween(Location loc1, Location loc2) {
        World world = loc1.getWorld();

        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        Set<Location> blocks = new HashSet<>();
        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++)
                    blocks.add(new Location(world, x, y, z));

        return blocks;
    }

    public String vectorToString(int x, int y, int z) {
        return String.format("%s;%s;%s", x, y, z);
    }

    public String vectorToString(Vector vector) {
        return SchematicManager.vectorToString(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }
}
