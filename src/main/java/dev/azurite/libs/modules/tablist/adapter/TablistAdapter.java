package dev.azurite.libs.modules.tablist.adapter;

import dev.azurite.libs.modules.tablist.Tablist;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public interface TablistAdapter {

    String[] getHeader();

    String[] getFooter();

    void updateEntries(Tablist tablist);

}
