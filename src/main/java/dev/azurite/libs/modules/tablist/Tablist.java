package dev.azurite.libs.modules.tablist;

import com.google.common.collect.Table;
import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.tablist.adapter.TablistAdapter;
import dev.azurite.libs.modules.tablist.entry.TablistEntry;
import dev.azurite.libs.modules.tablist.injector.TablistInjector;
import dev.azurite.libs.modules.tablist.skin.DefaultSkins;
import dev.azurite.libs.modules.tablist.skin.TablistSkin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class Tablist extends SubModule<AzuriteLibs, TablistModule> {

    private final Player player;
    private final TablistInjector reflection;
    private final AtomicBoolean initialized;

    private Table<Integer, Integer, TablistEntry> entries;

    private String[] currentHeader;
    private String[] currentFooter;

    private int maxColumns;

    public Tablist(TablistModule module, Player player) {
        super(module);
        this.player = player;
        this.reflection = new TablistInjector(module, this);
        this.initialized = new AtomicBoolean(false);
        this.entries = null;
        this.currentHeader = null;
        this.currentFooter = null;
        this.maxColumns = 4;
    }

    public void setEntry(int col, int row, String display) {
        this.setEntry(col, row, display, -1);
    }

    public void setEntry(int col, int row, String display, int ping) {
        this.setEntry(col, row, display, ping, DefaultSkins.GRAY);
    }

    public void setEntry(int col, int row, String display, int ping, TablistSkin skin) {
        TablistEntry entry = entries.get(col, row);
        boolean dirty = false;

        if (!entry.getDisplay().equals(display)) {
            entry.setDisplay(display);
            dirty = true;
        }

        if (entry.getPing() != ping) {
            entry.setPing(ping);
            dirty = true;
        }

        if (entry.getSkin().equals(skin)) {
            entry.setSkin(skin);
            dirty = true;
        }

        entry.setDirty(dirty);
    }

    public void update() {
        TablistAdapter adapter = module.getAdapter();
        String[] header = adapter.getHeader();
        String[] footer = adapter.getFooter();

        adapter.updateEntries(this);

        if (!Arrays.equals(currentHeader, header) || !Arrays.equals(currentFooter, footer)) {
            currentHeader = header;
            currentFooter = footer;
            reflection.sendHeaderFooter(String.join("\n", header), String.join("\n", footer));
        }

        for (TablistEntry entry : entries.values()) {
            if (!entry.isDirty()) continue;

        }
    }
}
