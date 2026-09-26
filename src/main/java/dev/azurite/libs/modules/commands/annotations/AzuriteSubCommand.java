package dev.azurite.libs.modules.commands.annotations;

import dev.azurite.libs.modules.commands.command.Sender;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AzuriteSubCommand {

    String mainCommand();

    String[] names();

    String permission() default "";

    Sender senderType() default Sender.ANY;

    boolean async() default false;

}
