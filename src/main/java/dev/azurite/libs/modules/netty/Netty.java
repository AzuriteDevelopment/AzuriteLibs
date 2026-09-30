package dev.azurite.libs.modules.netty;

import dev.azurite.libs.modules.netty.listener.NettyListener;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public abstract class Netty {

    public abstract void inject(Player player, NettyListener listener, String name, Object channel, boolean overwrite);

}
