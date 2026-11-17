package dev.azurite.libs.modules.board.enums;

import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public enum ScoreAction {

    CHANGE(0),
    REMOVE(1);

    private final int id;

    ScoreAction(int id) {
        this.id = id;
    }
}
