package dev.azurite.libs.utils;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NamedThreadFactory implements ThreadFactory {

    private final String name;
    private int counter;

    public NamedThreadFactory(String name) {
        this.name = name;
        this.counter = 0;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        return new Thread(r, name + "-" + counter++);
    }
}
