package org.satellite.dev.progiple.sateschematics.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.novasparkle.lunaspring.API.commands.LunaCompleter;
import org.novasparkle.lunaspring.API.commands.annotations.Check;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.commands.processor.ZeroArgCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateschematics.Config;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.pasted.undo.UndoManager;
import org.satellite.dev.progiple.sateschematics.schems.states.Clipboard;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.util.List;
import java.util.stream.Collectors;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = {"clipboard", "cb"})
@Check(permissions = "@.clipboard", flags = ZeroArgCommand.AccessFlag.PLAYER_ONLY)
public class ClipboardSubCommand implements LunaCompleter {
    // sateschematics cb load [id]
    // sateschematics cb paste

    @Override
    public List<String> tabComplete(CommandSender commandSender, List<String> list) {
        return list.size() == 1 ? Utils.tabCompleterFiltering(List.of("load", "paste"), list.get(0)) :
                list.size() == 2 && list.get(0).equalsIgnoreCase("load") ?
                        Utils.tabCompleterFiltering(
                                SchematicManager.getSchematics().map(YAMLSchematic::getId).collect(Collectors.toSet()),
                                list.get(1)) : null;
    }

    @Override
    public void invoke(CommandSender commandSender, String[] strings) {
        if (strings.length < 2) {
            Config.sendMessage(commandSender, "noArgs");
            return;
        }

        Player player = (Player) commandSender;
        if (strings[1].equalsIgnoreCase("load")) {
            if (strings.length < 3) {
                Config.sendMessage(player, "noArgs");
                return;
            }

            YAMLSchematic schematic = SchematicManager.getSchem(strings[2]);
            if (schematic == null) {
                Config.sendMessage(player, "schemNotExists");
                return;
            }

            if (Clipboard.loadSchem(player.getUniqueId(), schematic))
                Config.sendMessage(player, "loadToClipboard", "id-%-" + strings[2]);
            return;
        }

        if (strings[1].equalsIgnoreCase("paste")) {
            YAMLSchematic schematic = Clipboard.getSchem(player.getUniqueId());
            if (schematic == null) {
                Config.sendMessage(player, "clipboardIsEmpty");
                return;
            }

            if (UndoManager.paste(player.getUniqueId(), schematic, player.getLocation()) != null) {
                player.teleport(schematic.getOffsetLocation(player.getLocation()));
                Config.sendMessage(player, "paste", "id-%-" + schematic.getId());
            }
        }
    }
}
