package dev.azurite.libs.loader;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public abstract class ModuleLoader {

    protected final JavaPlugin plugin;

    public ModuleLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }
}
