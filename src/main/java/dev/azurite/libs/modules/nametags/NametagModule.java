package dev.azurite.libs.modules.nametags;

import dev.azurite.libs.AzuriteLibs;
import dev.azurite.libs.loader.Module;
import dev.azurite.libs.modules.nametags.listener.NametagListener;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class NametagModule extends Module<AzuriteLibs> {

    public NametagModule(AzuriteLibs loader) {
        super(loader);
        new NametagListener(this).register();
    }
}
