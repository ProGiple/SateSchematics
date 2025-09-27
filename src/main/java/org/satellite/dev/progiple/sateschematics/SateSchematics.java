package org.satellite.dev.progiple.sateschematics;

import lombok.Getter;
import org.novasparkle.lunaspring.API.commands.CommandInitializer;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.sateschematics.schems.events.listeners.InteractHandler;
import org.satellite.dev.progiple.sateschematics.schems.events.listeners.QuitHandler;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

@Getter
public final class SateSchematics extends LunaPlugin {
    @Getter private static SateSchematics INSTANCE;
    @Getter private static UUID consoleUUID = UUID.randomUUID();

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        super.onEnable();

        this.registerListeners(new InteractHandler(), new QuitHandler());
        this.loadSchems();

        CommandInitializer.initialize(this);
    }

    public void loadSchems() {
        File schemDir = new File(SateSchematics.getINSTANCE().getDataFolder(), "schematics/");
        if (!schemDir.exists() || !schemDir.isDirectory()) return;

        File[] files = schemDir.listFiles();
        if (files == null || files.length == 0) return;

        Arrays.stream(files).forEach(SchematicManager::load);
    }
}
