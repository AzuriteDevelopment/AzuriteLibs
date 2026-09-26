package dev.azurite.libs.modules.commands;

import dev.azurite.libs.modules.commands.annotations.AzuriteCommand;
import dev.azurite.libs.modules.commands.command.SubCommand;
import dev.azurite.libs.modules.commands.command.api.CommandClass;
import dev.azurite.libs.modules.commands.command.base.BaseCommand;
import dev.azurite.libs.modules.commands.command.base.SpigotCommand;
import dev.azurite.libs.modules.commands.utils.CommandUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class Command extends BaseCommand {

    private CommandClass commandClass;
    private List<SubCommand> subCommands;

    private SpigotCommand bukkitCommand;
    private String prefix;
    private String name;
    private String[] aliases;

    public Command(CommandModule module, AzuriteCommand annotation, Method method, String prefix, CommandClass commandClass) {
        super(module, method, annotation.permission(), annotation.senderType(), annotation.async());

        this.commandClass = commandClass;
        this.bukkitCommand = null;
        this.subCommands = new ArrayList<>();

        this.prefix = prefix;
        this.name = annotation.name();
        this.aliases = annotation.aliases();
    }

    public void register() {
        if (bukkitCommand == null) {
            this.bukkitCommand = new SpigotCommand(this);
        }
        CommandMap commandMap = CommandUtils.getCommandMap();
        commandMap.register(prefix, bukkitCommand);
    }

    public void unregister() {
        CommandUtils.clearCommand(name);
        this.bukkitCommand = null;
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            SubCommand subCommand = this.getSubCommand(args[0]);

            if (subCommand != null) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                this.handleInvoke(subCommand, sender, subArgs);
                return;
            }
        }

        this.handleInvoke(this, sender, args);
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length > 0) {
            SubCommand subCommand = this.getSubCommand(args[0]);

        }

        return null;
    }

    private void handleInvoke(BaseCommand command, CommandSender sender, String[] args) {
        if (command.isInsufficientPermission(sender, true)) {
            return;
        }
        if (command.isInvalid(sender, true)) {
            return;
        }
        if (command.isAsync()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> command.invokeExecution(commandClass, sender, args));
            return;
        }
        command.invokeExecution(commandClass, sender, args);
    }

    public SubCommand getSubCommand(String name) {
        for (SubCommand subCommand : subCommands) {
            for (String subCommandName : subCommand.getNames()) {
                if (subCommandName.equalsIgnoreCase(name)) return subCommand;
            }
        }
        return null;
    }
}
