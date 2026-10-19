package dev.azurite.libs.modules.tablist.injector;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.mojang.authlib.GameProfile;
import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.nametags.enums.CollisionRule;
import dev.azurite.libs.modules.nametags.enums.NametagColor;
import dev.azurite.libs.modules.nametags.enums.NametagVisibility;
import dev.azurite.libs.modules.nametags.scoreboard.AzuriteScoreboard;
import dev.azurite.libs.modules.netty.listener.NettyListener;
import dev.azurite.libs.modules.tablist.Tablist;
import dev.azurite.libs.modules.tablist.TablistModule;
import dev.azurite.libs.modules.tablist.entry.OldEntry;
import dev.azurite.libs.modules.tablist.entry.TablistEntry;
import dev.azurite.libs.modules.tablist.enums.PlayerInfoAction;
import dev.azurite.libs.modules.tablist.skin.Skins;
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

    private static final Field INFO_UPDATE_ENTRIES_FIELD;
    private static final Field HEADER_PACKET_FIELD;
    private static final Field FOOTER_PACKET_FIELD;

    private static final PlayerInfoAction[] CREATION_ACTIONS;

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

            // Fields
            INFO_UPDATE_ENTRIES_FIELD = NMSUtils.streamFieldsFindFirst(PLAYER_INFO_UPDATE_CLASS, field -> field.getType() == List.class, false, true);
            HEADER_PACKET_FIELD = NMSUtils.streamFieldsFind(HEADER_FOOTER_PACKET_CLASS, field -> !field.getType().isArray(), false, true, 0);
            FOOTER_PACKET_FIELD = NMSUtils.streamFieldsFind(HEADER_FOOTER_PACKET_CLASS, field -> !field.getType().isArray(), false, true, 1);

            // Actions
            CREATION_ACTIONS = new PlayerInfoAction[]{PlayerInfoAction.ADD_PLAYER, PlayerInfoAction.UPDATE_LATENCY, PlayerInfoAction.UPDATE_DISPLAY_NAME, PlayerInfoAction.UPDATE_LISTED};

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Tablist tablist;
    private final AzuriteScoreboard scoreboard;
    private int version;

    public TablistInjector(TablistModule module, Tablist tablist, Player player) {
        super(module);
        this.tablist = tablist;
        this.scoreboard = new AzuriteScoreboard(player);
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
        OldEntry oldEntry = entry.getOldEntry();

        if (!oldEntry.getDisplay().equals(entry.getDisplay())) {
            this.sendInfoPacket(player, entry, PlayerInfoAction.UPDATE_DISPLAY_NAME);
        }

        if (oldEntry.getPing() != entry.getPing()) {
            this.sendInfoPacket(player, entry, PlayerInfoAction.UPDATE_LATENCY);
        }

        if (version != 5 && !oldEntry.getSkin().equals(entry.getSkin())) {
            this.sendInfoPacket(player, entry, PlayerInfoAction.REMOVE_PLAYER);
            entry.setProfile(NMSUtils.createProfile(entry.getUuid(), entry.getProfileName(), entry.getSkin()));
            this.sendInfoPackets(player, Collections.singletonList(entry), CREATION_ACTIONS);
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

    private void createTablist(Player player) {
        this.version = VersionUtils.getProtocolVersion(player);
        int maxColumns = version == 5 ? 3 : 4;
        int size = maxColumns * 20;

        List<TablistEntry> sendingOrder = new ArrayList<>(size);
        Table<Integer, Integer, TablistEntry> entries = Tables.newCustomTable(new ConcurrentHashMap<>(size), ConcurrentHashMap::new);

        tablist.setMaxColumns(maxColumns);
        tablist.setEntries(entries);

        this.createAndUpdateEntries(player, entries, sendingOrder, maxColumns);
        this.sendInfoPackets(player, sendingOrder, CREATION_ACTIONS);
    }

    private void createAndUpdateEntries(Player player, Table<Integer, Integer, TablistEntry> entries, List<TablistEntry> sendingOrder, int maxColumns) {
        module.getAdapter().updateEntries(player, tablist);

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < maxColumns; col++) {
                TablistEntry entry = entries.get(row, col);

                if (entry == null) {
                    entry = new TablistEntry("", tablist.getInvisibleName(col, row), tablist.getNumberName(col, row), Skins.GRAY, -1);
                    entries.put(col, row, entry);
                }

                sendingOrder.add(entry);
            }
        }

        for (TablistEntry entry : sendingOrder) {
            UUID uuid = UUID.randomUUID();
            entry.setUuid(uuid);
            entry.setProfile(NMSUtils.createProfile(uuid, entry.getProfileName(), entry.getSkin()));
            entry.setOldEntry(new OldEntry(entry));
        }
    }

    private void sendInfoPacket(Player player, TablistEntry entry, PlayerInfoAction... actions) {
        this.sendInfoPackets(player, Collections.singletonList(entry), actions);
    }

    private void sendInfoPackets(Player player, List<TablistEntry> entries, PlayerInfoAction... actions) {
        try {

            boolean version1_7 = version == 5;

            if (PLAYER_INFO_REMOVE_CLASS != null) {
                for (PlayerInfoAction action : actions) {
                    if (action == PlayerInfoAction.REMOVE_PLAYER) {
                        NMSUtils.sendPacket(player, PLAYER_INFO_REMOVE_CONSTRUCTOR.newInstance(entries
                                .stream()
                                .map(TablistEntry::getUuid)
                                .collect(Collectors.toList())));
                        break;
                    }
                }

                EnumSet<?> enumSet = NMSUtils.getEnumSet(PLAYER_INFO_ACTION_CLASS, Arrays.stream(actions)
                        .filter(action -> action != PlayerInfoAction.REMOVE_PLAYER)
                        .filter(action -> !version1_7 || action != PlayerInfoAction.UPDATE_DISPLAY_NAME)
                        .map(action -> action.getEnumObject(PLAYER_INFO_ACTION_CLASS))
                        .toArray(Enum[]::new));

                NMSUtils.sendPacket(player, PLAYER_INFO_UPDATE_CONSTRUCTOR.newInstance(enumSet, getInfoPacketEntries(entries)));

            } else {
                for (PlayerInfoAction action : actions) {
                    Enum<?> enumClass = action.getEnumObject(PLAYER_INFO_ACTION_CLASS);

                    if (version1_7 && action == PlayerInfoAction.UPDATE_DISPLAY_NAME) continue;
                    if (enumClass == null) continue;

                    Object packet = PLAYER_INFO_UPDATE_CONSTRUCTOR.newInstance(enumClass, Collections.emptyList());
                    INFO_UPDATE_ENTRIES_FIELD.set(packet, getInfoPacketEntries(entries));
                    NMSUtils.sendPacket(player, packet);
                }
            }

            for (PlayerInfoAction action : actions) {
                this.handleTeamPackets(entries, action, version1_7);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleTeamPackets(List<TablistEntry> entries, PlayerInfoAction action, boolean version1_7) {
        boolean sendPacket = action == PlayerInfoAction.ADD_PLAYER || (version1_7 && action == PlayerInfoAction.UPDATE_DISPLAY_NAME);

        if (sendPacket) {
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

    private Pair<String, String> splitPrefixSuffixLegacy(String display) {
        int maxLength = version <= 340 ? 16 : 64;
        String prefix;
        String suffix;

        if (display.length() <= maxLength) {
            prefix = display;
            suffix = "";

        } else {
            prefix = display.substring(0, maxLength);
            suffix = display.substring(maxLength);

            if (prefix.endsWith("§")) {
                prefix = display.substring(0, maxLength - 1);
                suffix = display.substring(maxLength - 1);
            }

            if (!suffix.startsWith("§")) {
                suffix = ChatColor.getLastColors(prefix) + suffix;
            }
        }
        return new Pair<>(prefix, suffix.substring(0, Math.min(suffix.length(), maxLength)));
    }

    private List<Object> getInfoPacketEntries(List<TablistEntry> entries) {
        return entries.stream().map(this::getInfoPacketEntry).collect(Collectors.toList());
    }

    private Object getInfoPacketEntry(TablistEntry entry) {
        try {

            GameProfile profile = entry.getProfile();
            int ping = entry.getPing();
            return PLAYER_INFO_REMOVE_CLASS != null ?
                    PLAYER_INFO_ENTRY_CONSTRUCTOR.newInstance(entry.getUuid(), profile, true, ping, VALID_GAME_MODE_TYPE, NMSUtils.stringToComponent(entry.getDisplay()), false, 0, null) :
                    PLAYER_INFO_ENTRY_CONSTRUCTOR.newInstance(null, profile, ping, VALID_GAME_MODE_TYPE, NMSUtils.stringToComponent(entry.getDisplay()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
