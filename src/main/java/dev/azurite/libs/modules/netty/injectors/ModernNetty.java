package dev.azurite.libs.modules.netty.injectors;

import dev.azurite.libs.modules.netty.Netty;
import dev.azurite.libs.modules.netty.listener.NettyListener;
import io.netty.channel.*;
import org.bukkit.entity.Player;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ModernNetty extends Netty {

    @Override
    public void inject(Player player, NettyListener listener, String name, Object channel, boolean overwrite) {
        Channel nettyChannel = (Channel) channel;
        ChannelPipeline pipeline = nettyChannel.pipeline();
        ChannelHandler handler = pipeline.get(name);

        // Not initialized yet
        if (pipeline.get("packet_handler") == null) {
            return;
        }

        if (handler != null && overwrite) {
            handler = null;
            pipeline.remove(name);
        }

        if (handler == null) {
            pipeline.addBefore("packet_handler", name, new ChannelDuplexHandler() {
                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    boolean write = listener.write(player, msg);

                    if (write) {
                        super.write(ctx, msg, promise);
                    }
                }

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    boolean read = listener.read(player, msg);

                    if (read) {
                        super.channelRead(ctx, msg);
                    }
                }
            });
        }
    }
}
