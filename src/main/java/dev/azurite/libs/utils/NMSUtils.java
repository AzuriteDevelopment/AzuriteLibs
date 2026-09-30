package dev.azurite.libs.utils;

import dev.azurite.libs.modules.versions.SupportedVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class NMSUtils {

    public static final SupportedVersion SUPPORTED_VERSION;
    public static final String BUKKIT_CLASS_PATH;
    public static final String NMS_CLASS_PATH;
    public static final boolean MODERN_PACKAGING;

    public static final Class<?> CRAFT_PLAYER_CLASS;
    public static final Class<?> CRAFT_CHAT_MESSAGE_CLASS;
    public static final Class<?> ENTITY_PLAYER_CLASS;
    public static final Class<?> PLAYER_CONNECTION_CLASS;
    public static final Class<?> NETWORK_MANAGER_CLASS;
    public static final Class<?> PACKET_CLASS;
    public static final Class<?> NETTY_CHANNEL_CLASS;
    public static final Class<?> GAME_PROFILE_CLASS;

    public static final Constructor<?> GAME_PROFILE_CONSTRUCTOR;

    public static final Method PLAYER_GET_METHOD;
    public static final Method FROM_STRING_METHOD;
    public static final Method SEND_PACKET_METHOD;

    public static final Field PLAYER_CONNECTION_FIELD;
    public static final Field NETWORK_MANAGER_FIELD;
    public static final Field CHANNEL_FIELD;

    static {
        try {

            Class<?> MODERN_NMS_CLASS = findClass("net.minecraft.server.Main");
            SUPPORTED_VERSION = SupportedVersion.getSupportedVersion();
            BUKKIT_CLASS_PATH = Bukkit.getServer().getClass().getPackage().getName();
            NMS_CLASS_PATH = MODERN_NMS_CLASS != null ? "net.minecraft" : "net.minecraft.server." + BUKKIT_CLASS_PATH.substring(BUKKIT_CLASS_PATH.lastIndexOf('.') + 1);
            MODERN_PACKAGING = MODERN_NMS_CLASS != null;

            CRAFT_PLAYER_CLASS = getBukkitClass("entity.CraftPlayer");
            CRAFT_CHAT_MESSAGE_CLASS = getBukkitClass("util.CraftChatMessage");
            ENTITY_PLAYER_CLASS = getNMSClass("server.level", "EntityPlayer", "ServerPlayer");
            PLAYER_CONNECTION_CLASS = getNMSClass("server.network", "PlayerConnection", "ServerGamePacketListenerImpl");
            NETWORK_MANAGER_CLASS = getNMSClass("network", "NetworkManager", "Connection");
            PACKET_CLASS = getNMSClass("network.protocol", "Packet");
            NETTY_CHANNEL_CLASS = getNMSUtilClass("io.netty.channel.Channel");
            GAME_PROFILE_CLASS = getNMSUtilClass("com.mojang.authlib.GameProfile");

            GAME_PROFILE_CONSTRUCTOR = GAME_PROFILE_CLASS.getConstructor(UUID.class, String.class);

            PLAYER_GET_METHOD = CRAFT_PLAYER_CLASS.getMethod("getHandle");
            FROM_STRING_METHOD = CRAFT_CHAT_MESSAGE_CLASS.getMethod("fromString", String.class, boolean.class);
            SEND_PACKET_METHOD = NMSUtils.streamMethodsFindFirst(PLAYER_CONNECTION_CLASS, method -> method.getParameterCount() == 1 && method.getParameterTypes()[0] == PACKET_CLASS && method.getReturnType() == void.class, true, true);

            PLAYER_CONNECTION_FIELD = NMSUtils.streamFieldsFindFirst(ENTITY_PLAYER_CLASS, field -> field.getType() == PLAYER_CONNECTION_CLASS, false, true);
            NETWORK_MANAGER_FIELD = NMSUtils.streamFieldsFindFirst(PLAYER_CONNECTION_CLASS, field -> field.getType() == NETWORK_MANAGER_CLASS, true, true);
            CHANNEL_FIELD = NMSUtils.streamFieldsFindFirst(NETWORK_MANAGER_CLASS, field -> field.getType() == NETTY_CHANNEL_CLASS, true, true);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getChannel(Player player) {
        try {

            Object entityPlayer = PLAYER_GET_METHOD.invoke(player);
            Object playerConnection = PLAYER_CONNECTION_FIELD.get(entityPlayer);

            if (playerConnection != null) {
                Object networkManager = NETWORK_MANAGER_FIELD.get(playerConnection);
                return CHANNEL_FIELD.get(networkManager);
            }
            return null;

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object createGameProfile(UUID uuid, String name) {
        try {
            return GAME_PROFILE_CONSTRUCTOR.newInstance(uuid, name);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object stringToComponent(String string) {
        try {
            return ((Object[]) FROM_STRING_METHOD.invoke(null, string, true))[0];
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {

            Object entityPlayer = PLAYER_GET_METHOD.invoke(player);
            Object playerConnection = PLAYER_CONNECTION_FIELD.get(entityPlayer);
            SEND_PACKET_METHOD.invoke(playerConnection, packet);

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getNMSUtilClass(String name) {
        String prefix = SUPPORTED_VERSION == SupportedVersion.V1_7_10 ? "net.minecraft.util." : "";
        Class<?> clazz = findClass(prefix + name);
        if (clazz == null) {
            throw new IllegalStateException("Could not find bukkit class: " + name);
        }
        return clazz;
    }

    public static Class<?> getBukkitClass(String name) {
        Class<?> clazz = findClass(BUKKIT_CLASS_PATH + "." + name);
        if (clazz == null) {
            throw new IllegalStateException("Could not find bukkit class: " + name);
        }
        return clazz;
    }

    public static Class<?> getNMSClass(String modernPath, String... names) {
        Class<?> clazz = getNMSClassOrNull(modernPath, names);
        if (clazz == null) {
            throw new RuntimeException("Could not find NMS class: " + Arrays.toString(names));
        }
        return clazz;
    }

    public static Class<?> getNMSClassOrNull(String modernPath, String... names) {
        for (String name : names) {
            Class<?> clazz = findClass(NMS_CLASS_PATH + (MODERN_PACKAGING && modernPath != null ? "." + modernPath + "." : ".") + name);

            if (clazz != null) {
                return clazz;
            }
        }
        return null;
    }

    public static EnumSet<?> getEnumSet(Class<?> enumClass, Enum<?>... enums) {
        EnumSet actions = EnumSet.noneOf((Class) enumClass);
        actions.addAll(Arrays.asList(enums));
        return actions;
    }

    public static Enum<?> findEnumConstant(Class<?> clazz, int index) {
        return (Enum<?>) clazz.getEnumConstants()[index];
    }

    public static Field streamFieldsFind(Class<?> clazz, Predicate<Field> predicate, boolean inheritedFields, boolean accessible, int index) {
        return Arrays.stream(inheritedFields ? findAllInheritedFields(clazz) : clazz.getDeclaredFields()).filter(predicate).peek(field -> field.setAccessible(accessible)).toArray(Field[]::new)[index];
    }

    public static Field streamFieldsFindFirst(Class<?> clazz, Predicate<Field> predicate, boolean inheritedFields, boolean accessible) {
        return Arrays.stream(inheritedFields ? findAllInheritedFields(clazz) : clazz.getDeclaredFields()).filter(predicate).peek(field -> field.setAccessible(accessible)).findFirst().orElse(null);
    }

    public static Method streamMethodsFindFirst(Class<?> clazz, Predicate<Method> predicate, boolean inheritedMethods, boolean accessible) {
        return Arrays.stream(inheritedMethods ? findAllInheritedMethods(clazz) : clazz.getDeclaredMethods()).filter(predicate).peek(method -> method.setAccessible(accessible)).findFirst().orElse(null);
    }

    public static Field[] findAllInheritedFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();

        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    public static Method[] findAllInheritedMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();

        while (clazz != null) {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            clazz = clazz.getSuperclass();
        }
        return methods.toArray(new Method[0]);
    }

    public static Class<?> findClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
