package org.satellite.dev.progiple.sateschematics.schems.states;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.events.ClipboardLoadSchemEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@UtilityClass
public class Clipboard {
    private final Map<UUID, YAMLSchematic> clipboard = new HashMap<>();

    public YAMLSchematic getSchem(UUID uuid) {
        return clipboard.get(uuid);
    }

    public Stream<UUID> getHandlers(YAMLSchematic schematic) {
        return clipboard.entrySet().stream().filter(e -> e.getValue().equals(schematic)).map(Map.Entry::getKey);
    }

    public boolean loadSchem(UUID uuid, YAMLSchematic schematic) {
        ClipboardLoadSchemEvent loadSchemEvent = new ClipboardLoadSchemEvent(uuid, schematic);
        Bukkit.getPluginManager().callEvent(loadSchemEvent);

        schematic = loadSchemEvent.getLoadedSchematic();
        if (schematic == null) return false;

        clipboard.put(uuid, schematic);
        return true;
    }

    public boolean loadSchem(UUID uuid, String id) {
        YAMLSchematic schematic = SchematicManager.getSchem(id);
        return loadSchem(uuid, schematic);
    }

    public boolean loadSchem(UUID uuid, File file) {
        YAMLSchematic schematic = SchematicManager.getSchem(file);
        return loadSchem(uuid, schematic);
    }

    public void unloadSchem(UUID uuid) {
        clipboard.remove(uuid);
    }
}
