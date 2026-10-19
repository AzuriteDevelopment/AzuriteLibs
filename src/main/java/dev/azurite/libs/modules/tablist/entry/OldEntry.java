package dev.azurite.libs.modules.tablist.entry;

import dev.azurite.libs.modules.tablist.skin.TablistSkin;
import lombok.Getter;
import lombok.Setter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class OldEntry {

    private String display;
    private TablistSkin skin;
    private int ping;

    public OldEntry(TablistEntry entry) {
        this.display = entry.getDisplay();
        this.skin = entry.getSkin();
        this.ping = entry.getPing();
    }

    public OldEntry(String display, TablistSkin skin, int ping) {
        this.display = display;
        this.skin = skin;
        this.ping = ping;
    }
}
