package dev.azurite.libs.modules.tablist;

import com.google.common.collect.Table;
import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.tablist.adapter.TablistAdapter;
import dev.azurite.libs.modules.tablist.entry.OldEntry;
import dev.azurite.libs.modules.tablist.entry.TablistEntry;
import dev.azurite.libs.modules.tablist.injector.TablistInjector;
import dev.azurite.libs.modules.tablist.skin.Skins;
import dev.azurite.libs.modules.tablist.skin.TablistSkin;
import dev.azurite.libs.utils.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class Tablist extends SubModule<AzuriteLibs, TablistModule> {

    private final Player player;
    private final TablistInjector injector;

    private Table<Integer, Integer, TablistEntry> entries;

    private String[] currentHeader;
    private String[] currentFooter;

    private volatile boolean initialized;
    private int maxColumns;

    public Tablist(TablistModule module, Player player) {
        super(module);
        this.player = player;
        this.injector = new TablistInjector(module, this);
        this.entries = null;
        this.currentHeader = null;
        this.currentFooter = null;
        this.initialized = false;
        this.maxColumns = 4;
    }

    public void setEntry(int col, int row, String display) {
        this.setEntry(col, row, display, -1);
    }

    public void setEntry(int col, int row, String display, int ping) {
        this.setEntry(col, row, display, ping, Skins.GRAY);
    }

    public void setEntry(int col, int row, String display, TablistSkin skin) {
        this.setEntry(col, row, display, -1, skin);
    }

    public void setEntry(int col, int row, String display, int ping, TablistSkin skin) {
        TablistEntry entry = entries.get(col, row);
        display = CC.t(display);

        if (entry == null) {
            entry = new TablistEntry(display, skin, col, row, ping);
            entries.put(col, row, entry);

        } else {
            entry.setDisplay(display);
            entry.setPing(ping);
            entry.setSkin(skin);
        }
    }

    public void update() {
        TablistAdapter adapter = module.getAdapter();
        int validColumns = adapter.getColumns(player, injector.getVersion()) - 1;

        adapter.updateEntries(player, this);

        if (maxColumns == 4) {
            String[] header = adapter.getHeader(player);
            String[] footer = adapter.getFooter(player);

            if (!Arrays.equals(currentHeader, header) || !Arrays.equals(currentFooter, footer)) {
                this.currentHeader = header;
                this.currentFooter = footer;
                injector.sendHeaderFooter(player, String.join("\n", CC.t(header)), String.join("\n", CC.t(footer)));
            }
        }

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < maxColumns; col++) {
                TablistEntry entry = entries.get(col, row);

                if (col > validColumns) {
                    if (entry.isRemoved()) {
                        continue;
                    }

                    injector.sendTablistRemove(player, entry);
                    entry.setRemoved(true);
                    entry.setOldEntry(null);
                    continue;
                }

                if (entry.isRemoved()) {
                    injector.sendTablistAdd(player, entry);
                    entry.setRemoved(false);
                }

                injector.sendTablistUpdate(player, entry);
                entry.setOldEntry(new OldEntry(entry));
            }
        }
    }
}
