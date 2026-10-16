package dev.azurite.libs.modules.nametags.scoreboard;

import dev.azurite.libs.modules.nametags.extra.CollisionRule;
import dev.azurite.libs.modules.nametags.extra.NametagColor;
import dev.azurite.libs.modules.nametags.extra.NametagVisibility;
import dev.azurite.libs.modules.nametags.extra.TeamAction;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class AzuriteScoreboard {

    private final Player player;
    private final Map<String, AzuriteTeam> teams;

    public AzuriteScoreboard(Player player) {
        this.player = player;
        this.teams = new ConcurrentHashMap<>();
    }

    public void delete() {
        for (AzuriteTeam team : teams.values()) {
            team.sendUpdate(TeamAction.REMOVE_TEAM);
        }
        teams.clear();
    }

    public void addAndCreateTeam(String target, String team, String prefix,
                                 String suffix, CollisionRule collisionRule, NametagColor nametagColor,
                                 NametagVisibility nametagVisibility, boolean friendlyInvis, boolean friendlyFire) {

        AzuriteTeam azuriteTeam = teams.get(team);

        // Create Team
        if (azuriteTeam == null) {
            azuriteTeam = new AzuriteTeam(team, prefix, suffix, collisionRule, nametagVisibility, nametagColor, friendlyInvis, friendlyFire, target);
            azuriteTeam.sendUpdate(TeamAction.CREATE_TEAM);
            teams.put(team, azuriteTeam);
        }

        // Update team
        if (azuriteTeam.updateValues(prefix, suffix, collisionRule, nametagVisibility, nametagColor, friendlyInvis, friendlyFire)) {
            azuriteTeam.sendUpdate(TeamAction.UPDATE_TEAM);
        }

        // Add players
        if (azuriteTeam.addPlayer(target)) {
            azuriteTeam.sendUpdate(TeamAction.ADD_PLAYERS);
        }
    }
}
