package dev.azurite.libs.framework.module.sub;

import dev.azurite.libs.framework.module.Module;
import dev.azurite.libs.framework.module.ModuleLoader;
import dev.azurite.libs.libs.commands.api.CommandClass;
import org.bukkit.event.Listener;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class SubModule<L extends ModuleLoader, M extends Module<L>> implements CommandClass, Listener {

    protected final M module;
    protected final L loader;

    public SubModule(M module) {
        this.module = module;
        this.loader = module.getLoader();
    }

    public void enable() {
    }

    public void disable() {
    }
}
