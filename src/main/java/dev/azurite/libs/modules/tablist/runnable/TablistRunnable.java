package dev.azurite.libs.modules.tablist.runnable;

import dev.azurite.libs.modules.tablist.Tablist;
import dev.azurite.libs.modules.tablist.TablistModule;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TablistRunnable extends Thread {

    private final TablistModule module;

    public TablistRunnable(TablistModule module) {
        this.module = module;
    }

    @Override
    public void run() {
        for (Tablist tablist : module.getCachedData().values()) {
            if (tablist.isInitialized()) {
                tablist.update();
            }
        }
    }
}
