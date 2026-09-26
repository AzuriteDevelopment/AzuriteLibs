package dev.azurite.libs.modules.commands.comparator;

import dev.azurite.libs.modules.commands.annotations.AzuriteCommand;
import dev.azurite.libs.modules.commands.annotations.AzuriteSubCommand;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class MethodComparator implements Comparator<Method> {

    @Override
    public int compare(Method o1, Method o2) {
        return Integer.compare(getWeight(o2), getWeight(o1));
    }

    private int getWeight(Method method) {
        if (method.isAnnotationPresent(AzuriteCommand.class)) {
            return 2;
        }
        if (method.isAnnotationPresent(AzuriteSubCommand.class)) {
            return 1;
        }
        return 0;
    }
}
