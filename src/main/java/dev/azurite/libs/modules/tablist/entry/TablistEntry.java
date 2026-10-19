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
public class TablistEntry extends OldEntry {

    private UUID uuid;
    private String profileName;
    private String numberName;
    private GameProfile profile;
    private OldEntry oldEntry;

    public TablistEntry(String display, String profileName, String numberName, TablistSkin skin, int ping) {
        super(display, skin, ping);
        this.uuid = null;
        this.profile = null;
        this.profileName = profileName;
        this.numberName = numberName;
        this.oldEntry = null;
    }
}
