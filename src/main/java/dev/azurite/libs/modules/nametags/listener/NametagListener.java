package dev.azurite.libs.modules.nametags.listener;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.nametags.Nametag;
import dev.azurite.libs.modules.nametags.NametagModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NametagListener extends SubModule<AzuriteLibs, NametagModule> {

    public NametagListener(NametagModule module) {
        super(module);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (module.isEnabled()) {
            Nametag nametag = new Nametag(module, player);
            module.getCachedData().put(player.getUniqueId(), nametag);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.handleQuit(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        this.handleQuit(e.getPlayer());
    }

    private void handleQuit(Player player) {
        Nametag nametag = module.getCachedData().remove(player.getUniqueId());

        if (nametag != null) {
            nametag.delete();
        }
    }
}
