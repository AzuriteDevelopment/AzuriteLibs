package dev.azurite.libs.modules.nametags.scoreboard;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import dev.azurite.libs.utils.CC;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class AzuriteLunar {

    private final NametagModule nametagModule;

    public AzuriteLunar() {
        this.nametagModule = Apollo.getModuleManager().getModule(NametagModule.class);
    }

    public boolean isRunningLunar(Player player) {
        return Apollo.getPlayerManager().hasSupport(player.getUniqueId());
    }

    public void addLunarNametag(Player viewer, Player target, List<String> nametag) {
        Collections.reverse(nametag);
        Apollo.getPlayerManager().getPlayer(viewer.getUniqueId()).ifPresent(apolloPlayer ->
                nametagModule.overrideNametag(apolloPlayer, target.getUniqueId(), Nametag.builder()
                        .lines(nametag
                                .stream()
                                .map(CC::t)
                                .map(Component::text)
                                .collect(Collectors.toList()))
                        .build()));
    }
}
