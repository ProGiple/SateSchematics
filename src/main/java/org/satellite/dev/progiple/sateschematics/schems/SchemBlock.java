package org.satellite.dev.progiple.sateschematics.schems;

import lombok.Getter;
import lombok.NonNull;
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

@Getter
public class SchemBlock {
    private final Vector vector;
    private final BlockData blockData;
    private final EntityType spawnerType;
    public SchemBlock(@NonNull Block block, @NonNull Location center) {
        this.vector = block.getLocation().toVector().subtract(center.toVector());

        this.blockData = block.getBlockData().clone();
        this.spawnerType = block.getState() instanceof CreatureSpawner spawner ? spawner.getSpawnedType() : null;
    }

    public SchemBlock(String strData, Vector vector, EntityType entityType) {
        this.vector = vector;
        this.spawnerType = entityType;
        this.blockData = Bukkit.createBlockData(strData);
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
        if (this.blockData != null) block.setBlockData(this.blockData);

        if (block.getState() instanceof CreatureSpawner spawner && this.spawnerType != null) {
            spawner.setSpawnedType(this.spawnerType);
            spawner.update();
        }

        return new PastedBlock(savedData, block);
    }

    public Material getMaterial() {
        return this.blockData != null ? this.blockData.getMaterial() : Material.AIR;
    }
}
