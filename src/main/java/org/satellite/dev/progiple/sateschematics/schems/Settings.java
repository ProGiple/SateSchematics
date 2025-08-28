package org.satellite.dev.progiple.sateschematics.schems;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Settings {
    private int offsetX = 0;
    private int offsetY = 0;
    private int offsetZ = 0;
    private boolean connectPlayer = false;
    private boolean ignoreAir = false;
    private List<String> ignoredMaterials = new ArrayList<>();

    public Settings load(ConfigurationSection section) {
        if (section == null) return this;
        this.ignoreAir = section.getBoolean("ignoreAir");
        this.ignoredMaterials = section.getStringList("ignoredMaterials");

        ConfigurationSection offsets = section.getConfigurationSection("offsets");
        if (offsets == null) return this;

        this.offsetX = offsets.getInt("x");
        this.offsetY = offsets.getInt("y");
        this.offsetZ = offsets.getInt("z");
        this.connectPlayer = offsets.getBoolean("connectPlayer");
        return this;
    }

    public void save(ConfigurationSection baseSection) {
        baseSection.set("ignoreAir", this.ignoreAir);
        baseSection.set("ignoredMaterials", this.ignoredMaterials);

        ConfigurationSection offsets = baseSection.createSection("offsets");
        offsets.set("x", this.offsetX);
        offsets.set("y", this.offsetY);
        offsets.set("z", this.offsetZ);
        offsets.set("connectPlayer", this.connectPlayer);
    }
}
