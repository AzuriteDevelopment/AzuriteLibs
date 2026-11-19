package dev.azurite.libs.libs.tablist;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.framework.lib.ticking.TickingLib;
import dev.azurite.libs.libs.tablist.adapter.TablistAdapter;
import dev.azurite.libs.libs.tablist.listener.TablistListener;
import dev.azurite.libs.libs.tablist.runnable.TablistRunnable;
import dev.azurite.libs.libs.tablist.settings.TablistSettings;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TablistLib extends TickingLib<AzuriteLibs, Tablist, TablistSettings, TablistAdapter> {

    public TablistLib(AzuriteLibs azuriteLibs) {
        super(azuriteLibs, "azurite_tablist");
    }

    @Override
    public void enable(TablistSettings settings) {
        this.checkEnabled();
        this.settings = settings;
        this.adapter = settings.getAdapter();
        this.loader.registerListener(new TablistListener(this));
        this.enableAndStartTicking(settings.getTickTime(), new TablistRunnable(this));
    }
}
