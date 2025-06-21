package org.satellite.dev.progiple.sateschematics.schems.events;

import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;

import java.util.UUID;

@Getter
public class UndoSchematicEvent extends AttemptUndoSchematicEvent implements Cancellable {
    private final static HandlerList handlers = new HandlerList();

    private boolean isCancelled;
    private final PastedSchematic pastedSchematic;
    public UndoSchematicEvent(UUID who, PastedSchematic schematic) {
        super(who);
        this.pastedSchematic = schematic;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
