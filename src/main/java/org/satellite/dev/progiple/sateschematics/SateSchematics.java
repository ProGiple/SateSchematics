package org.satellite.dev.progiple.sateschematics;

import lombok.Getter;
import org.novasparkle.lunaspring.API.commands.LunaExecutor;
import org.novasparkle.lunaspring.LunaPlugin;
import org.satellite.dev.progiple.sateschematics.schems.events.listeners.InteractHandler;
import org.satellite.dev.progiple.sateschematics.schems.events.listeners.QuitHandler;
import org.satellite.dev.progiple.sateschematics.schems.states.SchematicManager;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

public final class SateSchematics extends LunaPlugin {
    @Getter private static SateSchematics INSTANCE;
    @Getter private static UUID consoleUUID = UUID.randomUUID();

    @Override
    public void onEnable() {
        INSTANCE = this;
        super.onEnable();

        this.registerListeners(new InteractHandler(), new QuitHandler());
        this.loadSchems();

        saveDefaultConfig();
        LunaExecutor.initialize(this);
    }

    private void loadSchems() {
        File dir = new File(INSTANCE.getDataFolder(), "schematics/");
        if (!dir.exists() || !dir.isDirectory()) return;

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;

        Arrays.stream(files).forEach(SchematicManager::load);
    }
}
