package dev.azurite.libs.modules.commands.utils;

import dev.azurite.libs.modules.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@SuppressWarnings("unchecked")
public class CommandUtils {

    private static final Method GET_COMMAND_MAP;
    private static final Field KNOWN_COMMANDS;

    static {
        try {

            GET_COMMAND_MAP = Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap");
            KNOWN_COMMANDS = SimpleCommandMap.class.getDeclaredField("knownCommands");
            KNOWN_COMMANDS.setAccessible(true);

        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static CommandMap getCommandMap() {
        try {
            return (CommandMap) GET_COMMAND_MAP.invoke(Bukkit.getServer());
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearCommand(String command) {
        try {
            ((Map<String, Command>) KNOWN_COMMANDS.get(getCommandMap())).remove(command);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
