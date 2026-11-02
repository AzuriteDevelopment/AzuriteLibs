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

    protected UUID uuid;
    protected GameProfile profile;
    protected OldEntry oldEntry;

    protected String profileName;
    protected String teamName;

    protected boolean removed;

    public TablistEntry(String display, TablistSkin skin, int col, int row, int ping) {
        super(display, skin, ping);

        this.uuid = null;
        this.profile = null;
        this.oldEntry = null;

        this.profileName = getProfileName(col, row);
        this.teamName = getTeamName(col, row);

        this.removed = false;
    }

    public boolean shouldUpdateDisplay() {
        return oldEntry == null || !oldEntry.getDisplay().equals(display);
    }

    public boolean shouldUpdatePing() {
        return oldEntry == null || oldEntry.getPing() != ping;
    }

    public boolean shouldUpdateSkin() {
        return oldEntry == null || !oldEntry.getSkin().equals(skin);
    }

    private String getProfileName(int col, int row) {
        StringBuilder builder = new StringBuilder("§" + col);

        for (char c : String.valueOf(row).toCharArray()) {
            builder.append("§").append(c);
        }
        return builder.toString();
    }

    private String getTeamName(int col, int row) {
        return String.format("%016d", (col * 20) + row);
    }
}
