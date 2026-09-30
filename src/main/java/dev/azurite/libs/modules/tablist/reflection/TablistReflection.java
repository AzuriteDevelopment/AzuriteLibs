package dev.azurite.libs.modules.tablist.reflection;

import com.mojang.authlib.GameProfile;
import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.netty.listener.NettyListener;
import dev.azurite.libs.modules.tablist.Tablist;
import dev.azurite.libs.modules.tablist.TablistModule;
import dev.azurite.libs.modules.tablist.entry.TablistEntry;
import dev.azurite.libs.modules.versions.utils.VersionUtils;
import dev.azurite.libs.utils.NMSUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TablistReflection extends SubModule<AzuriteLibs, TablistModule> implements NettyListener {

    private static final Class<?> PLAYER_INFO_UPDATE_CLASS;
    private static final Class<?> PLAYER_INFO_REMOVE_CLASS;
    private static final Class<?> PLAYER_INFO_ACTION_CLASS;
    private static final Class<?> PLAYER_INFO_ENTRY_CLASS;
    private static final Class<?> HEADER_FOOTER_PACKET_CLASS;
    private static final Class<?> GAME_MODE_CLASS;

    private static final Constructor<?> PLAYER_INFO_UPDATE_CONSTRUCTOR;
    private static final Constructor<?> PLAYER_INFO_ENTRY_CONSTRUCTOR;
    private static final Constructor<?> HEADER_FOOTER_PACKET_CONSTRUCTOR;

    private static final Enum<?> ACTION_ADD_PLAYER;
    private static final Enum<?> ACTION_UPDATE_LISTED;
    private static final Enum<?> ACTION_UPDATE_DISPLAY_NAME;
    private static final Enum<?> ACTION_REMOVE_PLAYER;
    private static final Enum<?> VALID_GAME_MODE_TYPE;

    private static final Field INFO_UPDATE_ENTRIES_FIELD;
    private static final Field INFO_UPDATE_ACTION_FIELD;
    private static final Field HEADER_PACKET_FIELD;
    private static final Field FOOTER_PACKET_FIELD;

    static {
        try {

            // Classes
            PLAYER_INFO_UPDATE_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutPlayerInfo", "ClientboundPlayerInfoUpdatePacket");
            PLAYER_INFO_REMOVE_CLASS = NMSUtils.getNMSClassOrNull("network.protocol.game", "ClientboundPlayerInfoRemovePacket");
            PLAYER_INFO_ACTION_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutPlayerInfo$EnumPlayerInfoAction", "ClientboundPlayerInfoUpdatePacket$Action");
            PLAYER_INFO_ENTRY_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutPlayerInfo$PlayerInfoData", "ClientboundPlayerInfoUpdatePacket$Entry");
            HEADER_FOOTER_PACKET_CLASS = NMSUtils.getNMSClass("network.protocol.game", "PacketPlayOutPlayerListHeaderFooter", "ClientboundTabListPacket");
            GAME_MODE_CLASS = NMSUtils.getNMSClass("world.level", "WorldSettings$EnumGamemode", "GameType");

            // Constructors
            PLAYER_INFO_UPDATE_CONSTRUCTOR = PLAYER_INFO_REMOVE_CLASS != null ? PLAYER_INFO_UPDATE_CLASS.getConstructor(EnumSet.class, List.class) : PLAYER_INFO_UPDATE_CLASS.getConstructor(PLAYER_INFO_ACTION_CLASS, Iterable.class);
            PLAYER_INFO_ENTRY_CONSTRUCTOR = PLAYER_INFO_ENTRY_CLASS.getConstructors()[0];
            HEADER_FOOTER_PACKET_CONSTRUCTOR = HEADER_FOOTER_PACKET_CLASS.getConstructors()[0];

            // Enums
            VALID_GAME_MODE_TYPE = NMSUtils.findEnumConstant(GAME_MODE_CLASS, 1);
            ACTION_ADD_PLAYER = NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, 0);
            ACTION_UPDATE_DISPLAY_NAME = NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, PLAYER_INFO_REMOVE_CLASS != null ? 5 : 3);
            ACTION_UPDATE_LISTED = PLAYER_INFO_REMOVE_CLASS != null ? NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, 3) : null;
            ACTION_REMOVE_PLAYER = PLAYER_INFO_REMOVE_CLASS != null ? null : NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, 4);

            // Fields
            INFO_UPDATE_ENTRIES_FIELD = NMSUtils.streamFieldsFindFirst(PLAYER_INFO_UPDATE_CLASS, field -> field.getType() == List.class, false, true);
            INFO_UPDATE_ACTION_FIELD = NMSUtils.streamFieldsFindFirst(PLAYER_INFO_UPDATE_CLASS, field -> field.getType() == PLAYER_INFO_ACTION_CLASS || field.getType() == EnumSet.class, false, true);
            HEADER_PACKET_FIELD = NMSUtils.streamFieldsFind(HEADER_FOOTER_PACKET_CLASS, field -> !field.getType().isArray(), false, true, 0);
            FOOTER_PACKET_FIELD = NMSUtils.streamFieldsFind(HEADER_FOOTER_PACKET_CLASS, field -> !field.getType().isArray(), false, true, 1);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Tablist tablist;
    private final Player player;

    public TablistReflection(TablistModule module, Tablist tablist) {
        super(module);
        this.tablist = tablist;
        this.player = tablist.getPlayer();
    }

    @Override
    public boolean write(Player player, Object packet) {
        /*
        This will create the tablist at the earliest point possible while also being async
        1.7 requires creation before any real player info packets are sent
        This will also allow us to not have to send remove/add packets
         */
        if (packet.getClass() == PLAYER_INFO_UPDATE_CLASS) {
            try {
                AtomicBoolean initialized = tablist.getInitialized();

                if (!initialized.get()) {
                    boolean addPlayer = false;

                    if (PLAYER_INFO_REMOVE_CLASS != null) {
                        EnumSet<?> enums = (EnumSet<?>) INFO_UPDATE_ACTION_FIELD.get(packet);

                        if (enums.contains(ACTION_ADD_PLAYER)) {
                            addPlayer = true;
                        }

                    } else {
                        addPlayer = INFO_UPDATE_ACTION_FIELD.get(packet) == ACTION_ADD_PLAYER;
                    }

                    if (addPlayer) {
                        initialized.set(true);
                        this.createTablist();
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public void sendHeaderFooter(String header, String footer) {
        try {

            int length = HEADER_FOOTER_PACKET_CONSTRUCTOR.getParameterCount();
            Object packet = length == 0 ? HEADER_FOOTER_PACKET_CONSTRUCTOR.newInstance() : HEADER_FOOTER_PACKET_CONSTRUCTOR.newInstance(NMSUtils.stringToComponent(header), NMSUtils.stringToComponent(footer));

            if (length == 0) {
                HEADER_PACKET_FIELD.set(packet, NMSUtils.stringToComponent(header));
                FOOTER_PACKET_FIELD.set(packet, NMSUtils.stringToComponent(footer));
            }

            NMSUtils.sendPacket(player, packet);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTablistEntryName(int col, int row) {
        int pos = (col * 20) + row;
        return String.format("%04d", pos);
    }

    private void createTablist() {
        List<TablistEntry> sendingOrder = new ArrayList<>();
        int version = VersionUtils.getProtocolVersion(player);
        tablist.setMaxColumns(version == 5 ? 3 : 4);
        tablist.forEachEntry(sendingOrder::add);
        this.sendCreationPacket(sendingOrder);
    }

    private void sendCreationPacket(List<TablistEntry> entries) {
        try {
            List<Object> nmsEntries = getNMSEntries(entries);
            Object packet;

            if (PLAYER_INFO_REMOVE_CLASS != null) {
                packet = PLAYER_INFO_UPDATE_CONSTRUCTOR.newInstance(NMSUtils.getEnumSet(PLAYER_INFO_ACTION_CLASS, ACTION_ADD_PLAYER, ACTION_UPDATE_LISTED), nmsEntries);

            } else {
                packet = PLAYER_INFO_UPDATE_CONSTRUCTOR.newInstance(ACTION_ADD_PLAYER, Collections.emptyList());
                INFO_UPDATE_ENTRIES_FIELD.set(packet, nmsEntries);
            }

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
                UUID uuid = entry.getUuid();
                GameProfile profile = entry.getProfile();
                int ping = entry.getPing();

                if (PLAYER_INFO_REMOVE_CLASS != null) {
                    fakeEntries.add(PLAYER_INFO_ENTRY_CONSTRUCTOR.newInstance(uuid, profile, true, ping, VALID_GAME_MODE_TYPE, component, false, 0, null));

                } else {
                    fakeEntries.add(PLAYER_INFO_ENTRY_CONSTRUCTOR.newInstance(profile, ping, VALID_GAME_MODE_TYPE, component));
                }
            }
            return fakeEntries;

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
