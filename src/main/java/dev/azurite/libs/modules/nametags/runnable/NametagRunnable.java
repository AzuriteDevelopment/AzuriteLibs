package dev.azurite.libs.modules.nametags.runnable;

import dev.azurite.libs.modules.nametags.Nametag;
import dev.azurite.libs.modules.nametags.NametagModule;
import dev.azurite.libs.modules.nametags.adapter.NametagAdapter;
import dev.azurite.libs.modules.nametags.scoreboard.AzuriteLunar;
import dev.azurite.libs.modules.nametags.update.NametagUpdate;
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

    private final NametagModule module;

    public NametagRunnable(NametagModule module) {
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
