package org.satellite.dev.progiple.sateschematics.schems.events.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

public class QuitHandler implements Listener {
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (SchematicManager.getLocationMap().containsKey(player)) SchematicManager.getLocationMap().remove(player);
    }
}
