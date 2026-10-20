package dev.azurite.libs.modules.tablist;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.ticking.TickingModule;
import dev.azurite.libs.modules.tablist.adapter.TablistAdapter;
import dev.azurite.libs.modules.tablist.listener.TablistListener;
import dev.azurite.libs.modules.tablist.runnable.TablistRunnable;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class TablistModule extends TickingModule<AzuriteLibs, Tablist, TablistAdapter> {

    public TablistModule(AzuriteLibs azuriteLibs) {
        super(azuriteLibs, "azurite_tablist");
        new TablistListener(this).register();
    }

    @Override
    public Runnable getRunnable() {
        return new TablistRunnable(this);
    }
}
