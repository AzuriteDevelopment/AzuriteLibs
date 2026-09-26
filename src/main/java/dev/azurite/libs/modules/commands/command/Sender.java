package dev.azurite.libs.modules.commands.command;

import dev.azurite.libs.modules.commands.CommandModule;
import lombok.Getter;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public enum Sender {

    PLAYER_ONLY(CommandModule::getOnlyPlayerMessage, Player.class),
    CONSOLE_ONLY(CommandModule::getOnlyConsoleMessage, ConsoleCommandSender.class),
    ANY(null, null);

    private final Function<CommandModule, String> notValidMessage;
    private final Class<?> validType;

    Sender(Function<CommandModule, String> notValidMessage, Class<?> validType) {
        this.notValidMessage = notValidMessage;
        this.validType = validType;
    }
}
