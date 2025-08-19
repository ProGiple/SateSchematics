package org.satellite.dev.progiple.sateschematics.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.Invocation;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.satellite.dev.progiple.sateschematics.Config;
import org.satellite.dev.progiple.sateschematics.SateSchematics;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "reload")
@Permissions("@.reload")
public class ReloadSubCommand implements Invocation {
    @Override
    public void invoke(CommandSender commandSender, String[] strings) {
        Config.reload();
        Config.sendMessage(commandSender, "reload");

        SateSchematics.getINSTANCE().loadSchems();
    }
}
