package dev.azurite.libs.modules.commands.command;

import dev.azurite.libs.modules.commands.CommandModule;
import dev.azurite.libs.modules.commands.annotations.AzuriteSubCommand;
import dev.azurite.libs.modules.commands.command.base.BaseCommand;
import lombok.Getter;

import java.lang.reflect.Method;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class SubCommand extends BaseCommand {

    private final String[] names;

    public SubCommand(CommandModule module, AzuriteSubCommand annotation, Method method) {
        super(module, method, annotation.permission(), annotation.senderType(), annotation.async());
        this.names = annotation.names();
    }
}
