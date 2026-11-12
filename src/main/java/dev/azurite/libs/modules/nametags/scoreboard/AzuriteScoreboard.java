package dev.azurite.libs.modules.nametags.scoreboard;

import dev.azurite.libs.modules.nametags.enums.CollisionRule;
import dev.azurite.libs.modules.nametags.enums.NametagColor;
import dev.azurite.libs.modules.nametags.enums.NametagVisibility;
import dev.azurite.libs.modules.nametags.enums.TeamAction;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class AzuriteScoreboard {

    private final Map<String, AzuriteTeam> teams;

    public AzuriteScoreboard() {
        this.teams = new ConcurrentHashMap<>();
    }

    public void removeFromTeam(Player player, String target, String team) {
        AzuriteTeam azuriteTeam = teams.get(team);

        if (azuriteTeam != null && azuriteTeam.getPlayers().remove(target)) {
            azuriteTeam.sendUpdate(player, TeamAction.REMOVE_PLAYERS, Collections.singletonList(target));
        }
    }

    public void addAndCreateTeam(Player player, String target, String team, String prefix, String suffix) {
        this.addAndCreateTeam(player, target, team, prefix, suffix, NametagVisibility.ALWAYS, CollisionRule.ALWAYS, NametagColor.NONE, true, false);
    }

    public void addAndCreateTeam(Player player, String target, String team, String prefix,
                                 String suffix, NametagVisibility nametagVisibility, CollisionRule collisionRule,
                                 NametagColor nametagColor, boolean friendlyInvis, boolean friendlyFire) {

        AzuriteTeam azuriteTeam = teams.get(team);

        // Remove from old teams
        for (AzuriteTeam other : teams.values()) {
            if (!other.equals(azuriteTeam)) {
                other.getPlayers().remove(target);
            }
        }

        // Create Team
        if (azuriteTeam == null) {
            azuriteTeam = new AzuriteTeam(team, prefix, suffix, collisionRule, nametagVisibility, nametagColor, friendlyInvis, friendlyFire, target);
            azuriteTeam.sendUpdate(player, TeamAction.CREATE_TEAM);
            teams.put(team, azuriteTeam);
        }

        // Update team
        if (azuriteTeam.updateValues(prefix, suffix, collisionRule, nametagVisibility, nametagColor, friendlyInvis, friendlyFire)) {
            azuriteTeam.sendUpdate(player, TeamAction.UPDATE_TEAM);
        }

        // Add players
        if (azuriteTeam.addPlayer(target)) {
            azuriteTeam.sendUpdate(player, TeamAction.ADD_PLAYERS, Collections.singletonList(target));
        }
    }
}
