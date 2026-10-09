package dev.azurite.libs.modules.tablist.listener;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.tablist.Tablist;
import dev.azurite.libs.modules.tablist.TablistModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

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
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (module.isEnabled()) {
            Tablist tablist = new Tablist(module, player);
            module.getTablists().put(player.getUniqueId(), tablist);
            moduleLoader.getNettyModule().tryInjectListener(player, tablist.getInjector(), "azurite_tablist", false);
        }
    }
}
