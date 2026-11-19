package dev.azurite.libs.libs.commands.command;

import dev.azurite.libs.libs.commands.CommandLib;
import dev.azurite.libs.libs.commands.annotations.AzuriteSubCommand;
import dev.azurite.libs.libs.commands.command.base.BaseCommand;
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

    public SubCommand(CommandLib module, AzuriteSubCommand annotation, Method method) {
        super(module, method, annotation.permission(), annotation.senderType(), annotation.async());
        this.names = annotation.names();
    }
}
