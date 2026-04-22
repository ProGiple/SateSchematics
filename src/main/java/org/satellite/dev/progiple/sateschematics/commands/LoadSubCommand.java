package org.satellite.dev.progiple.sateschematics.commands;

import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateschematics.Config;
import org.satellite.dev.progiple.sateschematics.SateSchematics;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "load")
@Permissions("@.load")
public class LoadSubCommand implements LunaExecutor {
    // /sateschematics load <file>

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> list) {
        if (list.size() == 1) {
            File dir = new File(SateSchematics.getInstance().getDataFolder(), "schematics/");
            if (!dir.exists() || !dir.isDirectory()) return null;

            File[] files = dir.listFiles();
            if (files == null) return null;

            return Utils.tabCompleterFiltering(
                    Arrays.stream(files)
                            .filter(f -> SchematicManager.getSchem(f) != null)
                            .map(File::getName),
                    list.get(0)
            );
        }

        return null;
    }

    @Override
    public void invoke(CommandSender sender, String[] strings) {
        if (strings.length < 2) {
            Config.sendMessage(sender, "noArgs");
            return;
        }

        File file = new File(SateSchematics.getInstance().getDataFolder(), "schematics/" + strings[1]);
        if (!file.exists()) {
            Config.sendMessage(sender, "schemFileForLoadNotExists", "file-%-" + strings[1]);
            return;
        }

        YAMLSchematic schematic = SchematicManager.getSchem(file);
        if (schematic == null) {
            Config.sendMessage(sender, "schemWithLoadingFileExists", "file-%-" + strings[1]);
            return;
        }

        schematic = new YAMLSchematic(file);
        SchematicManager.load(schematic);

        Config.sendMessage(sender, "load", "id-%-" + schematic.getId());
    }
}
