package dev.azurite.libs.modules.tablist;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.Module;
import dev.azurite.libs.modules.tablist.adapter.TablistAdapter;
import dev.azurite.libs.modules.tablist.thread.TablistThread;
import dev.azurite.libs.utils.NamedThreadFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
@Setter
public class TablistModule extends Module<AzuriteLibs> {

    private final Map<UUID, Tablist> tablists;
    private final ScheduledExecutorService tablistThread;

    private ScheduledFuture<?> future;
    private TablistAdapter adapter;

    public TablistModule(AzuriteLibs azuriteLibs) {
        super(azuriteLibs);
        this.tablists = new ConcurrentHashMap<>();
        this.tablistThread = Executors.newScheduledThreadPool(1, new NamedThreadFactory("azurite_tablist"));
        this.future = null;
        this.adapter = null;
    }

    public Tablist getTablist(UUID uuid) {
        return tablists.get(uuid);
    }

    public void setTickingTime(long millis) {
        if (future != null) {
            future.cancel(true);
        }
        this.future = tablistThread.scheduleAtFixedRate(new TablistThread(this), 0L, millis, TimeUnit.MILLISECONDS);
    }
}
