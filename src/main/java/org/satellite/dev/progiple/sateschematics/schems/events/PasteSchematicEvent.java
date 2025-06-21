package org.satellite.dev.progiple.sateschematics.schems.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedSchematic;

@Getter @RequiredArgsConstructor
public class PasteSchematicEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final PastedSchematic pastedSchematic;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
