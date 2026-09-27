package dev.azurite.libs.modules.tablist.reflection;

import com.mojang.authlib.GameProfile;
import dev.azurite.libs.modules.tablist.utils.ClassUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TablistReflection {

    private static final Class<?> CRAFT_PLAYER_CLASS;
    private static final Class<?> CRAFT_WORLD_CLASS;
    private static final Class<?> ENTITY_PLAYER_CLASS;
    private static final Class<?> PLAYER_CONNECTION_CLASS;
    private static final Class<?> PACKET_CLASS;
    private static final Class<?> MINECRAFT_SERVER_CLASS;

    private static final Class<?> PLAYER_INTERACT_CLASS;
    private static final Class<?> CLIENT_INFORMATION_CLASS;

    private static final Method PLAYER_GET_METHOD;
    private static final Method WORLD_GET_METHOD;
    private static final Method SEND_PACKET_METHOD;
    private static final Method GET_SERVER_METHOD;
    private static final Method CLIENT_INFORMATION_DEFAULT_METHOD;
    private static final Field PLAYER_CONNECTION_FIELD;

    private static final Constructor<?> ENTITY_PLAYER_CONSTRUCTOR;
    private static final Constructor<?> PLAYER_INTERACT_CONSTRUCTOR;

    static {
        try {
            // Classes
            CRAFT_PLAYER_CLASS = ClassUtils.getBukkitClass("entity.CraftPlayer");
            CRAFT_WORLD_CLASS = ClassUtils.getBukkitClass("CraftWorld");
            ENTITY_PLAYER_CLASS = ClassUtils.getNMSClass("server.level", "EntityPlayer", "ServerPlayer");
            PLAYER_CONNECTION_CLASS = ClassUtils.getNMSClass("server.network", "PlayerConnection", "ServerGamePacketListenerImpl");
            PACKET_CLASS = ClassUtils.getNMSClass("network.protocol", "Packet");
            MINECRAFT_SERVER_CLASS = ClassUtils.getNMSClass("", "MinecraftServer");

            // Nullable
            PLAYER_INTERACT_CLASS = ClassUtils.getNMSClassOrNull("", "PlayerInteractManager");
            CLIENT_INFORMATION_CLASS = ClassUtils.getNMSClassOrNull("server.level", "ClientInformation");

            // Methods
            PLAYER_GET_METHOD = CRAFT_PLAYER_CLASS.getMethod("getHandle");
            WORLD_GET_METHOD = CRAFT_WORLD_CLASS.getMethod("getHandle");
            SEND_PACKET_METHOD = Arrays.stream(PLAYER_CONNECTION_CLASS.getMethods()).filter(method -> method.getParameterCount() == 1 && method.getParameterTypes()[0] == PACKET_CLASS).collect(Collectors.toList()).get(0);
            GET_SERVER_METHOD = MINECRAFT_SERVER_CLASS.getMethod("getServer");
            CLIENT_INFORMATION_DEFAULT_METHOD = CLIENT_INFORMATION_CLASS != null ? CLIENT_INFORMATION_CLASS.getMethod("createDefault") : null;
            PLAYER_CONNECTION_FIELD = Arrays.stream(ENTITY_PLAYER_CLASS.getFields()).filter(field -> field.getType() == PLAYER_CONNECTION_CLASS).collect(Collectors.toList()).get(0);

            // Constructors
            ENTITY_PLAYER_CONSTRUCTOR = ENTITY_PLAYER_CLASS.getConstructors()[0];
            PLAYER_INTERACT_CONSTRUCTOR = PLAYER_INTERACT_CLASS != null ? PLAYER_INTERACT_CLASS.getConstructors()[0] : null;

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final Player player;

    public TablistReflection(Player player) {
        this.player = player;
    }

    public Object createServerPlayer(int col, int row) {
        try {
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "test-" + col + "-" + row);
            Object[] args = new Object[ENTITY_PLAYER_CONSTRUCTOR.getParameterCount()];
            Object world = WORLD_GET_METHOD.invoke(player.getWorld());

            args[0] = GET_SERVER_METHOD.invoke(null);
            args[1] = world;
            args[2] = gameProfile;

            if (args.length == 4) {
                args[3] = PLAYER_INTERACT_CLASS != null ? PLAYER_INTERACT_CONSTRUCTOR.newInstance(world) : CLIENT_INFORMATION_DEFAULT_METHOD.invoke(null);
            }

            return ENTITY_PLAYER_CONSTRUCTOR.newInstance(args);

        } catch (Exception e) {
            return null;
        }
    }

    public void sendPacket(Object packet) {
        try {

            Object entityPlayer = PLAYER_GET_METHOD.invoke(player);
            Object playerConnection = PLAYER_CONNECTION_FIELD.get(entityPlayer);
            SEND_PACKET_METHOD.invoke(playerConnection, packet);

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void test() {
        Object serverPlayer = createServerPlayer(0, 0);
        System.out.println(serverPlayer.toString());
    }
}
