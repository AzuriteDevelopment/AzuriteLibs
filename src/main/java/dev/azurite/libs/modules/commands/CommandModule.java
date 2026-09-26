package dev.azurite.libs.modules.commands;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.Module;
import dev.azurite.libs.modules.commands.annotations.AzuriteCommand;
import dev.azurite.libs.modules.commands.annotations.AzuriteSubCommand;
import dev.azurite.libs.modules.commands.annotations.TabComplete;
import dev.azurite.libs.modules.commands.command.SubCommand;
import dev.azurite.libs.modules.commands.command.api.CommandClass;
import dev.azurite.libs.modules.commands.comparator.MethodComparator;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class CommandModule extends Module<AzuriteLibs> {

    private final Map<String, Command> commands;
    private String noPermissionMessage;
    private String onlyPlayerMessage;
    private String onlyConsoleMessage;

    public CommandModule(AzuriteLibs azuriteLibs) {
        super(azuriteLibs);
        this.commands = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.noPermissionMessage = "You do not have permission to use this command";
        this.onlyPlayerMessage = "Only players can use this command";
        this.onlyConsoleMessage = "Only console can use this command";
    }

    public void unregisterAllCommands() {
        for (Command command : commands.values()) {
            command.unregister();
        }
        this.commands.clear();
    }

    public void unregisterCommand(String name) {
        Command command = commands.remove(name);

        if (command != null) {
            command.unregister();
        }
    }

    public void registerCommands(String prefix, List<CommandClass> commandClasses) {
        for (CommandClass commandClass : commandClasses) {
            this.registerCommand(prefix, commandClass);
        }
    }

    public void registerCommands(String prefix, CommandClass... commandClasses) {
        for (CommandClass commandClass : commandClasses) {
            this.registerCommand(prefix, commandClass);
        }
    }

    public void registerCommand(String prefix, CommandClass commandClass) {
        List<Method> methods = this.findMethods(commandClass);
        List<Command> registering = new ArrayList<>();

        // Load main commands and remove since we process sub commands after
        iterate(methods, iterator -> {
            Method method = iterator.next();
            AzuriteCommand annotation = method.getAnnotation(AzuriteCommand.class);

            if (annotation != null) {
                Command command = new Command(this, annotation, method, prefix, commandClass);
                registering.add(command);
                commands.put(annotation.name(), command);
                iterator.remove();
            }
        });

        // Load sub commands
        iterate(methods, iterator -> {
            Method method = iterator.next();
            AzuriteSubCommand annotation = method.getAnnotation(AzuriteSubCommand.class);

            if (annotation != null) {
                Command command = commands.get(annotation.mainCommand());
                command.getSubCommands().add(new SubCommand(this, annotation, method));
                iterator.remove();
            }
        });

        // Load tab completes
        iterate(methods, iterator -> {
            Method method = iterator.next();
            TabComplete tabComplete = method.getAnnotation(TabComplete.class);

            if (tabComplete != null) {
                Command command = commands.get(tabComplete.command());
                command.setTabComplete(method);
                command.setAutoMatchTab(tabComplete.autoMatch());
                command.setSubCommandsTab(tabComplete.autoSubCommands());
                iterator.remove();
            }
        });

        // Register all commands
        for (Command command : registering) {
            command.register();
        }
    }

    private void iterate(List<Method> methods, Consumer<Iterator<Method>> consumer) {
        Iterator<Method> iterator = methods.iterator();

        while (iterator.hasNext()) {
            consumer.accept(iterator);
        }
    }

    private List<Method> findMethods(CommandClass commandClass) {
        List<Method> methods = new ArrayList<>(Arrays.asList(commandClass.getClass().getMethods()));

        methods.removeIf(method -> {
            if (method.isAnnotationPresent(AzuriteCommand.class) || method.isAnnotationPresent(AzuriteSubCommand.class)) {
                return false;
            }
            return !method.isAnnotationPresent(TabComplete.class);
        });

        methods.sort(new MethodComparator());
        return methods;
    }
}
