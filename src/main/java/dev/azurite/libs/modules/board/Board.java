package dev.azurite.libs.modules.board;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.nametags.scoreboard.AzuriteScoreboard;
import dev.azurite.libs.modules.versions.utils.VersionUtils;
import dev.azurite.libs.utils.CC;
import dev.azurite.libs.utils.NMSAllocator;
import dev.azurite.libs.utils.NMSUtils;
import dev.azurite.libs.utils.Triple;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class Board extends SubModule<AzuriteLibs, BoardModule> {

    private static final String OBJECTIVE_NAME = "azurite_board";
    private static final ChatColor[] COLORS = ChatColor.values();

    private static final Class<?> SCOREBOARD_OBJECTIVE_CLASS;
    private static final Class<?> SCOREBOARD_DISPLAY_OBJECTIVE_CLASS;
    private static final Class<?> SCOREBOARD_SCORE_CLASS;
    private static final Class<?> SCOREBOARD_RESET_SCORE_CLASS;

    private static final NMSAllocator.Allocator SCOREBOARD_OBJECTIVE_ALLOCATOR;

    static {
        SCOREBOARD_OBJECTIVE_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutScoreboardObjective", "ClientboundSetObjectivePacket");
        SCOREBOARD_DISPLAY_OBJECTIVE_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutScoreboardDisplayObjective", "ClientboundSetDisplayObjectivePacket");
        SCOREBOARD_SCORE_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutScoreboardScore", "ClientboundSetScorePacket");
        SCOREBOARD_RESET_SCORE_CLASS = NMSUtils.getNMSClassOrNull("network.protocol.game", "ClientboundResetScorePacket");

        SCOREBOARD_OBJECTIVE_ALLOCATOR = NMSAllocator.getAllocator(SCOREBOARD_OBJECTIVE_CLASS);
    }

    private final Player player;
    private final Map<Integer, String> scores;

    private final AzuriteScoreboard scoreboard;
    private final int version;

    private String title;
    private List<String> oldLines;
    private boolean deleted;

    public Board(BoardModule module, Player player) {
        super(module);

        this.player = player;
        this.scores = new HashMap<>();
        this.scoreboard = new AzuriteScoreboard();
        this.version = VersionUtils.getProtocolVersion(player);
        this.title = null;
        this.oldLines = null;

        this.sendUpdateObjective(module.getAdapter().getTitle(player), 0);
        this.sendCreateObjectives();
    }

    public void update() {
        List<String> lines = module.getAdapter().getLines(player);
        String title = module.getAdapter().getTitle(player);

        if (lines == null || lines.isEmpty()) {
            if (!deleted) {
                // Remove all lines
                for (int i = 0; i < oldLines.size(); i++) {
                    this.removeScore(i);
                }

                // Delete scoreboard


                this.deleted = true;
                this.oldLines = null;
            }
            return;
        }

        this.sendUpdateObjective(title, 2);

        // Remove any unused lines
        if (oldLines != null && oldLines.size() > lines.size()) {
            for (int i = lines.size(); i < oldLines.size(); i++) {
                this.removeScore(i);
            }
        }

        // Update lines
        Collections.reverse(lines);

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            this.updateScore(i, line);
        }

        this.deleted = false;
        this.oldLines = lines;
    }

    private void sendUpdateObjective(String newTitle, int mode) {
        if (title != null && title.equals(newTitle)) {
            return;
        }

        newTitle = CC.t(newTitle);
        this.title = newTitle;

        PacketPlayOutScoreboardObjective objective = new PacketPlayOutScoreboardObjective(OBJECTIVE_NAME, newTitle, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER, mode);
        NMSUtils.sendPacket(player, objective);
    }

    private void sendCreateObjectives() {
        PacketPlayOutScoreboardDisplayObjective displayObjective = new PacketPlayOutScoreboardDisplayObjective(1, OBJECTIVE_NAME);
        NMSUtils.sendPacket(player, displayObjective);
    }

    private void updateScore(int index, String line) {
        Triple<String, String, String> formatted = getScoreFormat(line, index);

        String prefix = formatted.getLeft();
        String entry = formatted.getMiddle();
        String suffix = formatted.getRight();

        String oldScore = scores.get(index);
        String newScore = entry != null ? entry : COLORS[index].toString();

        if (oldScore == null || !oldScore.equals(newScore)) {
            if (oldScore != null) {
                scoreboard.removeFromTeam(player, oldScore, oldScore);
                this.removeScore(index, oldScore);
            }

            scores.put(index, newScore);
        }

        PacketPlayOutScoreboardScore scoreboardScore = new PacketPlayOutScoreboardScore(OBJECTIVE_NAME, newScore, PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE, index);
        NMSUtils.sendPacket(player, scoreboardScore);
        scoreboard.addAndCreateTeam(player, newScore, newScore, prefix, suffix);
    }

    private void removeScore(int index) {
        this.removeScore(index, scores.get(index));
    }

    private void removeScore(int index, String score) {
        if (score == null) {
            throw new NullPointerException("Trying to send remove score packet for line that does not exist: " + index);
        }
        PacketPlayOutScoreboardScore scoreboardScore = new PacketPlayOutScoreboardScore(OBJECTIVE_NAME, score, PacketPlayOutScoreboardScore.EnumScoreboardAction.REMOVE, index);
        NMSUtils.sendPacket(player, scoreboardScore);
    }

    private Triple<String, String, String> getScoreFormat(String line, int index) {
        int maxLength = version <= 340 ? 16 : 64;
        int lineLength = line.length();

        String prefix;
        String suffix;
        String entry = null;

        // We can just use prefix
        if (lineLength <= maxLength) {
            prefix = line;
            suffix = "";

            // We can use prefix + suffix
        } else if (lineLength <= (maxLength * 2)) {
            prefix = line.substring(0, maxLength);
            suffix = line.substring(maxLength);

            if (prefix.endsWith("§")) {
                prefix = line.substring(0, maxLength - 1);
                suffix = line.substring(maxLength - 1);
            }

            suffix = getLastColorsIfPresent(prefix) + suffix;
            prefix = cutStringIfOverLimit(maxLength, prefix);
            suffix = cutStringIfOverLimit(maxLength, suffix);

            // Use entry name to allow for 48 chars
        } else {
            prefix = line.substring(0, maxLength);
            suffix = line.substring(maxLength * 2);
            entry = line.substring(maxLength, maxLength * 2);

            // If a duplicate is present we add a random color at the start
            for (Map.Entry<Integer, String> scoreEntry : scores.entrySet()) {
                if (scoreEntry.getKey() != index && scoreEntry.getValue().equals(entry)) {
                    entry = cutStringIfOverLimit(maxLength - 2, entry); // Cut to - 2 so we can accommodate for the colors added
                    entry = entry + COLORS[index];
                }
            }

            prefix = cutStringIfOverLimit(maxLength, prefix);
            suffix = cutStringIfOverLimit(maxLength, suffix);
            entry = cutStringIfOverLimit(maxLength, entry);
        }

        return new Triple<>(prefix, entry, suffix);
    }

    private String getLastColorsIfPresent(String string) {
        String lastColors = ChatColor.getLastColors(string);
        return lastColors.isEmpty() ? "§r" : lastColors;
    }

    private String cutStringIfOverLimit(int maxLength, String string) {
        if (string.length() > maxLength) {
            return string.substring(0, maxLength);
        }
        return string;
    }
}
