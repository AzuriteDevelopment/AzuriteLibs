package dev.azurite.libs.modules.tablist.entry;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class TablistEntry {

    private UUID id;
    private String display;
    private GameProfile profile;
    private int ping;

    public TablistEntry(UUID id, String display, GameProfile profile, int ping) {
        this.id = id;
        this.display = display;
        this.profile = profile;
        this.ping = ping;
    }
}
