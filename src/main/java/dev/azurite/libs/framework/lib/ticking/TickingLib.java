package dev.azurite.libs.framework.lib.ticking;

import dev.azurite.libs.framework.lib.Lib;
import dev.azurite.libs.framework.lib.LibLoader;
import dev.azurite.libs.utils.threads.AzuriteThreadFactory;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public abstract class TickingLib<L extends LibLoader, C, S, A> extends Lib<L> {

    protected Map<UUID, C> cachedData;
    protected ScheduledExecutorService service;
    protected Future<?> serviceSchedule;

    protected S settings;
    protected A adapter;

    protected boolean enabled;

    public TickingLib(L loader, String threadName) {
        super(loader);
        this.cachedData = new ConcurrentHashMap<>();
        this.service = Executors.newScheduledThreadPool(1, new AzuriteThreadFactory(threadName));
        this.serviceSchedule = null;
        this.settings = null;
        this.adapter = null;
        this.enabled = false;
    }

    public abstract void enable(S settings);

    public C getCachedData(UUID uuid) {
        return cachedData.get(uuid);
    }

    protected void checkEnabled() {
        if (isEnabled()) {
            throw new IllegalStateException("Ticking is already enabled");
        }
    }

    protected void enableAndStartTicking(long millis, Runnable runnable) {
        if (serviceSchedule != null) {
            serviceSchedule.cancel(true);
        }
        if (millis != 0 && adapter != null) {
            this.serviceSchedule = service.scheduleAtFixedRate(runnable, 0L, millis, TimeUnit.MILLISECONDS);
            this.enabled = true;
        }
    }
}
