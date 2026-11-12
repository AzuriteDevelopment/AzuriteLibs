package dev.azurite.libs.utils;

import jdk.internal.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NMSAllocator {

    private static final Unsafe UNSAFE;

    static {
        try {

            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Allocator getAllocator(Class<?> clazz) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        try {
            MethodHandle constructor = lookup.findConstructor(clazz, MethodType.methodType(void.class));
            return constructor::invoke;

        } catch (NoSuchMethodException | IllegalAccessException e) {
            return () -> UNSAFE.allocateInstance(clazz);
        }
    }

    public static interface Allocator {

        Object allocate() throws Throwable;

    }
}
