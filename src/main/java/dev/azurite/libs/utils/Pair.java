package dev.azurite.libs.utils;

import lombok.Getter;

import java.util.Objects;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class Pair<L, R> {

    private final L left;
    private final R right;

    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) object;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
