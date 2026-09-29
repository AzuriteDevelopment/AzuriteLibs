package dev.azurite.libs.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class NMSUtils {

    public static final String BUKKIT_CLASS_PATH;
    public static final String NMS_CLASS_PATH;
    public static final boolean MODERN_PACKAGING;

    public static final Class<?> CRAFT_PLAYER_CLASS;
    public static final Class<?> CRAFT_CHAT_MESSAGE_CLASS;
    public static final Class<?> ENTITY_PLAYER_CLASS;
    public static final Class<?> PLAYER_CONNECTION_CLASS;
    public static final Class<?> PACKET_CLASS;

    public static final Method PLAYER_GET_METHOD;
    public static final Method FROM_STRING_METHOD;
    public static final Method SEND_PACKET_METHOD;

    public static final Field PLAYER_CONNECTION_FIELD;

    static {
        try {

            Class<?> MODERN_NMS_CLASS = findClass("net.minecraft.server.Main");
            BUKKIT_CLASS_PATH = Bukkit.getServer().getClass().getPackage().getName();
            NMS_CLASS_PATH = MODERN_NMS_CLASS != null ? "net.minecraft" : "net.minecraft.server." + BUKKIT_CLASS_PATH.substring(BUKKIT_CLASS_PATH.lastIndexOf('.') + 1);
            MODERN_PACKAGING = MODERN_NMS_CLASS != null;

            CRAFT_PLAYER_CLASS = getBukkitClass("entity.CraftPlayer");
            CRAFT_CHAT_MESSAGE_CLASS = getBukkitClass("util.CraftChatMessage");
            ENTITY_PLAYER_CLASS = getNMSClass("server.level", "EntityPlayer", "ServerPlayer");
            PLAYER_CONNECTION_CLASS = getNMSClass("server.network", "PlayerConnection", "ServerGamePacketListenerImpl");
            PACKET_CLASS = getNMSClass("network.protocol", "Packet");

            PLAYER_GET_METHOD = CRAFT_PLAYER_CLASS.getMethod("getHandle");
            FROM_STRING_METHOD = CRAFT_CHAT_MESSAGE_CLASS.getMethod("fromString", String.class);
            SEND_PACKET_METHOD = NMSUtils.streamMethodsFindFirst(PLAYER_CONNECTION_CLASS, method -> method.getParameterCount() == 1 && method.getParameterTypes()[0] == PACKET_CLASS && method.getReturnType() == void.class, true, true);

            PLAYER_CONNECTION_FIELD = NMSUtils.streamFieldsFindFirst(ENTITY_PLAYER_CLASS, field -> field.getType() == PLAYER_CONNECTION_CLASS, false, true);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object stringToComponent(String string) {
        try {
            return ((Object[]) FROM_STRING_METHOD.invoke(null, string))[0];
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
