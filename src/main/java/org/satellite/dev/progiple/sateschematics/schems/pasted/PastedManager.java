package org.satellite.dev.progiple.sateschematics.schems.pasted;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.satellite.dev.progiple.sateschematics.schems.SchemBlock;
import org.satellite.dev.progiple.sateschematics.schems.YAMLSchematic;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@UtilityClass
public class PastedManager {
    private final Set<PastedSchematic> schematics = new HashSet<>();

    public PastedSchematic paste(YAMLSchematic schematic, Location center, Function<SchemBlock, Boolean> filter) {
        return schematic.paste(center, filter);
    }

    public void load(PastedSchematic schematic) {
        schematics.add(schematic);
    }

    public void unload(PastedSchematic schematic) {
        schematics.remove(schematic);
    }

    public Stream<PastedSchematic> getSchems(Location center) {
        return schematics.stream().filter(s -> s.getCenter().equals(center));
    }

    public Stream<PastedSchematic> getSchems(String id) {
        return schematics.stream().filter(s -> s.getId().equalsIgnoreCase(id));
    }
}
