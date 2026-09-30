package plugin.listener;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.modules.tablist.Tablist;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import plugin.TestPlugin;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TestListener implements Listener {

    private final TestPlugin plugin;

    public TestListener(TestPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        //Tablist tablist = new Tablist(azuriteLibs.getTablistModule(), e.getPlayer());
        //Bukkit.getScheduler().runTaskAsynchronously(plugin, tablist::createTablist);
    }
}
