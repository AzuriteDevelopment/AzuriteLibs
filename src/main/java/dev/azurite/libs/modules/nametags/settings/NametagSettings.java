package dev.azurite.libs.modules.nametags.settings;

import dev.azurite.libs.modules.nametags.adapter.NametagAdapter;
import lombok.Builder;
import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Builder
@Getter
public class NametagSettings {

    private final NametagAdapter adapter;
    private final long tickTime;

}
