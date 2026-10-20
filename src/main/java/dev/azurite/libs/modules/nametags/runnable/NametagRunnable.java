package dev.azurite.libs.modules.nametags.runnable;

import dev.azurite.libs.modules.nametags.Nametag;
import dev.azurite.libs.modules.nametags.NametagModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NametagRunnable implements Runnable {

    private final NametagModule module;

    public NametagRunnable(NametagModule module) {
        this.module = module;
    }

    @Override
    public void run() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for (Player viewer : players) {
            Nametag nametag = module.getCachedData(viewer.getUniqueId());

            if (nametag != null) {
                for (Player target : players) {
                    nametag.update(viewer, target);
                }
            }
        }
    }
}
