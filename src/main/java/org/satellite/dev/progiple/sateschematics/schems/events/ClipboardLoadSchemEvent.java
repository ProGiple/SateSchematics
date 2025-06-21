package org.satellite.dev.progiple.sateschematics.schems.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;

import java.util.UUID;

@Getter @Setter
public class ClipboardLoadSchemEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private boolean isCancelled;
    private YAMLSchematic loadedSchematic;
    public ClipboardLoadSchemEvent(UUID uuid, YAMLSchematic schematic) {
        this.uuid = uuid;
        this.loadedSchematic = schematic;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
