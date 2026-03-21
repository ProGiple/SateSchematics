package org.satellite.dev.progiple.sateschematics;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.novasparkle.lunaspring.API.configuration.IConfig;

@UtilityClass
public class Config {
    private final IConfig config;
    static {
        config = new IConfig(SateSchematics.getInstance());
    }

    public void reload() {
        config.reload(SateSchematics.getInstance());
    }

    public ConfigurationSection getSection(String path) {
        return config.getSection(path);
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public void sendMessage(CommandSender sender, String id, String... rpl) {
        config.sendMessage(sender, id, rpl);
    }
}
