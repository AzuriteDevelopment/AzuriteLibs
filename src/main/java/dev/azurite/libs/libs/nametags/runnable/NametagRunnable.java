package dev.azurite.libs.libs.nametags.runnable;

import dev.azurite.libs.libs.nametags.Nametag;
import dev.azurite.libs.libs.nametags.NametagLib;
import dev.azurite.libs.libs.nametags.adapter.NametagAdapter;
import dev.azurite.libs.libs.nametags.lunar.AzuriteLunar;
import dev.azurite.libs.libs.nametags.update.NametagUpdate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NametagRunnable implements Runnable {

    private final NametagLib module;

    public NametagRunnable(NametagLib module) {
        this.module = module;
    }

    @Override
    public void run() {
        NametagAdapter adapter = module.getAdapter();
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for (Player viewer : players) {
            Nametag nametag = module.getCachedData(viewer.getUniqueId());

            if (nametag == null) continue;

            AzuriteLunar lunar = nametag.getLunar();

            for (Player target : players) {
                List<String> lunarNametag = adapter.getLunarNametags(viewer, target);

                if (lunar != null && lunarNametag != null && lunar.isRunningLunar(viewer)) {
                    lunar.addLunarNametag(viewer, target, lunarNametag);

                } else {
                    NametagUpdate update = adapter.getNametagUpdate(viewer, target);

                    if (update != null) {
                        nametag.update(update, target);
                    }
                }
            }
        }
    }
}
