package dev.azurite.libs.modules.netty.listener;

import org.bukkit.entity.Player;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public interface NettyListener {

    default boolean preWrite(Player player, Object packet) {
        return true;
    }

    default boolean preRead(Player player, Object packet) {
        return true;
    }

    default void postWrite(Player player, Object packet) {
    }

    default void postRead(Player player, Object packet) {
    }
}
