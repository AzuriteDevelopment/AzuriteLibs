package dev.azurite.libs.modules.commands.command.base;

import dev.azurite.libs.modules.commands.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class SpigotCommand extends BukkitCommand {

    private final Command command;

    public SpigotCommand(Command command) {
        super(command.getName());
        this.command = command;
        this.setAliases(Arrays.asList(command.getAliases()));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        command.execute(sender, args);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        List<String> completions = command.tabComplete(sender, args);
        if (completions != null) {
            return completions;
        }
        return super.tabComplete(sender, alias, args);
    }
}
