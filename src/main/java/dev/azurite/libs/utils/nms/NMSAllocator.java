package dev.azurite.libs.utils.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NMSAllocator {

    private static Object UNSAFE;
    private static Method ALLOCATE_METHOD;

    static {
        try {

            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            UNSAFE = theUnsafeField.get(null);
            ALLOCATE_METHOD = UNSAFE.getClass().getDeclaredMethod("allocateInstance", Class.class);

        } catch (Exception e) {
            UNSAFE = null;
            ALLOCATE_METHOD = null;
        }
    }

    public static Allocator getAllocator(Class<?> clazz) {
        return getAllocator(clazz, true);
    }

    public static Allocator getAllocator(Class<?> clazz, boolean useUnsafe) {
        try {
            for (Constructor<?> declaredConstructor : clazz.getDeclaredConstructors()) {
                if (declaredConstructor.getParameterCount() == 0) {
                    declaredConstructor.setAccessible(true);
                    return declaredConstructor::newInstance;
                }
            }
            if (useUnsafe && UNSAFE != null) {
                return () -> ALLOCATE_METHOD.invoke(UNSAFE, clazz);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface Allocator {

        Object allocate() throws Throwable;

    }
}
