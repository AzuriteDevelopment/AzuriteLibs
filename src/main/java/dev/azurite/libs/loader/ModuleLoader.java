package dev.azurite.libs.loader;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@SuppressWarnings("unchecked")
public abstract class ModuleLoader {

    protected final JavaPlugin plugin;
    protected final Map<Class<?>, Module<?>> modules;

    public ModuleLoader(JavaPlugin plugin) {
        this.plugin = plugin;
        this.modules = new HashMap<>();
    }

    public <T> T getModule(Class<T> moduleClass) {
        return (T) modules.get(moduleClass);
    }
}
