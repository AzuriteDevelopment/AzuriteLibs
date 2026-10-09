package dev.azurite.libs.modules.tablist.entry;

import com.mojang.authlib.GameProfile;
import dev.azurite.libs.modules.tablist.skin.TablistSkin;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
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
    private String profileName;
    private String numberName;
    private GameProfile profile;

    private TablistSkin skin;
    private TablistEntry oldValue;

    private int ping;

    public TablistEntry(TablistEntry old) {
        this(old.getUuid(), old.getDisplay(), old.getProfileName(), old.getNumberName(), old.getProfile(), old.getSkin(), old.getPing());
    }

    public TablistEntry(UUID uuid, String display, String profileName, String numberName, GameProfile profile, TablistSkin skin, int ping) {
        this.uuid = uuid;
        this.display = display;
        this.profile = profile;
        this.profileName = profileName;
        this.numberName = numberName;
        this.skin = skin;
        this.ping = ping;
        this.oldValue = null;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        TablistEntry entry = (TablistEntry) object;
        return ping == entry.ping && Objects.equals(uuid, entry.uuid) && Objects.equals(display, entry.display) && Objects.equals(profile, entry.profile) && Objects.equals(skin, entry.skin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, display, profile, skin, ping);
    }
}
