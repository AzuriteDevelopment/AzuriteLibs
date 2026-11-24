package dev.azurite.libs.framework.module;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.utils.yaml.ConfigFile;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public abstract class ModuleLoader extends JavaPlugin {

    protected final AzuriteLibs azuriteLibs;
    protected final List<ConfigFile> configs;
    protected final List<Module<?>> modules;

    public ModuleLoader() {
        this.azuriteLibs = new AzuriteLibs(this);
        this.configs = new ArrayList<>();
        this.modules = new ArrayList<>();
    }

    public void registerConfigs(ConfigFile... configs) {
        this.configs.addAll(Arrays.asList(configs));
    }

    public void registerModules(Module<?>... modules) {
        this.modules.addAll(Arrays.asList(modules));
    }

    public void loadConfigs() {
        this.configs.forEach(ConfigFile::load);
    }

    public void saveConfigs() {
        this.configs.forEach(ConfigFile::save);
    }

    public void loadModules() {
        this.modules.forEach(Module::enable);
    }

    public void unloadModules() {
        this.modules.forEach(Module::disable);
    }
}
