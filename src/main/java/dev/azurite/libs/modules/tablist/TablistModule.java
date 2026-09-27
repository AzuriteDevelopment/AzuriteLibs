package dev.azurite.libs.modules.tablist;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.Module;
import dev.azurite.libs.modules.tablist.adapter.TablistAdapter;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class TablistModule extends Module<AzuriteLibs> {

    private TablistAdapter adapter;

    public TablistModule(AzuriteLibs azuriteLibs) {
        super(azuriteLibs);
    }
}
