package dev.azurite.libs.modules.tablist.utils;

import org.bukkit.Bukkit;

import java.util.Arrays;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class ClassUtils {

    private static final String BUKKIT_CLASS_PATH;
    private static final String NMS_CLASS_PATH;
    private static final boolean MODERN_PACKAGING;

    static {
        BUKKIT_CLASS_PATH = Bukkit.getServer().getClass().getPackage().getName();
        Class<?> MODERN_NMS_CLASS = findClass("net.minecraft.server.Main");
        NMS_CLASS_PATH = MODERN_NMS_CLASS != null ? "net.minecraft" : "net.minecraft.server." + BUKKIT_CLASS_PATH.substring(BUKKIT_CLASS_PATH.lastIndexOf('.') + 1);
        MODERN_PACKAGING = MODERN_NMS_CLASS != null;
    }

    public static Class<?> getBukkitClass(String name) {
        Class<?> clazz = findClass(BUKKIT_CLASS_PATH + "." + name);
        if (clazz == null) {
            throw new IllegalStateException("Cannot find bukkit class: " + name);
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
            Class<?> clazz = findClass(NMS_CLASS_PATH + (MODERN_PACKAGING ? modernPath + "." : ".") + name);

            if (clazz != null) {
                return clazz;
            }
        }
        return null;
    }

    public static Class<?> findClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
