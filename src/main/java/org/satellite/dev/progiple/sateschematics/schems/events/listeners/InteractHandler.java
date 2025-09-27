package org.satellite.dev.progiple.sateschematics.schems.events.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.novasparkle.lunaspring.API.events.CooldownPrevent;
import org.novasparkle.lunaspring.API.util.service.managers.NBTManager;
import org.novasparkle.lunaspring.self.LSConfig;
import org.satellite.dev.progiple.sateschematics.Config;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

public class InteractHandler implements Listener {
    private final CooldownPrevent<Player> playerCooldownPrevent = new CooldownPrevent<>(75);

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType().isAir() || !NBTManager.hasTag(hand, "sateschems-item")) return;

        Block block = e.getClickedBlock();
        if (block == null || block.getType().isAir()) return;

        Action action = e.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) return;

        if (this.playerCooldownPrevent.isCancelled(e, player)) return;

        if (!player.hasPermission("sateschematics.pos")) {
            LSConfig.sendMessage(player, "noPermission");
            return;
        }

        boolean isFirstPos = action == Action.LEFT_CLICK_BLOCK;
        e.setCancelled(true);

        if (SchematicManager.selectPos(player, block.getLocation(), isFirstPos))
            Config.sendMessage(player, "selectPos", "pos-%-" + (isFirstPos ? 1 : 2));
    }
}
