package dev.azurite.libs.framework.lib;

import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class Lib<L extends LibLoader> {

    protected final L loader;

    public Lib(L loader) {
        this.loader = loader;
    }
}
