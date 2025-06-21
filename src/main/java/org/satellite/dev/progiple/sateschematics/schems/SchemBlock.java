package org.satellite.dev.progiple.sateschematics.schems;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;
import org.satellite.dev.progiple.sateschematics.schems.pasted.PastedBlock;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.util.Objects;

@Getter
public class SchemBlock {
    private final Vector vector;
    private final BlockData blockData;
    private final Material material;
    private final EntityType spawnerType;
    public SchemBlock(@NonNull Block block, @NonNull Location center) {
        this.material = block.getType();
        this.vector = block.getLocation().toVector().subtract(center.toVector());

        this.blockData = block.getBlockData().clone();
        this.spawnerType = block.getState() instanceof CreatureSpawner spawner ? spawner.getSpawnedType() : null;
    }

    public SchemBlock(ConfigurationSection section) {
        this.vector = SchematicManager.stringToVector(section.getName());
        this.material = Material.getMaterial(Objects.requireNonNull(section.getString("material")));

        String stringData = section.getString("data");
        this.blockData = stringData == null || stringData.isEmpty() ? null : Bukkit.createBlockData(stringData);

        String stringSpawnerType = section.getString("spawnerType");
        this.spawnerType = stringSpawnerType == null || stringSpawnerType.isEmpty() ? null : EntityType.valueOf(stringSpawnerType);
    }

    @SneakyThrows
    public ConfigurationSection createSection(@NonNull ConfigurationSection vectorsParent) {
        ConfigurationSection section = vectorsParent.createSection(this.vectorToString());

        section.set("material", this.material.name());
        section.set("spawnerType", this.spawnerType == null ? null : this.spawnerType.name());
        section.set("data", this.blockData.getAsString());
        return section;
    }

    public String vectorToString() {
        return SchematicManager.vectorToString(this.vector);
    }

    public Location getPasteLocation(Location center) {
        return center.clone().add(this.vector);
    }

    public PastedBlock paste(Location center) {
        Block block = this.getPasteLocation(center).getBlock();
        BlockData savedData = block.getBlockData().clone();

        block.setType(this.material);
        if (this.blockData != null) block.setBlockData(this.blockData);

        if (block.getState() instanceof CreatureSpawner spawner && this.spawnerType != null) {
            spawner.setSpawnedType(this.spawnerType);
            spawner.update();
        }

        return new PastedBlock(savedData, block);
    }
}
