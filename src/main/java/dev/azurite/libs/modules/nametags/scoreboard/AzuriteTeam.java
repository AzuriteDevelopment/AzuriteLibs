package dev.azurite.libs.modules.nametags.scoreboard;

import dev.azurite.libs.modules.nametags.enums.CollisionRule;
import dev.azurite.libs.modules.nametags.enums.NametagColor;
import dev.azurite.libs.modules.nametags.enums.NametagVisibility;
import dev.azurite.libs.modules.nametags.enums.TeamAction;
import dev.azurite.libs.modules.versions.utils.VersionUtils;
import dev.azurite.libs.utils.NMSAllocator;
import dev.azurite.libs.utils.NMSUtils;
import dev.azurite.libs.utils.Pair;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class AzuriteTeam {

    private static final Class<?> SCOREBOARD_TEAM_PACKET_CLASS;
    private static final Class<?> NAMETAG_COLOR_CLASS;

    private static final Class<?> SCOREBOARD_TEAM_PARAMETERS_SUB_CLASS;
    private static final Class<?> COLLISION_RULE_SUB_CLASS;
    private static final Class<?> NAMETAG_VISIBILITY_SUB_CLASS;

    private static final Enum<?> DEFAULT_COLOR_ENUM;

    private static final NMSAllocator.Allocator SCOREBOARD_TEAM_PARAMETERS_ALLOCATOR;

    private static final Constructor<?> SCOREBOARD_TEAM_PACKET_CONSTRUCTOR;
    private static final Constructor<?> SCOREBOARD_TEAM_PARAMETERS_CONSTRUCTOR;

    private static final Field[] SCOREBOARD_TEAM_PACKET_FIELDS;
    private static final Field[] SCOREBOARD_TEAM_PARAMETERS_PACKET_FIELDS;

    private static final Method GET_NAMETAG_COLOR_METHOD;

    static {
        // Classes
        SCOREBOARD_TEAM_PACKET_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutScoreboardTeam", "ClientboundSetPlayerTeamPacket");
        NAMETAG_COLOR_CLASS = NMSUtils.getNMSClass("world.scores", "EnumChatFormat", "TeamColor");

        // Sub Classes
        SCOREBOARD_TEAM_PARAMETERS_SUB_CLASS = NMSUtils.getNMSSubClass("network.protocol.game", false, 1, SCOREBOARD_TEAM_PACKET_CLASS, "Parameters");
        COLLISION_RULE_SUB_CLASS = NMSUtils.getNMSSubClass("world.scores", true, 0, NMSUtils.getNMSClassOrNull("world.scores", "ScoreboardTeamBase", "Team"), "EnumTeamPush", "CollisionRule");
        NAMETAG_VISIBILITY_SUB_CLASS = NMSUtils.getNMSSubClass("world.scores", true, 1, NMSUtils.getNMSClassOrNull("world.scores", "ScoreboardTeamBase", "Team"), "EnumNameTagVisibility", "Visibility");

        // Enums
        DEFAULT_COLOR_ENUM = NMSUtils.findEnumConstant(NAMETAG_COLOR_CLASS, NAMETAG_COLOR_CLASS.getEnumConstants().length - 1);

        // Allocator
        SCOREBOARD_TEAM_PARAMETERS_ALLOCATOR = SCOREBOARD_TEAM_PARAMETERS_SUB_CLASS != null ? NMSAllocator.getAllocator(SCOREBOARD_TEAM_PARAMETERS_SUB_CLASS) : null;

        // Constructors
        SCOREBOARD_TEAM_PACKET_CONSTRUCTOR = NMSUtils.streamFindConstructor(SCOREBOARD_TEAM_PACKET_CLASS, constructor -> constructor.getParameterCount() == 0, true, NMSUtils.getConstructorOrNull(SCOREBOARD_TEAM_PACKET_CLASS, String.class, int.class, Optional.class, Collection.class));
        SCOREBOARD_TEAM_PARAMETERS_CONSTRUCTOR = SCOREBOARD_TEAM_PARAMETERS_SUB_CLASS != null ? NMSUtils.streamFindConstructor(SCOREBOARD_TEAM_PARAMETERS_SUB_CLASS, constructor -> constructor.getParameterCount() > 1, true, SCOREBOARD_TEAM_PARAMETERS_SUB_CLASS.getConstructors()[0]) : null;

        // Fields
        SCOREBOARD_TEAM_PACKET_FIELDS = Arrays.stream(SCOREBOARD_TEAM_PACKET_CLASS.getDeclaredFields()).peek(field -> field.setAccessible(true)).toArray(Field[]::new);
        SCOREBOARD_TEAM_PARAMETERS_PACKET_FIELDS = SCOREBOARD_TEAM_PARAMETERS_SUB_CLASS != null ? Arrays.stream(SCOREBOARD_TEAM_PARAMETERS_SUB_CLASS.getDeclaredFields()).peek(field -> field.setAccessible(true)).toArray(Field[]::new) : null;

        // Methods
        GET_NAMETAG_COLOR_METHOD = Arrays.stream(NAMETAG_COLOR_CLASS.getDeclaredMethods()).filter(method -> method.getReturnType() == NAMETAG_COLOR_CLASS && method.getParameterCount() == 1 && method.getParameterTypes()[0] == String.class).findFirst().orElse(null);
    }

    private String name;
    private String prefix;
    private String suffix;

    private CollisionRule collisionRule;
    private NametagVisibility nametagVisibility;
    private NametagColor nametagColor;

    private boolean friendlyInvis;
    private boolean friendlyFire;

    private List<String> players;

    public AzuriteTeam(String name, String prefix, String suffix, CollisionRule collisionRule, NametagVisibility nametagVisibility, NametagColor nametagColor, boolean friendlyInvis, boolean friendlyFire, String... players) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.collisionRule = collisionRule;
        this.nametagVisibility = nametagVisibility;
        this.nametagColor = nametagColor;
        this.friendlyInvis = friendlyInvis;
        this.friendlyFire = friendlyFire;
        this.players = new ArrayList<>(Arrays.asList(players));
    }

    public boolean updateValues(String newPrefix, String newSuffix, CollisionRule newCollisionRule, NametagVisibility newNametagVisibility, NametagColor newNametagColor, boolean newFriendlyInvis, boolean newFriendlyFire) {
        boolean updated = false;

        if (!prefix.equals(newPrefix)) {
            this.prefix = newPrefix;
            updated = true;
        }
        if (!suffix.equals(newSuffix)) {
            this.suffix = newSuffix;
            updated = true;
        }
        if (collisionRule != newCollisionRule) {
            this.collisionRule = newCollisionRule;
            updated = true;
        }
        if (nametagVisibility != newNametagVisibility) {
            this.nametagVisibility = newNametagVisibility;
            updated = true;
        }
        if (nametagColor != newNametagColor) {
            this.nametagColor = newNametagColor;
            updated = true;
        }
        if (friendlyInvis != newFriendlyInvis) {
            this.friendlyInvis = newFriendlyInvis;
            updated = true;
        }
        if (friendlyFire != newFriendlyFire) {
            this.friendlyFire = newFriendlyFire;
            updated = true;
        }

        return updated;
    }

    public boolean addPlayer(String player) {
        if (!players.contains(player)) {
            return players.add(player);
        }
        return false;
    }

    public void sendUpdate(Player player, TeamAction action) {
        this.sendUpdate(player, action, players);
    }

    public void sendUpdate(Player player, TeamAction action, List<String> players) {
        try {

            boolean sendParameters = action == TeamAction.CREATE_TEAM || action == TeamAction.UPDATE_TEAM;
            boolean sendPlayerList = action == TeamAction.CREATE_TEAM || action == TeamAction.ADD_PLAYERS || action == TeamAction.REMOVE_PLAYERS;
            int version = VersionUtils.getProtocolVersion(player);

            Object packet = SCOREBOARD_TEAM_PACKET_CONSTRUCTOR.getParameterCount() == 0 ?
                    getLegacyPacket(action, players, sendParameters, sendPlayerList, version) :
                    getModernPacket(action, players, sendParameters, sendPlayerList);

            NMSUtils.sendPacket(player, packet);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getLegacyPacket(TeamAction action, List<String> players, boolean sendParameters, boolean sendPlayerList, int version) {
        try {
            int add = SCOREBOARD_TEAM_PACKET_FIELDS.length == 10 ? 1 : 0;

            Object packet = SCOREBOARD_TEAM_PACKET_CONSTRUCTOR.newInstance();
            Field colorField = SCOREBOARD_TEAM_PACKET_FIELDS[5 + add];

            boolean isIntColor = colorField.getType() == int.class;
            boolean addColor = isIntColor && nametagColor != NametagColor.NONE;

            // Name + Action
            NMSUtils.setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[0], name);
            NMSUtils.setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[7 + add], action.getId());

            // Parameters
            if (sendParameters) {
                Pair<String, String> prefixSuffix = cutPrefixSuffix(version, prefix, suffix, addColor);

                NMSUtils.setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[1], name);
                NMSUtils.setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[2], prefixSuffix.getLeft());
                NMSUtils.setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[3], prefixSuffix.getRight());
                NMSUtils.setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[4], nametagVisibility.getName());

                if (SCOREBOARD_TEAM_PACKET_FIELDS.length == 10) {
                    NMSUtils.setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[5], collisionRule.getName());
                }

                if (colorField.getType() != int.class) {
                    NMSUtils.setField(packet, colorField, getChatFormat());
                }

                NMSUtils.setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[8 + add], packOptionData());
            }

            // Player list
            if (sendPlayerList) {
                NMSUtils.setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[6 + add], players);
            }

            return packet;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getModernPacket(TeamAction action, List<String> players, boolean sendParameters, boolean sendPlayerList) {
        try {

            Optional<Object> parameters;

            if (sendParameters) {
                // Parameters object
                if (SCOREBOARD_TEAM_PARAMETERS_CONSTRUCTOR.getParameterCount() == 1) {
                    Object object = SCOREBOARD_TEAM_PARAMETERS_ALLOCATOR.allocate();

                    NMSUtils.setField(object, SCOREBOARD_TEAM_PARAMETERS_PACKET_FIELDS[0], "");
                    NMSUtils.setField(object, SCOREBOARD_TEAM_PARAMETERS_PACKET_FIELDS[1], prefix);
                    NMSUtils.setField(object, SCOREBOARD_TEAM_PARAMETERS_PACKET_FIELDS[2], suffix);
                    NMSUtils.setField(object, SCOREBOARD_TEAM_PARAMETERS_PACKET_FIELDS[3], nametagVisibility.getName());
                    NMSUtils.setField(object, SCOREBOARD_TEAM_PARAMETERS_PACKET_FIELDS[4], collisionRule.getName());
                    NMSUtils.setField(object, SCOREBOARD_TEAM_PARAMETERS_PACKET_FIELDS[5], getChatFormat());
                    NMSUtils.setField(object, SCOREBOARD_TEAM_PARAMETERS_PACKET_FIELDS[6], packOptionData());

                    parameters = Optional.of(object);

                } else {
                    Object componentName = NMSUtils.stringToComponent("");
                    Object componentPrefix = NMSUtils.stringToComponent(prefix);
                    Object componentSuffix = NMSUtils.stringToComponent(suffix);

                    Enum<?> enumNametag = NMSUtils.findEnumConstant(NAMETAG_VISIBILITY_SUB_CLASS, nametagVisibility.name());
                    Enum<?> enumCollision = NMSUtils.findEnumConstant(COLLISION_RULE_SUB_CLASS, collisionRule.name());

                    Optional<Object> color = nametagColor == NametagColor.NONE ? Optional.empty() : Optional.ofNullable(NMSUtils.findEnumConstant(NAMETAG_COLOR_CLASS, nametagColor.name()));
                    byte data = (byte) packOptionData();

                    parameters = Optional.of(SCOREBOARD_TEAM_PARAMETERS_CONSTRUCTOR.newInstance(componentName, componentPrefix, componentSuffix, enumNametag, enumCollision, color, data));
                }

            } else {
                parameters = Optional.empty();
            }

            return SCOREBOARD_TEAM_PACKET_CONSTRUCTOR.newInstance(name, action.getId(), parameters, sendPlayerList ? players : Collections.emptyList());

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private Pair<String, String> cutPrefixSuffix(int version, String prefix, String suffix, boolean addColor) {
        int maxLength = version <= 340 ? 16 : 64;
        int maxLengthPrefix = maxLength - (addColor ? 2 : 0);

        if (prefix.length() > maxLengthPrefix) {
            prefix = prefix.substring(0, maxLengthPrefix);
        }

        if (suffix.length() > maxLength) {
            suffix = suffix.substring(0, maxLength);
        }

        prefix = prefix + (addColor ? nametagColor.toColorString() : "");
        return new Pair<>(prefix, suffix);
    }

    private Object getChatFormat() {
        try {
            if (nametagColor == NametagColor.NONE) {
                return DEFAULT_COLOR_ENUM;
            }
            return GET_NAMETAG_COLOR_METHOD.invoke(null, nametagColor.name());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int packOptionData() {
        int data = 0;
        if (friendlyFire) {
            data |= 1;
        }
        if (friendlyInvis) {
            data |= 2;
        }
        return data;
    }
}
