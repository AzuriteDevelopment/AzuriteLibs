package dev.azurite.libs.loader.sub;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.Module;
import dev.azurite.libs.loader.ModuleLoader;
import dev.azurite.libs.modules.commands.CommandModule;
import dev.azurite.libs.modules.commands.command.api.CommandClass;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class SubModule<L extends ModuleLoader, M extends Module<L>> implements CommandClass, Listener {

    protected final M module;
    protected final L moduleLoader;
    protected final JavaPlugin plugin;

    public SubModule(M module) {
        this.module = module;
        this.moduleLoader = module.getLoader();
        this.plugin = module.getPlugin();
    }

    public void registerAsCommand(AzuriteLibs azuriteLibs, String prefix) {
        azuriteLibs.getModule(CommandModule.class).registerCommand(prefix, this);
    }

    public void registerAsListener() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
