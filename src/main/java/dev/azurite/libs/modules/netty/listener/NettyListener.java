package dev.azurite.libs.modules.netty.listener;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public interface NettyListener {

    boolean write(Object packet);

    boolean read(Object packet);

}
