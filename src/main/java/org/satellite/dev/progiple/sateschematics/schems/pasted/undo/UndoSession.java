package org.satellite.dev.progiple.sateschematics.schems.pasted.undo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.satellite.dev.progiple.sateschematics.schems.events.AttemptUndoSchematicEvent;
import org.satellite.dev.progiple.sateschematics.schems.events.UndoSchematicEvent;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

@RequiredArgsConstructor @Getter
public class UndoSession {
    private final Stack<PastedSchematic> pastedSchematics = new Stack<>();
    private final UUID uuid;

    public void add(PastedSchematic schematic) {
        this.pastedSchematics.push(schematic);
    }

    public boolean undo() {
        if (this.pastedSchematics.isEmpty()) {
            AttemptUndoSchematicEvent schematicEvent = new AttemptUndoSchematicEvent(this.uuid);
            Bukkit.getPluginManager().callEvent(schematicEvent);

            return false;
        }

        PastedSchematic schematic = this.pastedSchematics.peek();

        UndoSchematicEvent schematicEvent = new UndoSchematicEvent(this.uuid, schematic);
        Bukkit.getPluginManager().callEvent(schematicEvent);
        if (schematicEvent.isCancelled()) return false;

        pastedSchematics.remove(schematic);
        schematic.undo();
        return true;
    }

    public void undoAll() {
        pastedSchematics.removeIf(p -> {
            UndoSchematicEvent schematicEvent = new UndoSchematicEvent(this.uuid, p);
            Bukkit.getPluginManager().callEvent(schematicEvent);
            if (schematicEvent.isCancelled()) return false;

            p.undo();
            return true;
        });
    }
}
