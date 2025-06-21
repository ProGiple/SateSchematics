package org.satellite.dev.progiple.sateschematics.schems.pasted.undo;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.satellite.dev.progiple.sateschematics.schems.SchemBlock;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@UtilityClass
public class UndoManager {
    private final Set<UndoSession> undoSessions = new HashSet<>();

    public UndoSession getSession(UUID uuid) {
        return undoSessions.stream().filter(s -> s.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public PastedSchematic paste(UUID uuid, PastedSchematic schematic) {
        UndoSession session = getSession(uuid);
        if (session == null) {
            session = new UndoSession(uuid);
            undoSessions.add(session);
        }

        session.add(schematic);
        return schematic;
    }

    public PastedSchematic paste(UUID uuid, YAMLSchematic schematic, Location location, Function<SchemBlock, Boolean> filter) {
        return paste(uuid, schematic.paste(location, filter));
    }

    public PastedSchematic paste(UUID uuid, YAMLSchematic schematic, Location location) {
        return paste(uuid, schematic, location, null);
    }

    public boolean undo(UUID uuid) {
        UndoSession session = getSession(uuid);
        if (session == null) return false;

        return session.undo();
    }

    public boolean clear(UUID uuid) {
        UndoSession session = getSession(uuid);
        if (session == null) return false;

        session.clear();
        return true;
    }
}
