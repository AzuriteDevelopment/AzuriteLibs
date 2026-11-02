package dev.azurite.libs.modules.nametags;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.nametags.enums.TeamAction;
import dev.azurite.libs.modules.nametags.scoreboard.AzuriteLunar;
import dev.azurite.libs.modules.nametags.scoreboard.AzuriteScoreboard;
import dev.azurite.libs.modules.nametags.scoreboard.AzuriteTeam;
import dev.azurite.libs.modules.nametags.update.NametagUpdate;
import dev.azurite.libs.utils.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class Nametag extends SubModule<AzuriteLibs, NametagModule> {

    private final Player player;
    private final AzuriteScoreboard scoreboard;
    private final AzuriteLunar lunar;

    public Nametag(NametagModule module, Player player) {
        super(module);
        this.player = player;
        this.scoreboard = new AzuriteScoreboard();
        this.lunar = Bukkit.getPluginManager().isPluginEnabled("Apollo-Bukkit") ? new AzuriteLunar() : null;
    }

    public void update(NametagUpdate update, Player target) {
        scoreboard.addAndCreateTeam(player, target.getName(),
                update.getTeam(), CC.t(update.getPrefix()), CC.t(update.getSuffix()),
                update.getNametagVisibility(), update.getCollisionRule(), update.getNametagColor(),
                update.isFriendlyInvis(), update.isFriendlyFire()
        );
    }

    public void delete() {
        Map<String, AzuriteTeam> teams = scoreboard.getTeams();

        for (AzuriteTeam team : teams.values()) {
            team.sendUpdate(player, TeamAction.REMOVE_TEAM);
        }

        teams.clear();
    }
}
