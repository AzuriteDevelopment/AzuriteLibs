package dev.azurite.libs.modules.commands.command.base;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.commands.CommandModule;
import dev.azurite.libs.modules.commands.command.Sender;
import dev.azurite.libs.modules.commands.command.api.CommandClass;
import dev.azurite.libs.utils.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
@SuppressWarnings("unchecked")
public class BaseCommand extends SubModule<AzuriteLibs, CommandModule> {

    protected Method method;
    protected Method tabComplete;
    protected String permission;
    protected Sender sender;
    protected boolean async;
    protected boolean autoMatchTab;
    protected boolean subCommandsTab;

    public BaseCommand(CommandModule module, Method method, String permission, Sender sender, boolean async) {
        super(module);
        this.method = method;
        this.permission = permission;
        this.sender = sender;
        this.async = async;
    }

    public boolean isInsufficientPermission(CommandSender sender, boolean sendMessage) {
        if (!permission.isEmpty() && !sender.hasPermission(permission)) {
            if (sendMessage) sender.sendMessage(module.getNoPermissionMessage());
            return true;
        }
        return false;
    }

    public boolean isInvalid(CommandSender sender, boolean sendMessage) {
        Class<?> validType = this.sender.getValidType();
        boolean valid = validType == null || validType.isInstance(sender);

        if (sendMessage && !valid) {
            sender.sendMessage(CC.t(this.sender.getNotValidMessage().apply(module)));
        }
        return !valid;
    }

    public List<String> invokeExecutionTabComplete(CommandClass commandClass, CommandSender sender, String[] args) {
        if (tabComplete == null) {
            return null;
        }
        try {
            return (List<String>) tabComplete.invoke(commandClass, sender, args);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void invokeExecution(CommandClass commandClass, CommandSender sender, String[] args) {
        try {
            switch (this.sender) {
                case PLAYER_ONLY:
                    Player player = (Player) sender;
                    method.invoke(commandClass, player, args);
                    break;

                case CONSOLE_ONLY:
                    ConsoleCommandSender console = (ConsoleCommandSender) sender;
                    method.invoke(commandClass, console, args);
                    break;

                case ANY:
                    method.invoke(commandClass, sender, args);
                    break;
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
