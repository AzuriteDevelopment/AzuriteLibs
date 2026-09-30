package dev.azurite.libs.modules.tablist.entry;

import com.mojang.authlib.GameProfile;
import dev.azurite.libs.modules.tablist.skin.TablistSkin;
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

    private UUID uuid;
    private String display;
    private GameProfile profile;
    private TablistSkin skin;

    private int ping;
    private boolean dirty;

    public TablistEntry(UUID uuid, String display, GameProfile profile, TablistSkin skin, int ping) {
        this.uuid = uuid;
        this.display = display;
        this.profile = profile;
        this.skin = skin;
        this.ping = ping;
        this.dirty = true;
    }
}
