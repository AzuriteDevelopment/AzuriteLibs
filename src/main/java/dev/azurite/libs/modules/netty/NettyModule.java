package dev.azurite.libs.modules.netty;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.Module;
import dev.azurite.libs.modules.netty.injectors.ModernNetty;
import dev.azurite.libs.modules.netty.listener.NettyListener;
import dev.azurite.libs.utils.NMSUtils;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NettyModule extends Module<AzuriteLibs> {

    private final Netty netty;

    public NettyModule(AzuriteLibs loader) {
        super(loader);
        this.netty = new ModernNetty();
    }

    public void tryInjectListener(Player player, NettyListener listener, String name, boolean overwrite) {
        Object channel = NMSUtils.getChannel(player);

        if (channel != null) {
            netty.inject(player, listener, name, channel, overwrite);
        }
    }
}
