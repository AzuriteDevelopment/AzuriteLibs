package dev.azurite.libs.modules.nametags;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.ticking.TickingModule;
import dev.azurite.libs.modules.nametags.adapter.NametagAdapter;
import dev.azurite.libs.modules.nametags.listener.NametagListener;
import dev.azurite.libs.modules.nametags.runnable.NametagRunnable;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NametagModule extends TickingModule<AzuriteLibs, Nametag, NametagAdapter> {

    public NametagModule(AzuriteLibs loader) {
        super(loader, "azurite_nametag");
        new NametagListener(this).register();
    }

    @Override
    public Runnable getRunnable() {
        return new NametagRunnable(this);
    }
}
