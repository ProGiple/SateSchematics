package org.satellite.dev.progiple.sateschematics.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.commands.processor.NoArgCommand;
import org.satellite.dev.progiple.sateschematics.Config;
import org.satellite.dev.progiple.sateschematics.schems.items.PosItem;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "give")
@Check(permissions = "@.give", flags = NoArgCommand.AccessFlag.PLAYER_ONLY)
public class GiveSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender commandSender, String[] strings) {
        Player player = (Player) commandSender;

        PosItem posItem = new PosItem();
        posItem.give(player);

        Config.sendMessage(player, "get");
    }
}
