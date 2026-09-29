package dev.azurite.libs.modules.tablist.reflection;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.tablist.TablistModule;
import dev.azurite.libs.modules.tablist.entry.TablistEntry;
import dev.azurite.libs.utils.NMSUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TablistReflection extends SubModule<AzuriteLibs, TablistModule> {

    private static final Class<?> PLAYER_INFO_UPDATE_CLASS;
    private static final Class<?> PLAYER_INFO_REMOVE_CLASS;
    private static final Class<?> PLAYER_INFO_ACTION_CLASS;
    private static final Class<?> PLAYER_INFO_ENTRY_CLASS;
    private static final Class<?> GAME_MODE_CLASS;

    private static final Constructor<?> PLAYER_INFO_UPDATE_CONSTRUCTOR;
    private static final Constructor<?> PLAYER_INFO_ENTRY_CONSTRUCTOR;

    private static final Enum<?> ACTION_ADD_PLAYER;
    private static final Enum<?> ACTION_UPDATE_LISTED;
    private static final Enum<?> ACTION_UPDATE_DISPLAY_NAME;
    private static final Enum<?> ACTION_REMOVE_PLAYER;
    private static final Enum<?> VALID_GAME_MODE_TYPE;

    private static final Field INFO_UPDATE_ENTRIES_FIELD;

    static {
        try {

            // Classes
            PLAYER_INFO_UPDATE_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutPlayerInfo", "ClientboundPlayerInfoUpdatePacket");
            PLAYER_INFO_REMOVE_CLASS = NMSUtils.getNMSClassOrNull("network.protocol.game", "ClientboundPlayerInfoRemovePacket");
            PLAYER_INFO_ACTION_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutPlayerInfo$EnumPlayerInfoAction", "ClientboundPlayerInfoUpdatePacket$Action");
            PLAYER_INFO_ENTRY_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutPlayerInfo$PlayerInfoData", "ClientboundPlayerInfoUpdatePacket$Entry");
            GAME_MODE_CLASS = NMSUtils.getNMSClass("world.level", "WorldSettings$EnumGamemode", "GameType");

            // Constructors
            PLAYER_INFO_UPDATE_CONSTRUCTOR = PLAYER_INFO_REMOVE_CLASS != null ? PLAYER_INFO_UPDATE_CLASS.getConstructor(EnumSet.class, List.class) : PLAYER_INFO_UPDATE_CLASS.getConstructor(PLAYER_INFO_ACTION_CLASS, Iterable.class);
            PLAYER_INFO_ENTRY_CONSTRUCTOR = PLAYER_INFO_ENTRY_CLASS.getConstructors()[0];

            // Enums
            VALID_GAME_MODE_TYPE = NMSUtils.findEnumConstant(GAME_MODE_CLASS, 1);
            ACTION_ADD_PLAYER = NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, 0);
            ACTION_UPDATE_DISPLAY_NAME = NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, PLAYER_INFO_REMOVE_CLASS != null ? 5 : 3);
            ACTION_UPDATE_LISTED = PLAYER_INFO_REMOVE_CLASS != null ? NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, 3) : null;
            ACTION_REMOVE_PLAYER = PLAYER_INFO_REMOVE_CLASS != null ? null : NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, 4);

            // Fields
            INFO_UPDATE_ENTRIES_FIELD = NMSUtils.streamFieldsFindFirst(PLAYER_INFO_UPDATE_CLASS, field -> field.getType() == List.class, true, true);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Player player;

    public TablistReflection(TablistModule module, Player player) {
        super(module);
        this.player = player;
    }

    public void sendCreationPacket(Collection<TablistEntry> entries) {
        try {
            List<Object> nmsEntries = getNMSEntries(entries);
            Object packet;

            if (PLAYER_INFO_REMOVE_CLASS != null) {
                packet = PLAYER_INFO_UPDATE_CONSTRUCTOR.newInstance(NMSUtils.getEnumSet(PLAYER_INFO_ACTION_CLASS, ACTION_ADD_PLAYER, ACTION_UPDATE_LISTED), nmsEntries);

            } else {
                packet = PLAYER_INFO_UPDATE_CONSTRUCTOR.newInstance(ACTION_ADD_PLAYER, Collections.emptyList());
            }

            INFO_UPDATE_ENTRIES_FIELD.set(packet, nmsEntries);
            NMSUtils.sendPacket(player, packet);

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Object> getNMSEntries(Collection<TablistEntry> entries) {
        try {
            List<Object> fakeEntries = new ArrayList<>();

            for (TablistEntry entry : entries) {
                Object component = NMSUtils.stringToComponent(entry.getDisplay());

                if (PLAYER_INFO_REMOVE_CLASS != null) {
                    fakeEntries.add(PLAYER_INFO_ENTRY_CONSTRUCTOR.newInstance(entry.getId(), entry.getProfile(), true, entry.getPing(), VALID_GAME_MODE_TYPE, component, false, 0, null));

                } else {
                    fakeEntries.add(PLAYER_INFO_ENTRY_CONSTRUCTOR.newInstance(entry.getProfile(), entry.getPing(), VALID_GAME_MODE_TYPE, component));
                }
            }
            return fakeEntries;

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
