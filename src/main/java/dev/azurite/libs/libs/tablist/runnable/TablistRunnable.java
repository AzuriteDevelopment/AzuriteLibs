package dev.azurite.libs.libs.tablist.runnable;

import dev.azurite.libs.libs.tablist.Tablist;
import dev.azurite.libs.libs.tablist.TablistLib;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TablistRunnable extends Thread {

    private final TablistLib module;

    public TablistRunnable(TablistLib module) {
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
