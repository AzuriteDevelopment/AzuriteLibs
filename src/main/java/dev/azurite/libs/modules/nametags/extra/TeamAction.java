package dev.azurite.libs.modules.nametags.extra;

import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public enum TeamAction {

    CREATE_TEAM(0),
    REMOVE_TEAM(1),
    UPDATE_TEAM(2),
    ADD_PLAYERS(3),
    REMOVE_PLAYERS(4);

    private final int id;

    TeamAction(int id) {
        this.id = id;
    }
}
