package dev.azurite.libs.framework.module;

import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class Module<L extends ModuleLoader> {

    protected final L loader;

    public Module(L loader) {
        this.loader = loader;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }
}
