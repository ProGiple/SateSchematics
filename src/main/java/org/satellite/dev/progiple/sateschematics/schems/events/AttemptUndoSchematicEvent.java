package org.satellite.dev.progiple.sateschematics.schems.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter @RequiredArgsConstructor
public class AttemptUndoSchematicEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final UUID uuid;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
