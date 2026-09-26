package dev.azurite.libs;

import dev.azurite.libs.loader.ModuleLoader;
import dev.azurite.libs.modules.commands.CommandModule;
import dev.azurite.libs.modules.tablist.TablistModule;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class AzuriteLibs extends ModuleLoader {

    public AzuriteLibs(JavaPlugin plugin) {
        super(plugin);
        this.loadModules();
    }

    private void loadModules() {
        Arrays.asList(
                new CommandModule(this),
                new TablistModule(this)
        ).forEach(manager -> modules.put(manager.getClass(), manager));
    }
}
