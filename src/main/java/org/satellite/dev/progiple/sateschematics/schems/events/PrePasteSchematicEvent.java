package org.satellite.dev.progiple.sateschematics.schems.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.satellite.dev.progiple.sateschematics.schems.SchemBlock;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;

import java.util.function.Function;

@Getter @RequiredArgsConstructor
public class PrePasteSchematicEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean isCancelled = false;

    private final YAMLSchematic schematic;
    private final Location pasteLocation;
    private final Function<SchemBlock, Boolean> filter;

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
