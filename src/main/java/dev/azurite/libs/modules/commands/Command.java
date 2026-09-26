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
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
        if (bukkitCommand == null) this.bukkitCommand = new SpigotCommand(this);
        CommandUtils.register(prefix, bukkitCommand);
    }

    public void unregister() {
        this.bukkitCommand = null;
        CommandUtils.unregister(name);
    }

    public void execute(CommandSender sender, String[] args) {
        if (cannotUseCommand(sender, true)) return;

        if (args.length > 0) {
            SubCommand subCommand = this.getSubCommand(args[0]);

            if (subCommand != null) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                if (subCommand.cannotUseCommand(sender, true)) return;
                this.handleInvoke(subCommand, sender, subArgs);
                return;
            }
        }

        this.handleInvoke(this, sender, args);
    }

    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (cannotUseCommand(sender, false)) {
            return null;
        }

        List<String> completion = this.invokeExecutionTabComplete(commandClass, sender, args);

        if (!subCommands.isEmpty() && args.length == 1 && subCommandsTab) {
            completion = new ArrayList<>();

            for (SubCommand subCommand : subCommands) {
                if (subCommand.cannotUseCommand(sender, false)) continue;
                completion.addAll(Arrays.asList(subCommand.getNames()));
            }
        }

        if (completion != null && !completion.isEmpty()) {
            String string = args[args.length - 1];
            return completion
                    .stream()
                    .filter(s -> !autoMatchTab || s.regionMatches(true, 0, string, 0, string.length()))
                    .collect(Collectors.toList());
        }
        return null;
    }

    private void handleInvoke(BaseCommand command, CommandSender sender, String[] args) {
        if (command.isAsync()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> command.invokeExecution(commandClass, sender, args));
            return;
        }
        command.invokeExecution(commandClass, sender, args);
    }

    private SubCommand getSubCommand(String name) {
        for (SubCommand subCommand : subCommands) {
            for (String subCommandName : subCommand.getNames()) {
                if (subCommandName.equalsIgnoreCase(name)) return subCommand;
            }
        }
        return null;
    }
}
