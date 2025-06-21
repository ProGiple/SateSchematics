package org.satellite.dev.progiple.sateschematics.schems.pasted.undo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.satellite.dev.progiple.sateschematics.schems.events.AttemptUndoSchematicEvent;
import org.satellite.dev.progiple.sateschematics.schems.events.UndoSchematicEvent;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor @Getter
public class UndoSession {
    private final List<PastedSchematic> pastedSchematics = new ArrayList<>();
    private final UUID uuid;

    public void add(PastedSchematic schematic) {
        this.pastedSchematics.add(schematic);
    }

    public boolean undo() {
        if (this.pastedSchematics.isEmpty()) {
            AttemptUndoSchematicEvent schematicEvent = new AttemptUndoSchematicEvent(this.uuid);
            Bukkit.getPluginManager().callEvent(schematicEvent);

            return false;
        }

        PastedSchematic schematic = this.pastedSchematics.get(this.pastedSchematics.size() - 1);

        UndoSchematicEvent schematicEvent = new UndoSchematicEvent(this.uuid, schematic);
        Bukkit.getPluginManager().callEvent(schematicEvent);
        if (schematicEvent.isCancelled()) return true;

        schematic.undo();
        return true;
    }

    public void clear() {
        this.pastedSchematics.forEach(PastedSchematic::undo);
        this.pastedSchematics.clear();
    }
}
