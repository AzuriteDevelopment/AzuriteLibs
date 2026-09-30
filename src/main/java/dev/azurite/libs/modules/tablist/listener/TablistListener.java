package dev.azurite.libs.modules.tablist.listener;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.netty.NettyModule;
import dev.azurite.libs.modules.tablist.TablistModule;
import dev.azurite.libs.modules.tablist.injector.TablistInjector;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TablistListener extends SubModule<AzuriteLibs, TablistModule> {

    public TablistListener(TablistModule module) {
        super(module);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        NettyModule nettyModule = moduleLoader.getModule(NettyModule.class);
        nettyModule.tryInjectListener(player, new TablistInjector(), "azurite_tablist", false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        NettyModule nettyModule = moduleLoader.getModule(NettyModule.class);
        nettyModule.tryInjectListener(player, new TablistInjector(), "azurite_tablist", false);
    }
}
