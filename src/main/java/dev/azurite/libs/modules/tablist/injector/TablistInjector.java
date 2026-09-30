package dev.azurite.libs.modules.tablist.injector;

import dev.azurite.libs.modules.netty.listener.NettyListener;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TablistInjector implements NettyListener {

    @Override
    public boolean write(Object packet) {
        return true;
    }

    @Override
    public boolean read(Object packet) {
        return true;
    }
}
