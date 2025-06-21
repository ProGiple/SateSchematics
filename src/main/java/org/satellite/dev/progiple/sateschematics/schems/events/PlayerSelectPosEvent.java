package org.satellite.dev.progiple.sateschematics.schems.events;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerSelectPosEvent extends PlayerEvent implements Cancellable {
    private final static HandlerList handlers = new HandlerList();

    private final Location selectedPos;
    private final boolean isFirstPoint;

    private boolean isCancelled = false;
    public PlayerSelectPosEvent(@NotNull Player who, Location location, boolean isFirstPoint) {
        super(who);
        this.selectedPos = location;
        this.isFirstPoint = isFirstPoint;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
