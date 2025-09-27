package org.satellite.dev.progiple.sateschematics.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.LunaCompleter;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.commands.processor.NoArgCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateschematics.Config;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.pasted.undo.UndoManager;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.util.List;
import java.util.stream.Collectors;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "paste")
@Check(permissions = "@.paste", flags = NoArgCommand.AccessFlag.PLAYER_ONLY)
public class PasteSubCommand implements LunaExecutor {
    @Override
    public List<String> tabComplete(CommandSender sender, List<String> list) {
        return list.size() == 1 ? Utils.tabCompleterFiltering(SchematicManager.getSchematics()
                .map(YAMLSchematic::getId).collect(Collectors.toSet()), list.get(0)) : null;
    }

    @Override
    public void invoke(CommandSender sender, String[] strings) {
        if (strings.length < 2) {
            Config.sendMessage(sender, "noArgs");
            return;
        }

        YAMLSchematic schematic = SchematicManager.getSchem(strings[1]);
        if (schematic == null) {
            Config.sendMessage(sender, "schemNotExists");
            return;
        }

        Player player = (Player) sender;
        if (UndoManager.paste(player.getUniqueId(), schematic, player.getLocation()) != null) {
            player.teleport(schematic.getOffsetLocation(player.getLocation()));
            Config.sendMessage(sender, "paste", "id-%-" + schematic.getId());
        }
    }
}
