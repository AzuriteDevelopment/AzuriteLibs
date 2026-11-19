package dev.azurite.libs.libs.board.enums;

import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public enum ObjectiveAction {

    CREATE(0),
    REMOVE(1),
    UPDATE(2);

    private final int id;

    ObjectiveAction(int id) {
        this.id = id;
    }
}
