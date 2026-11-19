package dev.azurite.libs.libs.board.settings;

import dev.azurite.libs.libs.board.adapter.BoardAdapter;
import lombok.Builder;
import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Builder
@Getter
public class BoardSettings {

    private final BoardAdapter adapter;
    private final long tickTime;

}
