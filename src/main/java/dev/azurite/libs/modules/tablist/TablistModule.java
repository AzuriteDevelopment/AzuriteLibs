package dev.azurite.libs.modules.tablist;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.ticking.TickingModule;
import dev.azurite.libs.modules.tablist.adapter.TablistAdapter;
import dev.azurite.libs.modules.tablist.listener.TablistListener;
import dev.azurite.libs.modules.tablist.runnable.TablistRunnable;
import dev.azurite.libs.modules.tablist.settings.TablistSettings;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TablistModule extends TickingModule<AzuriteLibs, Tablist, TablistSettings, TablistAdapter> {

    public TablistModule(AzuriteLibs azuriteLibs) {
        super(azuriteLibs, "azurite_tablist");
        new TablistListener(this).register();
    }

    @Override
    public void enable(TablistSettings settings) {
        this.settings = settings;
        this.adapter = settings.getAdapter();
        this.enableAndStartTicking(settings.getTickTime(), new TablistRunnable(this));
    }
}
