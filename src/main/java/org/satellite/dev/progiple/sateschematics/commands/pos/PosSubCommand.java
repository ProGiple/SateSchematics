package org.satellite.dev.progiple.sateschematics.commands.pos;

import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateschematics.Config;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.util.List;

@RequiredArgsConstructor
public abstract class PosSubCommand implements LunaExecutor {
    private final boolean isFirst;

    @Override
    public void invoke(CommandSender sender, String[] strings) {
        Player player = (Player) sender;

        Type type = strings.length == 2 ? Type.valueOf(strings[1]) : null;
        if (type == null) type = Type.valueOf(Config.getString("default_select_pos_mode"));

        Location location = null;
        if (type == Type.SEEING_LOCATION) {
            Block block = player.getTargetBlock(9);
            if (block != null) location = block.getLocation();
        }

        if (location == null) location = player.getLocation().getBlock().getLocation();

        if (SchematicManager.selectPos(player, location, this.isFirst))
            Config.sendMessage(player, "selectPos", "pos-%-" + (this.isFirst ? 1 : 2));
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> list) {
        return list.size() == 1 ? Utils.tabCompleterFiltering(List.of("SEEING_LOCATION", "CURRENT_LOCATION"), list.get(0)) : null;
    }

    private enum Type {
        SEEING_LOCATION,
        CURRENT_LOCATION
    }
}
