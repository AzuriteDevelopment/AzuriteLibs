package dev.azurite.libs.modules.nametags;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.nametags.enums.CollisionRule;
import dev.azurite.libs.modules.nametags.enums.NametagColor;
import dev.azurite.libs.modules.nametags.enums.NametagVisibility;
import dev.azurite.libs.modules.nametags.enums.TeamAction;
import dev.azurite.libs.modules.nametags.scoreboard.AzuriteLunar;
import dev.azurite.libs.modules.nametags.scoreboard.AzuriteScoreboard;
import dev.azurite.libs.modules.nametags.scoreboard.AzuriteTeam;
import dev.azurite.libs.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
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

    public void addAndCreateTeam(Player target, String team, NametagColor color) {
        this.addAndCreateTeam(target, team, "", "", NametagVisibility.ALWAYS, CollisionRule.NEVER, color, true, false);
    }

    public void addAndCreateTeam(Player target, String team, String prefix, NametagColor color) {
        this.addAndCreateTeam(target, team, prefix, "", NametagVisibility.ALWAYS, CollisionRule.NEVER, color, true, false);
    }

    public void addAndCreateTeam(Player target, String team, String prefix, String suffix) {
        this.addAndCreateTeam(target, team, prefix, suffix, NametagVisibility.ALWAYS, CollisionRule.NEVER, NametagColor.NONE, true, false);
    }

    public void addAndCreateTeam(Player target, String team, String prefix, String suffix, NametagColor color) {
        this.addAndCreateTeam(target, team, prefix, suffix, NametagVisibility.ALWAYS, CollisionRule.NEVER, color, true, false);
    }

    public void addAndCreateTeam(Player target, String team, String prefix, String suffix, NametagVisibility visibility) {
        this.addAndCreateTeam(target, team, prefix, suffix, visibility, CollisionRule.NEVER, NametagColor.NONE, true, false);
    }

    public void addAndCreateTeam(Player target, String team, String prefix, String suffix, NametagVisibility visibility, CollisionRule rule, NametagColor color) {
        this.addAndCreateTeam(target, team, prefix, suffix, visibility, rule, color, true, false);
    }

    public void addAndCreateTeam(Player target, String team, String prefix, String suffix, NametagVisibility visibility, CollisionRule rule, NametagColor color, boolean friendlyInvis, boolean friendlyFire) {
        scoreboard.addAndCreateTeam(player, target.getName(), team, CC.t(prefix), CC.t(suffix), visibility, rule, color, friendlyInvis, friendlyFire);
    }

    public void addLunarNametag(Player target, List<String> nametag) {
        if (lunar != null) {
            lunar.addLunarNametag(player, target, CC.t(nametag));
        }
    }

    public void update(Player viewer, Player target) {
        module.getAdapter().updateNametag(this, viewer, target);
    }

    public void delete() {
        Map<String, AzuriteTeam> teams = scoreboard.getTeams();

        for (AzuriteTeam team : teams.values()) {
            team.sendUpdate(player, TeamAction.REMOVE_TEAM);
        }

        teams.clear();
    }
}
