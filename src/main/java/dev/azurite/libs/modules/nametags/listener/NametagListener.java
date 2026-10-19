package dev.azurite.libs.modules.nametags.listener;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.nametags.NametagModule;
import dev.azurite.libs.modules.nametags.enums.CollisionRule;
import dev.azurite.libs.modules.nametags.enums.NametagColor;
import dev.azurite.libs.modules.nametags.enums.NametagVisibility;
import dev.azurite.libs.modules.nametags.scoreboard.AzuriteScoreboard;
import dev.azurite.libs.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NametagListener extends SubModule<AzuriteLibs, NametagModule> {

    public NametagListener(NametagModule module) {
        super(module);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        AzuriteScoreboard azuriteScoreboard = new AzuriteScoreboard(player);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            NametagColor[] colors = NametagColor.values();

            int prefix = ThreadLocalRandom.current().nextInt(0, 100);
            int suffix = ThreadLocalRandom.current().nextInt(0, 100);

            azuriteScoreboard.addAndCreateTeam(player.getName(), "test", CC.t("&e&l[" + prefix + "] "), CC.t(" &e&l[" + suffix + "] "), NametagVisibility.ALWAYS, CollisionRule.ALWAYS, colors[ThreadLocalRandom.current().nextInt(colors.length - 1)], true, true);

        }, 0L, 20 * 5L);
    }
}
