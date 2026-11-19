package dev.azurite.libs.utils.command;

import dev.azurite.libs.libs.commands.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@SuppressWarnings("unchecked")
public class CommandUtils {

    private static final CommandMap COMMAND_MAP;
    private static final Field KNOWN_COMMANDS;

    static {
        try {

            COMMAND_MAP = (CommandMap) Bukkit.getServer().getClass().getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
            KNOWN_COMMANDS = SimpleCommandMap.class.getDeclaredField("knownCommands");
            KNOWN_COMMANDS.setAccessible(true);

        } catch (NoSuchMethodException | NoSuchFieldException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void register(String prefix, BukkitCommand bukkitCommand) {
        COMMAND_MAP.register(prefix, bukkitCommand);
    }

    public static void unregister(String command) {
        try {
            ((Map<String, Command>) KNOWN_COMMANDS.get(COMMAND_MAP)).remove(command);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
