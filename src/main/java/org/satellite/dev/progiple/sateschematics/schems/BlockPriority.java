package org.satellite.dev.progiple.sateschematics.schems;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class BlockPriority {
    private static final Map<Material, Integer> PRIORITY_CACHE = new ConcurrentHashMap<>();

    public int getBlockPriority(Material material) {
        return PRIORITY_CACHE.computeIfAbsent(material, m -> {
            if (m.hasGravity() || !m.isSolid()) return 0;
            String name = m.name();
            if (name.contains("SIGN") || name.contains("DOOR") ||
                    name.contains("BUTTON") || name.contains("TORCH")) {
                return -1;
            }
            return 1;
        });
    }
}
