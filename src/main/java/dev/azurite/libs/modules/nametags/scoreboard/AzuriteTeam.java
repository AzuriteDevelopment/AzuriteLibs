package dev.azurite.libs.modules.nametags.scoreboard;

import dev.azurite.libs.modules.nametags.extra.CollisionRule;
import dev.azurite.libs.modules.nametags.extra.NametagColor;
import dev.azurite.libs.modules.nametags.extra.NametagVisibility;
import dev.azurite.libs.modules.nametags.extra.TeamAction;
import dev.azurite.libs.utils.NMSUtils;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class AzuriteTeam {

    private static final Class<?> SCOREBOARD_TEAM_PACKET_CLASS;
    private static final Class<?> SCOREBOARD_TEAM_PARAMETERS_CLASS;
    private static final Class<?> SCOREBOARD_TEAM_CLASS;
    private static final Class<?> SCOREBOARD_CLASS;

    private static final Constructor<?> SCOREBOARD_TEAM_PACKET_CONSTRUCTOR;
    private static final Constructor<?> SCOREBOARD_TEAM_PARAMETERS_CONSTRUCTOR;

    private static final Field[] SCOREBOARD_TEAM_PACKET_FIELDS;
    private static final Object FAKE_SCOREBOARD_TEAM;

    static {
        try {

            SCOREBOARD_TEAM_PACKET_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutScoreboardTeam", "ClientboundSetPlayerTeamPacket");
            SCOREBOARD_TEAM_PARAMETERS_CLASS = Arrays.stream(SCOREBOARD_TEAM_PACKET_CLASS.getDeclaredClasses()).filter(clazz -> clazz.getDeclaredFields().length == 7).findFirst().orElse(null);
            SCOREBOARD_TEAM_CLASS = SCOREBOARD_TEAM_PARAMETERS_CLASS != null ? NMSUtils.getNMSClass("world.scores", "ScoreboardTeam") : null;
            SCOREBOARD_CLASS = SCOREBOARD_TEAM_PARAMETERS_CLASS != null ? NMSUtils.getNMSClass("world.scores", "Scoreboard") : null;

            SCOREBOARD_TEAM_PACKET_CONSTRUCTOR = NMSUtils.streamFindConstructor(SCOREBOARD_TEAM_PACKET_CLASS, constructor -> constructor.getParameterCount() == 0, true, SCOREBOARD_TEAM_PACKET_CLASS.getConstructors()[0]);
            SCOREBOARD_TEAM_PARAMETERS_CONSTRUCTOR = SCOREBOARD_TEAM_PARAMETERS_CLASS != null ? NMSUtils.streamFindConstructor(SCOREBOARD_TEAM_PARAMETERS_CLASS, constructor -> constructor.getParameterCount() > 1, true, SCOREBOARD_TEAM_PARAMETERS_CLASS.getConstructors()[0]) : null;

            SCOREBOARD_TEAM_PACKET_FIELDS = SCOREBOARD_TEAM_PACKET_CLASS.getDeclaredFields();
            FAKE_SCOREBOARD_TEAM = SCOREBOARD_TEAM_CLASS != null ? SCOREBOARD_TEAM_CLASS.getConstructor(SCOREBOARD_CLASS, String.class).newInstance(SCOREBOARD_CLASS.newInstance(), "Azurite") : null;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

        } else if (!suffix.equals(newSuffix)) {
            this.suffix = newSuffix;
            updated = true;

        } else if (collisionRule != newCollisionRule) {
            this.collisionRule = newCollisionRule;
            updated = true;

        } else if (nametagVisibility != newNametagVisibility) {
            this.nametagVisibility = newNametagVisibility;
            updated = true;

        } else if (nametagColor != newNametagColor) {
            this.nametagColor = newNametagColor;
            updated = true;

        } else if (friendlyInvis != newFriendlyInvis) {
            this.friendlyInvis = newFriendlyInvis;
            updated = true;

        } else if (friendlyFire != newFriendlyFire) {
            this.friendlyFire = newFriendlyFire;
            updated = true;
        }

        return updated;
    }

    public int packOptionData() {
        int data = 0;
        if (friendlyFire) {
            data |= 1;
        }
        if (friendlyInvis) {
            data |= 2;
        }
        return data;
    }

    public boolean addPlayer(String player) {
        if (!players.contains(player)) {
            return players.add(player);
        }
        return false;
    }

    public void sendUpdate(TeamAction action) {
        try {

            int parameters = SCOREBOARD_TEAM_PACKET_CONSTRUCTOR.getParameterCount();
            int actionNumber = action.getId();

            boolean sendParameters = action == TeamAction.CREATE_TEAM || action == TeamAction.UPDATE_TEAM;
            boolean sendPlayerList = action == TeamAction.CREATE_TEAM || action == TeamAction.ADD_PLAYERS || action == TeamAction.REMOVE_PLAYERS;

            Object packet;
            int index = 0;

            if (parameters == 0) {
                boolean hasCollisionRule = SCOREBOARD_TEAM_PACKET_FIELDS.length == 10;
                packet = SCOREBOARD_TEAM_PACKET_CONSTRUCTOR.newInstance();

                setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[index++], name);

                if (sendParameters) {
                    setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[index++], name);
                    setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[index++], prefix);
                    setField(packet, SCOREBOARD_TEAM_PACKET_FIELDS[index++], suffix);
                }

            } else {

            }

            switch (action) {
                case CREATE_TEAM:
                    Object createPacket = parameters == 0 ? SCOREBOARD_TEAM_PACKET_CONSTRUCTOR.newInstance() : SCOREBOARD_TEAM_PACKET_CONSTRUCTOR.newInstance(null);
                    break;

                case REMOVE_TEAM:

                    break;

                case ADD_PLAYERS:

                    break;
            }

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Object packet, Field field, Object value) {
        try {

            Class<?> type = field.getType();

            if (type == int.class) {
                field.set(packet, (int) value);

            } else if (type == String.class) {
                field.set(packet, (String) value);

            } else if (type == NMSUtils.BASE_COMPONENT_CLASS) {
                field.set(packet, NMSUtils.stringToComponent((String) value));
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object createParametersObject() {
        Object parameters;
        int length = SCOREBOARD_TEAM_PARAMETERS_CONSTRUCTOR.getParameterCount();

        if (length == 1) {


        } else {

        }

        return null;
    }
}
