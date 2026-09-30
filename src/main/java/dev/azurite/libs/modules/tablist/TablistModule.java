package dev.azurite.libs.modules.tablist;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.Module;
import dev.azurite.libs.modules.tablist.adapter.TablistAdapter;
import dev.azurite.libs.modules.tablist.listener.TablistListener;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class TablistModule extends Module<AzuriteLibs> {

    private final Map<UUID, Tablist> tablists;

    private TablistAdapter adapter;

    public TablistModule(AzuriteLibs azuriteLibs) {
        super(azuriteLibs);
        this.tablists = new ConcurrentHashMap<>();

        this.adapter = new TablistAdapter() {
            @Override
            public String[] getHeader() {
                return new String[0];
            }

            @Override
            public String[] getFooter() {
                return new String[0];
            }

            @Override
            public void updateEntries(Tablist tablist) {
            }
        };
        new TablistListener(this).register();
    }

    public Tablist getTablist(UUID uuid) {
        return tablists.get(uuid);
    }
}
