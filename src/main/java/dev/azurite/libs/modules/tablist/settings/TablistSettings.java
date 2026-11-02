package dev.azurite.libs.modules.tablist.settings;

import dev.azurite.libs.modules.tablist.adapter.TablistAdapter;
import lombok.Builder;
import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Builder
@Getter
public class TablistSettings {

    private TablistAdapter adapter;
    private long tickTime;
    private boolean modernHideRealPlayers;

}
