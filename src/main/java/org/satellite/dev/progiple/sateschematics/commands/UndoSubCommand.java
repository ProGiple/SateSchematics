package org.satellite.dev.progiple.sateschematics.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.commands.processor.NoArgCommand;
import org.satellite.dev.progiple.sateschematics.Config;
import org.satellite.dev.progiple.sateschematics.schems.pasted.undo.UndoManager;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "undo")
@Check(permissions = "@.undo", flags = NoArgCommand.AccessFlag.PLAYER_ONLY)
public class UndoSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender sender, String[] strings) {
        Player player = (Player) sender;

        if (UndoManager.undo(player.getUniqueId())) {
            Config.sendMessage(player, "undo");
            return;
        }

        Config.sendMessage(player, "noSchemsForUndo");
    }
}
