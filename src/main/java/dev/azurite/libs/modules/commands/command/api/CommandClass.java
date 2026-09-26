package dev.azurite.libs.modules.commands.command.api;

import dev.azurite.libs.utils.CC;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public interface CommandClass {

    default void sendMessage(CommandSender sender, List<String> messages) {
        for (String message : messages) {
            sendMessage(sender, message);
        }
    }

    default void sendMessage(CommandSender sender, String... messages) {
        for (String message : messages) {
            sendMessage(sender, message);
        }
    }

    default void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(CC.t(message));
    }

    default Integer parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    default Double parseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
