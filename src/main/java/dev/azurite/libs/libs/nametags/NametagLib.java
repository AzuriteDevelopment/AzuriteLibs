package dev.azurite.libs.libs.nametags;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.framework.lib.ticking.TickingLib;
import dev.azurite.libs.libs.nametags.adapter.NametagAdapter;
import dev.azurite.libs.libs.nametags.listener.NametagListener;
import dev.azurite.libs.libs.nametags.runnable.NametagRunnable;
import dev.azurite.libs.libs.nametags.settings.NametagSettings;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NametagLib extends TickingLib<AzuriteLibs, Nametag, NametagSettings, NametagAdapter> {

    public NametagLib(AzuriteLibs loader) {
        super(loader, "azurite_nametag");
    }

    @Override
    public void enable(NametagSettings settings) {
        this.checkEnabled();
        this.settings = settings;
        this.adapter = settings.getAdapter();
        this.loader.registerListener(new NametagListener(this));
        this.enableAndStartTicking(settings.getTickTime(), new NametagRunnable(this));
    }
}
