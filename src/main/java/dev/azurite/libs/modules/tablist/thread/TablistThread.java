package dev.azurite.libs.modules.tablist.thread;

import dev.azurite.libs.modules.tablist.Tablist;
import dev.azurite.libs.modules.tablist.TablistModule;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TablistThread extends Thread {

    private final TablistModule module;

    public TablistThread(TablistModule module) {
        this.module = module;
    }

    @Override
    public void run() {
        for (Tablist tablist : module.getTablists().values()) {
            if (tablist.isInitialized()) {
                tablist.update();
            }
        }
    }
}
