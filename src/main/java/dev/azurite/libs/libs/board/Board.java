package dev.azurite.libs.libs.board;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.framework.lib.sub.SubLib;
import dev.azurite.libs.libs.board.enums.ObjectiveAction;
import dev.azurite.libs.libs.board.enums.ScoreAction;
import dev.azurite.libs.libs.nametags.scoreboard.AzuriteScoreboard;
import dev.azurite.libs.utils.Triple;
import dev.azurite.libs.utils.nms.NMSAllocator;
import dev.azurite.libs.utils.nms.NMSUtils;
import dev.azurite.libs.utils.version.VersionUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class Board extends SubLib<AzuriteLibs, BoardLib> {

    private static final String OBJECTIVE_NAME = "azurite_board";
    private static final ChatColor[] COLORS = ChatColor.values();

    private static final Class<?> SCOREBOARD_OBJECTIVE_CLASS;
    private static final Class<?> SCOREBOARD_DISPLAY_OBJECTIVE_CLASS;
    private static final Class<?> SCOREBOARD_SCORE_CLASS;
    private static final Class<?> SCOREBOARD_SERVER_CLASS;
    private static final Class<?> SCOREBOARD_RESET_SCORE_CLASS;
    private static final Class<?> SCOREBOARD_SCORE_ACTION_CLASS;
    private static final Class<?> DISPLAY_SLOT_CLASS;

    private static final Enum<?> SCOREBOARD_RENDER_TYPE_ENUM;
    private static final Enum<?> DISPLAY_SLOT_ENUM;

    private static final Constructor<?> SCOREBOARD_SCORE_CONSTRUCTOR;
    private static final Constructor<?> SCOREBOARD_RESET_SCORE_CONSTRUCTOR;

    private static final NMSAllocator.Allocator SCOREBOARD_OBJECTIVE_ALLOCATOR;
    private static final NMSAllocator.Allocator SCOREBOARD_DISPLAY_OBJECTIVE_ALLOCATOR;

    private static final Field[] SCOREBOARD_OBJECTIVE_FIELDS;
    private static final Field[] SCOREBOARD_SCORE_FIELDS;
    private static final Field[] SCOREBOARD_DISPLAY_OBJECTIVE_FIELDS;

    static {
        // Classes
        SCOREBOARD_OBJECTIVE_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutScoreboardObjective", "ClientboundSetObjectivePacket");
        SCOREBOARD_DISPLAY_OBJECTIVE_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutScoreboardDisplayObjective", "ClientboundSetDisplayObjectivePacket");
        SCOREBOARD_SCORE_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutScoreboardScore", "ClientboundSetScorePacket");
        SCOREBOARD_SERVER_CLASS = NMSUtils.getNMSClassOrNull(null, "ScoreboardServer");
        SCOREBOARD_RESET_SCORE_CLASS = NMSUtils.getNMSClassOrNull("network.protocol.game", "ClientboundResetScorePacket");
        SCOREBOARD_SCORE_ACTION_CLASS = NMSUtils.getNMSSubClass(null, true, 0, SCOREBOARD_SERVER_CLASS != null && SCOREBOARD_SERVER_CLASS.getEnumConstants() != null ? SCOREBOARD_SERVER_CLASS : SCOREBOARD_SCORE_CLASS, "EnumScoreboardAction", "Action");
        DISPLAY_SLOT_CLASS = NMSUtils.getNMSClassOrNull("world.scores", "DisplaySlot");

        // Enums
        SCOREBOARD_RENDER_TYPE_ENUM = NMSUtils.findEnumConstant(NMSUtils.getNMSClass("world.scores.criteria", "IScoreboardCriteria$EnumScoreboardHealthDisplay", "ObjectiveCriteria$RenderType"), 0);
        DISPLAY_SLOT_ENUM = DISPLAY_SLOT_CLASS != null ? NMSUtils.findEnumConstant(DISPLAY_SLOT_CLASS, 1) : null;

        // Constructors
        Constructor<?> firstConstructor = NMSUtils.getConstructorOrNull(SCOREBOARD_SCORE_CLASS, SCOREBOARD_SCORE_ACTION_CLASS, String.class, String.class, int.class);
        Constructor<?> secondConstructor = NMSUtils.streamFindConstructor(SCOREBOARD_SCORE_CLASS, constructor -> constructor.getParameterCount() == 5, true, null);
        SCOREBOARD_SCORE_CONSTRUCTOR = firstConstructor != null ? firstConstructor : secondConstructor != null ? secondConstructor : NMSUtils.getConstructorOrNull(SCOREBOARD_SCORE_CLASS);
        SCOREBOARD_RESET_SCORE_CONSTRUCTOR = SCOREBOARD_RESET_SCORE_CLASS != null ? NMSUtils.getConstructorOrNull(SCOREBOARD_RESET_SCORE_CLASS, String.class, String.class) : null;

        // Allocator
        SCOREBOARD_OBJECTIVE_ALLOCATOR = NMSAllocator.getAllocator(SCOREBOARD_OBJECTIVE_CLASS);
        SCOREBOARD_DISPLAY_OBJECTIVE_ALLOCATOR = NMSAllocator.getAllocator(SCOREBOARD_DISPLAY_OBJECTIVE_CLASS);

        // Fields
        SCOREBOARD_OBJECTIVE_FIELDS = NMSUtils.streamFieldsFindAllType(SCOREBOARD_OBJECTIVE_CLASS, null, false, true, false);
        SCOREBOARD_SCORE_FIELDS = NMSUtils.streamFieldsFindAllType(SCOREBOARD_SCORE_CLASS, null, false, true, false);
        SCOREBOARD_DISPLAY_OBJECTIVE_FIELDS = NMSUtils.streamFieldsFindAllType(SCOREBOARD_DISPLAY_OBJECTIVE_CLASS, null, false, true, false);
    }

    private final Player player;
    private final Map<Integer, String> scores;
    private final AzuriteScoreboard scoreboard;
    private final int version;

    private String oldTitle;
    private List<String> oldLines;
    private boolean deleted;

    public Board(BoardLib module, Player player) {
        super(module);

        this.player = player;
        this.scores = new HashMap<>();
        this.scoreboard = new AzuriteScoreboard();
        this.version = VersionUtils.getProtocolVersion(player);

        this.oldTitle = null;
        this.oldLines = null;
        this.deleted = true;
    }

    public void update() {
        List<String> lines = module.getAdapter().getLines(player);
        String title = module.getAdapter().getTitle(player);

        // Delete if lines are null or empty
        if (lines == null || lines.isEmpty()) {
            if (!deleted) {
                // Remove all lines
                for (int i = 0; i < oldLines.size(); i++) {
                    String score = scores.get(i);
                    this.sendScore(score, i, ScoreAction.REMOVE);
                }

                // Delete scoreboard
                this.sendObjective(title, ObjectiveAction.REMOVE);
                this.deleted = true;
                this.oldLines = null;
            }
            return;
        }

        // If deleted or did not exist we recreate
        if (deleted) {
            this.deleted = false;
            this.sendObjective(title, ObjectiveAction.CREATE);
            this.sendDisplayObjective();
        }

        // Update title
        this.sendObjective(title, ObjectiveAction.UPDATE);

        // Remove any unused lines
        if (oldLines != null && oldLines.size() > lines.size()) {
            for (int i = lines.size(); i < oldLines.size(); i++) {
                String score = scores.get(i);
                this.sendScore(score, i, ScoreAction.REMOVE);
            }
        }

        // Update lines
        Collections.reverse(lines);

        for (int i = 0; i < lines.size(); i++) {
            String score = lines.get(i);
            Triple<String, String, String> formatted = getScoreFormat(score, i);

            String prefix = formatted.getLeft();
            String entry = formatted.getMiddle();
            String suffix = formatted.getRight();

            String oldScore = scores.get(i);
            String newScore = entry != null ? entry : COLORS[i].toString();

            // If the old score existed, and it's a custom score or different we remove to change/send again
            if (oldScore == null || !oldScore.equals(newScore)) {
                if (oldScore != null) {
                    scoreboard.removeFromTeam(player, oldScore, oldScore);
                    this.sendScore(oldScore, i, ScoreAction.REMOVE);
                }

                scores.put(i, newScore);
            }

            // Send update and prefix/suffix
            this.sendScore(newScore, i, ScoreAction.CHANGE);
            scoreboard.addAndCreateTeam(player, newScore, newScore, prefix, suffix);
        }

        this.deleted = false;
        this.oldLines = lines;
    }

    private void sendObjective(String title, ObjectiveAction action) {
        try {
            if (oldTitle != null && oldTitle.equals(title) && action == ObjectiveAction.UPDATE) {
                return;
            }

            Object packet = SCOREBOARD_OBJECTIVE_ALLOCATOR.allocate();
            int actionField = SCOREBOARD_OBJECTIVE_FIELDS.length == 5 ? 4 : 3;

            NMSUtils.setField(packet, SCOREBOARD_OBJECTIVE_FIELDS[0], OBJECTIVE_NAME);
            NMSUtils.setField(packet, SCOREBOARD_OBJECTIVE_FIELDS[actionField], action.getId());

            if (action == ObjectiveAction.CREATE || action == ObjectiveAction.UPDATE) {
                NMSUtils.setField(packet, SCOREBOARD_OBJECTIVE_FIELDS[1], title);
                NMSUtils.setField(packet, SCOREBOARD_OBJECTIVE_FIELDS[2], SCOREBOARD_RENDER_TYPE_ENUM);

                if (actionField == 4) {
                    NMSUtils.setField(packet, SCOREBOARD_OBJECTIVE_FIELDS[3], null);
                }
            }

            this.oldTitle = title;
            NMSUtils.sendPacket(player, packet);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void sendDisplayObjective() {
        try {

            Object packet = SCOREBOARD_DISPLAY_OBJECTIVE_ALLOCATOR.allocate();

            SCOREBOARD_DISPLAY_OBJECTIVE_FIELDS[0].set(packet, DISPLAY_SLOT_ENUM != null ? DISPLAY_SLOT_ENUM : 1);
            SCOREBOARD_DISPLAY_OBJECTIVE_FIELDS[1].set(packet, OBJECTIVE_NAME);

            NMSUtils.sendPacket(player, packet);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void sendScore(String score, int index, ScoreAction action) {
        try {
            // Use reset score packet for versions that have it
            if (SCOREBOARD_RESET_SCORE_CLASS != null && action == ScoreAction.REMOVE) {
                Object packet = SCOREBOARD_RESET_SCORE_CONSTRUCTOR.newInstance(score, OBJECTIVE_NAME);
                NMSUtils.sendPacket(player, packet);
                return;
            }

            // Different constructors throughout the versions
            int count = SCOREBOARD_SCORE_CONSTRUCTOR.getParameterCount();
            Object packet;

            if (count == 0) {
                Object actionEnum = NMSUtils.findEnumConstant(SCOREBOARD_SCORE_ACTION_CLASS, action.getId());
                packet = SCOREBOARD_SCORE_CONSTRUCTOR.newInstance();

                NMSUtils.setField(packet, SCOREBOARD_SCORE_FIELDS[0], score);
                NMSUtils.setField(packet, SCOREBOARD_SCORE_FIELDS[1], OBJECTIVE_NAME);
                NMSUtils.setField(packet, SCOREBOARD_SCORE_FIELDS[2], index);
                NMSUtils.setField(packet, SCOREBOARD_SCORE_FIELDS[3], actionEnum);

            } else if (count == 4) {
                Object actionEnum = NMSUtils.findEnumConstant(SCOREBOARD_SCORE_ACTION_CLASS, action.getId());
                packet = SCOREBOARD_SCORE_CONSTRUCTOR.newInstance(actionEnum, OBJECTIVE_NAME, score, index);

            } else {
                Object component = SCOREBOARD_SCORE_CONSTRUCTOR.getParameterTypes()[3] == Optional.class ? Optional.empty() : null;
                packet = SCOREBOARD_SCORE_CONSTRUCTOR.newInstance(score, OBJECTIVE_NAME, index, component, component);
            }

            NMSUtils.sendPacket(player, packet);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

            // Use entry name to allow for 48 chars on 1.8 and below
        } else {
            prefix = line.substring(0, maxLength);
            suffix = line.substring(maxLength * 2);
            entry = line.substring(maxLength, maxLength * 2);

            // If a duplicate is present we add a random color at the start
            for (Map.Entry<Integer, String> scoreEntry : scores.entrySet()) {
                if (scoreEntry.getKey() != index && scoreEntry.getValue().equals(entry)) {
                    entry = COLORS[index] + getLastColorsIfPresent(prefix) + entry;
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
