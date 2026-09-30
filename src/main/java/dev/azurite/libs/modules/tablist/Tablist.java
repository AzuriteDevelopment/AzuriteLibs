package dev.azurite.libs.modules.tablist;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.mojang.authlib.GameProfile;
import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.tablist.entry.TablistEntry;
import dev.azurite.libs.modules.tablist.reflection.TablistReflection;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
@SuppressWarnings("UnstableApiUsage")
public class Tablist extends SubModule<AzuriteLibs, TablistModule> {

    private final Player player;
    private final Table<Integer, Integer, TablistEntry> entries;

    private final TablistReflection reflection;
    private final AtomicBoolean initialized;

    private int maxColumns;

    public Tablist(TablistModule module, Player player) {
        super(module);
        this.player = player;
        this.entries = Tables.newCustomTable(new ConcurrentHashMap<>(80), ConcurrentHashMap::new);
        this.initialized = new AtomicBoolean(false);
        this.reflection = new TablistReflection(module, this);
    }

    public void setEntry(int col, int row, String display) {
        this.setEntry(col, row, display, -1);
    }

    public void setEntry(int col, int row, String display, int ping) {
        TablistEntry entry = entries.get(col, row);
        entry.setDisplay(display);
        entry.setPing(ping);
    }

    public void forEachEntry(Consumer<TablistEntry> consumer) {
        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < maxColumns; col++) {
                TablistEntry entry = entries.get(col, row);

                if (entry == null) {
                    UUID uuid = UUID.randomUUID();
                    String name = reflection.getTablistEntryName(col, row);
                    entry = new TablistEntry(uuid, name, new GameProfile(uuid, name), -1);
                }

                consumer.accept(entry);
            }
        }
    }

    public void tick() {
        module.getAdapter().updateEntries(this);

        for (Table.Cell<Integer, Integer, TablistEntry> cell : entries.cellSet()) {
            Integer column = cell.getRowKey();
            Integer row = cell.getColumnKey();
            TablistEntry entry = cell.getValue();

        }
    }
}
