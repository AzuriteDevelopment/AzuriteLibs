package dev.azurite.libs.modules.tablist.injector;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.mojang.authlib.GameProfile;
import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.nametags.extra.CollisionRule;
import dev.azurite.libs.modules.nametags.extra.NametagColor;
import dev.azurite.libs.modules.nametags.extra.NametagVisibility;
import dev.azurite.libs.modules.nametags.scoreboard.AzuriteScoreboard;
import dev.azurite.libs.modules.netty.listener.NettyListener;
import dev.azurite.libs.modules.tablist.Tablist;
import dev.azurite.libs.modules.tablist.TablistModule;
import dev.azurite.libs.modules.tablist.entry.TablistEntry;
import dev.azurite.libs.modules.tablist.skin.DefaultSkins;
import dev.azurite.libs.modules.tablist.skin.TablistSkin;
import dev.azurite.libs.modules.versions.utils.VersionUtils;
import dev.azurite.libs.utils.NMSUtils;
import dev.azurite.libs.utils.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@SuppressWarnings("UnstableApiUsage")
public class TablistInjector extends SubModule<AzuriteLibs, TablistModule> implements NettyListener {

    private static final Class<?> PLAYER_INFO_UPDATE_CLASS;
    private static final Class<?> PLAYER_INFO_REMOVE_CLASS;
    private static final Class<?> PLAYER_INFO_ACTION_CLASS;
    private static final Class<?> PLAYER_INFO_ENTRY_CLASS;
    private static final Class<?> HEADER_FOOTER_PACKET_CLASS;
    private static final Class<?> GAME_MODE_CLASS;

    private static final Constructor<?> PLAYER_INFO_UPDATE_CONSTRUCTOR;
    private static final Constructor<?> PLAYER_INFO_REMOVE_CONSTRUCTOR;
    private static final Constructor<?> PLAYER_INFO_ENTRY_CONSTRUCTOR;
    private static final Constructor<?> HEADER_FOOTER_PACKET_CONSTRUCTOR;

    private static final Enum<?> VALID_GAME_MODE_TYPE;
    private static final Enum<?> ACTION_ADD_PLAYER;
    private static final Enum<?> ACTION_UPDATE_LATENCY;
    private static final Enum<?> ACTION_UPDATE_DISPLAY_NAME;
    private static final Enum<?> ACTION_UPDATE_LISTED;
    private static final Enum<?> ACTION_REMOVE_PLAYER;

    private static final Field INFO_UPDATE_ENTRIES_FIELD;
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
            PLAYER_INFO_REMOVE_CONSTRUCTOR = PLAYER_INFO_REMOVE_CLASS != null ? PLAYER_INFO_REMOVE_CLASS.getConstructors()[0] : null;
            PLAYER_INFO_ENTRY_CONSTRUCTOR = PLAYER_INFO_ENTRY_CLASS.getConstructors()[0];
            HEADER_FOOTER_PACKET_CONSTRUCTOR = NMSUtils.streamFindConstructor(HEADER_FOOTER_PACKET_CLASS, constructor -> constructor.getParameterCount() > 0, true, HEADER_FOOTER_PACKET_CLASS.getConstructors()[0]);

            // Enums
            VALID_GAME_MODE_TYPE = NMSUtils.findEnumConstant(GAME_MODE_CLASS, 1);
            ACTION_ADD_PLAYER = NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, 0);
            ACTION_UPDATE_LATENCY = NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, PLAYER_INFO_REMOVE_CLASS != null ? 4 : 2);
            ACTION_UPDATE_DISPLAY_NAME = NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, PLAYER_INFO_REMOVE_CLASS != null ? 5 : 3);
            ACTION_UPDATE_LISTED = PLAYER_INFO_REMOVE_CLASS != null ? NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, 3) : null;
            ACTION_REMOVE_PLAYER = PLAYER_INFO_REMOVE_CLASS != null ? null : NMSUtils.findEnumConstant(PLAYER_INFO_ACTION_CLASS, 4);

            // Fields
            INFO_UPDATE_ENTRIES_FIELD = NMSUtils.streamFieldsFindFirst(PLAYER_INFO_UPDATE_CLASS, field -> field.getType() == List.class, false, true);
            HEADER_PACKET_FIELD = NMSUtils.streamFieldsFind(HEADER_FOOTER_PACKET_CLASS, field -> !field.getType().isArray(), false, true, 0);
            FOOTER_PACKET_FIELD = NMSUtils.streamFieldsFind(HEADER_FOOTER_PACKET_CLASS, field -> !field.getType().isArray(), false, true, 1);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Tablist tablist;
    private final AzuriteScoreboard scoreboard;
    private int version;

    public TablistInjector(TablistModule module, Tablist tablist) {
        super(module);
        this.tablist = tablist;
        this.scoreboard = new AzuriteScoreboard(tablist.getPlayer());
        this.version = -1;
    }

    @Override
    public boolean write(Player player, Object packet) {
        if (packet.getClass() == PLAYER_INFO_UPDATE_CLASS) {
            if (!tablist.isInitialized()) {
                tablist.setInitialized(true);
                this.createTablist(player);
            }
        }
        return true;
    }

    public void sendTablistUpdate(Player player, TablistEntry entry) {
        TablistEntry oldEntry = entry.getOldValue();

        if (!oldEntry.getDisplay().equals(entry.getDisplay())) {
            this.sendInfoPackets(player, Collections.singletonList(entry), ACTION_UPDATE_DISPLAY_NAME);
        }

        if (oldEntry.getPing() != entry.getPing()) {
            this.sendInfoPackets(player, Collections.singletonList(entry), ACTION_UPDATE_LATENCY);
        }

        if (!isVersion1_7() && !oldEntry.getSkin().equals(entry.getSkin())) {
            this.sendInfoPackets(player, Collections.singletonList(entry), ACTION_REMOVE_PLAYER);
            entry.setProfile(NMSUtils.createProfile(entry.getUuid(), entry.getProfileName(), entry.getSkin()));
            this.sendInfoPackets(player, Collections.singletonList(entry), ACTION_ADD_PLAYER);
        }
    }

    public void sendHeaderFooter(Player player, String header, String footer) {
        try {

            int length = HEADER_FOOTER_PACKET_CONSTRUCTOR.getParameterCount();
            Object packet;

            switch (length) {
                case 0:
                    packet = HEADER_FOOTER_PACKET_CONSTRUCTOR.newInstance();
                    break;

                case 1:
                    packet = HEADER_FOOTER_PACKET_CONSTRUCTOR.newInstance(NMSUtils.stringToComponent(header));
                    break;

                default:
                    packet = HEADER_FOOTER_PACKET_CONSTRUCTOR.newInstance(NMSUtils.stringToComponent(header), NMSUtils.stringToComponent(footer));
                    break;
            }

            if (length == 0) {
                HEADER_PACKET_FIELD.set(packet, NMSUtils.stringToComponent(header));
                FOOTER_PACKET_FIELD.set(packet, NMSUtils.stringToComponent(footer));

            } else if (length == 1) {
                FOOTER_PACKET_FIELD.set(packet, NMSUtils.stringToComponent(footer));
            }

            NMSUtils.sendPacket(player, packet);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private String getInvisibleName(int col, int row) {
        StringBuilder builder = new StringBuilder("§" + col);

        for (char c : String.valueOf(row).toCharArray()) {
            builder.append("§").append(c);
        }
        return builder.toString();
    }

    private String getNumberName(int col, int row) {
        return String.format("%016d", (col * 20) + row);
    }

    private void createTablist(Player player) {
        this.version = VersionUtils.getProtocolVersion(player);
        int maxColumns = version == 5 ? 3 : 4;
        int size = maxColumns * 20;

        List<TablistEntry> sendingOrder = new ArrayList<>(size);
        Table<Integer, Integer, TablistEntry> entries = Tables.newCustomTable(new ConcurrentHashMap<>(size), ConcurrentHashMap::new);

        tablist.setMaxColumns(maxColumns);
        tablist.setEntries(entries);

        this.createAndUpdateEntries(player, entries, sendingOrder, maxColumns);

        // Only set profile after we've updated entries as the skin could have changed.
        sendingOrder.forEach(entry -> {
            UUID uuid = UUID.randomUUID();
            entry.setProfile(NMSUtils.createProfile(uuid, entry.getProfileName(), entry.getSkin()));
            entry.setOldValue(new TablistEntry(entry));
        });

        this.sendInfoPackets(player, sendingOrder, ACTION_ADD_PLAYER, ACTION_UPDATE_LATENCY, ACTION_UPDATE_DISPLAY_NAME, ACTION_UPDATE_LISTED);
    }

    private void createAndUpdateEntries(Player player, Table<Integer, Integer, TablistEntry> entries, List<TablistEntry> sendingOrder, int maxColumns) {
        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < maxColumns; col++) {
                TablistSkin skin = DefaultSkins.GRAY;
                UUID uuid = UUID.randomUUID();

                String invisibleName = getInvisibleName(col, row);
                String numberName = getNumberName(row, col);
                TablistEntry entry = new TablistEntry(uuid, "", invisibleName, numberName, null, skin, -1);

                entries.put(col, row, entry);
                sendingOrder.add(entry);
            }
        }
        module.getAdapter().updateEntries(player, tablist);
    }

    private void sendInfoPackets(Player player, List<TablistEntry> entries, Enum<?>... actions) {
        try {
            boolean version1_7 = isVersion1_7();

            for (Enum<?> action : actions) {
                Object packet = null;

                if (action == null) {
                    if (PLAYER_INFO_REMOVE_CONSTRUCTOR != null) {
                        packet = PLAYER_INFO_REMOVE_CONSTRUCTOR.newInstance(entries
                                .stream()
                                .map(TablistEntry::getUuid)
                                .collect(Collectors.toList()));
                    } else {
                        continue;
                    }
                }

                if (packet == null) {
                    if (PLAYER_INFO_REMOVE_CLASS != null) {
                        packet = PLAYER_INFO_UPDATE_CONSTRUCTOR.newInstance(NMSUtils.getEnumSet(PLAYER_INFO_ACTION_CLASS, action), getInfoPacketEntries(null, entries));

                    } else {
                        packet = PLAYER_INFO_UPDATE_CONSTRUCTOR.newInstance(action, Collections.emptyList());
                        INFO_UPDATE_ENTRIES_FIELD.set(packet, getInfoPacketEntries(packet, entries));
                    }
                }

                NMSUtils.sendPacket(player, packet);

                if (action == ACTION_ADD_PLAYER || (version1_7 && action == ACTION_UPDATE_DISPLAY_NAME)) {
                    for (TablistEntry entry : entries) {
                        String prefix = "";
                        String suffix = "";

                        if (version1_7) {
                            Pair<String, String> split = splitPrefixSuffixLegacy(entry.getDisplay());
                            prefix = split.getLeft();
                            suffix = split.getRight();
                        }

                        scoreboard.addAndCreateTeam(entry.getProfileName(), entry.getNumberName(), prefix, suffix, NametagVisibility.ALWAYS, CollisionRule.NEVER, NametagColor.NONE, false, false);
                    }
                }
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isVersion1_7() {
        return version == 5;
    }

    private Pair<String, String> splitPrefixSuffixLegacy(String display) {
        String prefix;
        String suffix;

        if (display.length() <= 16) {
            prefix = display;
            suffix = "";

        } else {
            prefix = display.substring(0, 16);
            suffix = display.substring(16);

            if (prefix.endsWith("§")) {
                prefix = display.substring(0, 15);
                suffix = display.substring(15);
            }

            if (!suffix.startsWith("§")) {
                suffix = ChatColor.getLastColors(prefix) + suffix;
            }
        }
        return new Pair<>(prefix, suffix.substring(0, Math.min(suffix.length(), 16)));
    }

    private List<Object> getInfoPacketEntries(Object packet, List<TablistEntry> entries) {
        return entries.stream().map(entry -> getInfoPacketEntry(packet, entry)).collect(Collectors.toList());
    }

    private Object getInfoPacketEntry(Object packet, TablistEntry entry) {
        try {

            UUID uuid = entry.getUuid();
            GameProfile profile = entry.getProfile();
            int ping = entry.getPing();
            return PLAYER_INFO_REMOVE_CLASS != null ?
                    PLAYER_INFO_ENTRY_CONSTRUCTOR.newInstance(uuid, profile, true, ping, VALID_GAME_MODE_TYPE, NMSUtils.stringToComponent(entry.getDisplay()), false, 0, null) :
                    PLAYER_INFO_ENTRY_CONSTRUCTOR.newInstance(packet, profile, ping, VALID_GAME_MODE_TYPE, isVersion1_7() ? null : NMSUtils.stringToComponent(entry.getDisplay()));

        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
