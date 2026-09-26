package dev.azurite.libs.loader;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class Module<L extends ModuleLoader> {

    protected final L loader;
    protected final JavaPlugin plugin;

    public Module(L loader) {
        this.loader = loader;
        this.plugin = loader.getPlugin();
    }

    public void onEnable() {
    }

    public void onDisable() {
    }
}
