package org.satellite.dev.progiple.sateschematics.schems.pasted;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.satellite.dev.progiple.sateschematics.schems.events.PasteSchematicEvent;

import java.util.*;
import java.util.stream.Stream;

@Getter
public class PastedSchematic {
    private final Location center;
    private final Set<PastedBlock> pastedBlocks;
    private final String id;
    public PastedSchematic(Location center, Set<PastedBlock> blocks, String id) {
        this.id = id;
        this.center = center;
        this.pastedBlocks = blocks;
        PastedManager.load(this);

        PasteSchematicEvent schematicEvent = new PasteSchematicEvent(this);
        Bukkit.getPluginManager().callEvent(schematicEvent);
    }

    public PastedSchematic(ConfigurationSection section) {
        this.id = section.getString("id");
        this.center = section.getLocation("center");
        this.pastedBlocks = new HashSet<>();

        ConfigurationSection blocks = section.getConfigurationSection("blocks");
        if (blocks == null) return;

        for (String key : blocks.getKeys(false)) {
            Location location = blocks.getLocation(key + ".location");
            if (location == null) continue;

            String stringData = blocks.getString(key + ".data");
            if (stringData == null || stringData.isEmpty()) continue;

            BlockData data = Bukkit.createBlockData(stringData);
            this.pastedBlocks.add(new PastedBlock(data, location.getBlock()));
        }
    }

    public Stream<Block> getBlocks() {
        return this.pastedBlocks.stream().map(PastedBlock::nowBlock);
    }

    public void undo() {
        List<PastedBlock> blocks = new ArrayList<>(this.pastedBlocks);
        blocks.sort(Comparator.comparingInt(b -> {
                    Material m = b.previousState().getMaterial();
                    if (m.hasGravity() || !m.isSolid()) return 0;
                    if (m.name().contains("SIGN") ||
                            m.name().contains("DOOR") ||
                            m.name().contains("BUTTON") ||
                            m.name().contains("TORCH")) return 0;
                    return 1;
                })
        );
        blocks.forEach(b -> b.nowBlock().setBlockData(b.previousState()));
        PastedManager.unload(this);
    }

    public ConfigurationSection save(ConfigurationSection target) {
        target.set("center", this.center);
        target.set("id", this.id);

        ConfigurationSection blocks = target.createSection("blocks");

        long index = 0;
        for (PastedBlock block : this.pastedBlocks) {
            ConfigurationSection section = blocks.createSection(String.valueOf(index));
            section.set("location", block.nowBlock().getLocation());
            section.set("data", block.previousState().getAsString());
            index++;
        }

        return blocks;
    }
}
