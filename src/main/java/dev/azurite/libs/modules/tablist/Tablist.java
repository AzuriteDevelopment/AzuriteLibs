package dev.azurite.libs.modules.tablist;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.sub.SubModule;
import dev.azurite.libs.modules.tablist.entry.TablistEntry;
import dev.azurite.libs.modules.tablist.reflection.TablistReflection;
import dev.azurite.libs.utils.NMSUtils;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@SuppressWarnings("UnstableApiUsage")
public class Tablist extends SubModule<AzuriteLibs, TablistModule> {

    private final Player player;
    private final Table<Integer, Integer, TablistEntry> entries;
    private final TablistReflection reflection;

    public Tablist(TablistModule module, Player player) {
        super(module);
        this.player = player;
        this.entries = Tables.newCustomTable(new ConcurrentHashMap<>(80), ConcurrentHashMap::new);
        this.reflection = new TablistReflection(module, player);
    }

    public void setEntry(int col, int row, String display) {

    }

    public void setEntry(int col, int row, String display, int ping) {
        TablistEntry entry = entries.get(col, row);
        entry.setDisplay(display);
        entry.setPing(ping);
    }

    public void createTablist() {
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 20; row++) {
                UUID uuid = UUID.randomUUID();
                TablistEntry entry = new TablistEntry(uuid, "", NMSUtils.createGameProfile(uuid, getName(col, row)), -1);
                entries.put(col, row, entry);
            }
        }
        reflection.sendCreationPacket(entries.values());
    }

    public String getName(int col, int row) {
        StringBuilder builder = new StringBuilder("§" + col);
        for (char c : String.valueOf(row).toCharArray()) {
            builder.append("§").append(c);
        }
        return builder.toString();
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
