package org.satellite.dev.progiple.sateschematics.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.API.commands.annotations.Permissions;
import org.novasparkle.lunaspring.API.commands.annotations.SubCommand;
import org.novasparkle.lunaspring.API.util.utilities.LunaMath;
import org.novasparkle.lunaspring.API.util.utilities.Utils;
import org.satellite.dev.progiple.sateschematics.Config;
import org.satellite.dev.progiple.sateschematics.schems.Settings;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.util.List;
import java.util.stream.Collectors;

@SubCommand(appliedCommand = "sateschematics", commandIdentifiers = "edit")
@Permissions("@.edit")
public class SettingsEditSubCommand implements LunaExecutor {
    // schem edit <id> <param> <values...>

    @Override
    public void invoke(CommandSender sender, String[] strings) {
        if (strings.length < 4) {
            Config.sendMessage(sender, "noArgs");
            return;
        }

        YAMLSchematic schematic = SchematicManager.getSchem(strings[1]);
        if (schematic == null) {
            Config.sendMessage(sender, "schemNotExists");
            return;
        }

        Settings settings = schematic.getSettings();
        boolean request = switch (strings[2]) {
            case "offsetX" -> {
                settings.setOffsetX(LunaMath.toInt(strings[3]));
                schematic.getConfig().set("settings.offsets.x", settings.getOffsetX());
                yield true;
            }
            case "offsetY" -> {
                settings.setOffsetY(LunaMath.toInt(strings[3]));
                schematic.getConfig().set("settings.offsets.y", settings.getOffsetY());
                yield true;
            }
            case "offsetZ" -> {
                settings.setOffsetZ(LunaMath.toInt(strings[3]));
                schematic.getConfig().set("settings.offsets.z", settings.getOffsetZ());
                yield true;
            }
            case "connectPlayer" -> {
                settings.setConnectPlayer(LunaMath.toBoolean(strings[3]));
                schematic.getConfig().set("settings.offsets.connectPlayer", settings.isConnectPlayer());
                yield true;
            }
            case "ignoreAir" -> {
                settings.setIgnoreAir(LunaMath.toBoolean(strings[3]));
                schematic.getConfig().set("settings.ignoreAir", settings.isIgnoreAir());
                yield true;
            }
            case "ignoredMaterials" -> {
                settings.getIgnoredMaterials().clear();
                if (!strings[3].equalsIgnoreCase("clear")) {
                    for (int i = 3; i < strings.length; i++) {
                        String type = strings[i];

                        Material material = Material.matchMaterial(type);
                        if (material == null) continue;

                        settings.getIgnoredMaterials().add(material);
                    }
                }

                schematic.getConfig().set("settings.ignoredMaterials",
                        settings.getIgnoredMaterials()
                                .stream()
                                .map(Material::name)
                                .collect(Collectors.toList()));
                yield true;
            }
            default -> false;
        };

        if (request) {
            schematic.getConfig().save();
            Config.sendMessage(sender, "successEdit", "id-%-" + schematic.getId());
        }
        else {
            Config.sendMessage(sender, "failEdit", "id-%-" + strings[2]);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> list) {
        if (list.size() == 1)
            return Utils.tabCompleterFiltering(
                    SchematicManager.getSchematics().map(YAMLSchematic::getId), list.get(0));
        if (list.size() == 2) {
            return Utils.tabCompleterFiltering(
                    List.of("offsetX", "offsetY", "offsetZ", "connectPlayer", "ignoreAir", "ignoredMaterials"),
                    list.get(1));
        }
        if (list.isEmpty()) {
            return null;
        }

        YAMLSchematic schematic = SchematicManager.getSchem(list.get(0));
        if (schematic == null) return null;

        Settings settings = schematic.getSettings();

        String param = list.get(1);
        Object result = switch (param) {
            case "offsetX" -> settings.getOffsetX();
            case "offsetY" -> settings.getOffsetY();
            case "offsetZ" -> settings.getOffsetZ();
            case "connectPlayer" -> settings.isConnectPlayer();
            case "ignoreAir" -> settings.isIgnoreAir();
            case "ignoredMaterials" ->
                    String.join(" ",
                            settings.getIgnoredMaterials()
                                    .stream()
                                    .map(Material::name)
                                    .toList());
            default -> null;
        };

        if (result == null) return null;
        return List.of(result.toString(), "clear");
    }
}
