package dev.azurite.libs.loader.ticking;

import dev.azurite.libs.loader.Module;
import dev.azurite.libs.loader.ModuleLoader;
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
public abstract class TickingModule<L extends ModuleLoader, S, A> extends Module<L> {

    protected Map<UUID, S> cachedData;
    protected A adapter;

    protected ScheduledExecutorService service;
    protected Future<?> serviceSchedule;

    public TickingModule(L loader, String threadName) {
        super(loader);
        this.cachedData = new ConcurrentHashMap<>();
        this.adapter = null;
        this.service = Executors.newScheduledThreadPool(1, new NamedThreadFactory(threadName));
        this.serviceSchedule = null;
    }

    public abstract Runnable getRunnable();

    public boolean isEnabled() {
        return adapter != null;
    }

    public S getCachedData(UUID uuid) {
        return cachedData.get(uuid);
    }

    public void startOrSetTickingTime(long millis) {
        if (serviceSchedule != null) {
            serviceSchedule.cancel(true);
        }
        Runnable runnable = getRunnable();
        this.serviceSchedule = service.scheduleAtFixedRate(runnable, 0L, millis, TimeUnit.MILLISECONDS);
    }
}
