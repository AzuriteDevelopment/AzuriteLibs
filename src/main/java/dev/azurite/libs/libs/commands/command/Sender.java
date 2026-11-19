package dev.azurite.libs.libs.commands.command;

import dev.azurite.libs.libs.commands.CommandLib;
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

    PLAYER_ONLY(module -> module.getSettings().getOnlyPlayerMessage(), Player.class),
    CONSOLE_ONLY(module -> module.getSettings().getOnlyConsoleMessage(), ConsoleCommandSender.class),
    ANY(null, null);

    private final Function<CommandLib, String> notValidMessage;
    private final Class<?> validType;

    Sender(Function<CommandLib, String> notValidMessage, Class<?> validType) {
        this.notValidMessage = notValidMessage;
        this.validType = validType;
    }
}
