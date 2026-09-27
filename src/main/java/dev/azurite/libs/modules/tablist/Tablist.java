package dev.azurite.libs.modules.tablist;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.tablist.entry.TablistEntry;
import dev.azurite.libs.modules.tablist.reflection.TablistReflection;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@SuppressWarnings("UnstableApiUsage")
public class Tablist extends SubModule<AzuriteLibs, TablistModule> {

    private final Table<Integer, Integer, TablistEntry> entries;
    //private final TablistReflection packets;

    public Tablist(TablistModule module) {
        super(module);
        this.entries = Tables.newCustomTable(new ConcurrentHashMap<>(), ConcurrentHashMap::new);
       // this.packets = new TablistReflection(null, module);
    }

    public void tick() {
        entries.clear();
        module.getAdapter().updateEntries(this);

        for (Table.Cell<Integer, Integer, TablistEntry> cell : entries.cellSet()) {
            Integer column = cell.getRowKey();
            Integer row = cell.getColumnKey();
            TablistEntry entry = cell.getValue();

        }
    }
}
