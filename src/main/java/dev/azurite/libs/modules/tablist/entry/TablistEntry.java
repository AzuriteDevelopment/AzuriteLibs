package dev.azurite.libs.modules.tablist.entry;

import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class TablistEntry {

    private final String display;
    private final int ping;

    public TablistEntry(String display, int ping) {
        this.display = display;
        this.ping = ping;
    }
}
