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
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.util.List;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "save")
@Check(permissions = "@.save", flags = NoArgCommand.AccessFlag.PLAYER_ONLY)
public class SaveSubCommand implements LunaExecutor {
    // sateschematics save [id] [save_type]

    @Override
    public void invoke(CommandSender commandSender, String[] strings) {
        if (strings.length <= 1) {
            Config.sendMessage(commandSender, "noArgs");
            return;
        }

        Player player = (Player) commandSender;
        if (SchematicManager.getSchem(strings[1]) != null) {
            Config.sendMessage(player, "schemNowExists", "id-%-" + strings[1]);
            return;
        }

        YAMLSchematic schematic = SchematicManager.create(
                player, strings[1], strings.length >= 3 ? YAMLSchematic.SaveMode.valueOf(strings[2]) : null);
        if (schematic == null) {
            Config.sendMessage(player, "noSelectedPoses");
            return;
        }

        if (SchematicManager.containsSchem(schematic)) {
            Config.sendMessage(player, "schemNowExists", "id-%-" + strings[1]);
            return;
        }

        SchematicManager.save(schematic);
        Config.sendMessage(player, "save", "id-%-" + strings[1]);
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> list) {
        return list.size() == 1 ? List.of("<id>") :
                list.size() == 2 ? Utils.tabCompleterFiltering(List.of("FROM_PLAYER", "VECTOR_CENTER"), list.get(1)) : null;
    }
}
