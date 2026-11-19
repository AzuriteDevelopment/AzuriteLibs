package dev.azurite.libs.framework.lib.sub;

import dev.azurite.libs.framework.lib.Lib;
import dev.azurite.libs.framework.lib.LibLoader;
import dev.azurite.libs.libs.commands.api.CommandClass;
import lombok.Getter;
import org.bukkit.event.Listener;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class SubLib<L extends LibLoader, M extends Lib<L>> implements CommandClass, Listener {

    protected final M module;
    protected final L loader;

    public SubLib(M module) {
        this.module = module;
        this.loader = module.getLoader();
    }
}

