package dev.azurite.libs.framework.module;

import dev.azurite.libs.AzuriteLibs;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public abstract class ModuleLoader extends JavaPlugin {

    protected final AzuriteLibs azuriteLibs;
    protected final List<Module<?>> modules;

    public ModuleLoader(AzuriteLibs azuriteLibs) {
        this.azuriteLibs = azuriteLibs;
        this.modules = new ArrayList<>();
    }

    public void registerModules(Module<?>... modules) {
        for (Module<?> module : modules) {
            this.registerModule(module);
        }
    }

    public void registerModule(Module<?> module) {
        modules.add(module);
    }

    public void unregisterModule(Module<?> module) {
        modules.remove(module);
    }

    public void enableModules() {
        this.modules.forEach(Module::enable);
    }

    public void disableModules() {
        this.modules.forEach(Module::disable);
    }
}
