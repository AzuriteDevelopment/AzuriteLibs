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
public abstract class TickingModule<L extends ModuleLoader, C, S, A> extends Module<L> {

    protected Map<UUID, C> cachedData;
    protected S settings;
    protected A adapter;

    protected ScheduledExecutorService service;
    protected Future<?> serviceSchedule;

    protected boolean enabled;

    public TickingModule(L loader, String threadName) {
        super(loader);
        this.cachedData = new ConcurrentHashMap<>();
        this.service = Executors.newScheduledThreadPool(1, new NamedThreadFactory(threadName));
        this.settings = null;
        this.adapter = null;
        this.serviceSchedule = null;
        this.enabled = false;
    }

    public abstract void enable(S settings);

    public C getCachedData(UUID uuid) {
        return cachedData.get(uuid);
    }

    public void enableAndStartTicking(long millis, Runnable runnable) {
        if (serviceSchedule != null) {
            serviceSchedule.cancel(true);
        }
        if (millis != 0 && adapter != null) {
            this.serviceSchedule = service.scheduleAtFixedRate(runnable, 0L, millis, TimeUnit.MILLISECONDS);
            this.enabled = true;
        }
    }
}
