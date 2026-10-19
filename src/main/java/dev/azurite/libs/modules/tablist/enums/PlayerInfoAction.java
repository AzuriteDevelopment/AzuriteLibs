package dev.azurite.libs.modules.tablist.enums;

import dev.azurite.libs.utils.NMSUtils;

import java.util.function.UnaryOperator;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public enum PlayerInfoAction {

    ADD_PLAYER(enums -> 0),
    UPDATE_GAME_MODE(enums -> enums >= 6 ? 2 : 1),
    UPDATE_LATENCY(enums -> enums >= 6 ? 4 : 2),
    UPDATE_DISPLAY_NAME(enums -> enums >= 6 ? 5 : 3),
    REMOVE_PLAYER(enums -> enums >= 6 ? -1 : 4),
    UPDATE_LISTED(enums -> enums >= 6 ? 3 : -1),
    UPDATE_LIST_ORDER(enums -> enums > 6 ? 6 : -1);

    private final UnaryOperator<Integer> index;

    PlayerInfoAction(UnaryOperator<Integer> index) {
        this.index = index;
    }

    public Enum<?> getEnumObject(Class<?> clazz) {
        int length = clazz.getEnumConstants().length;
        int currentIndex = index.apply(length);

        if (currentIndex == -1) {
            return null;
        }

        Enum<?> action = NMSUtils.findEnumConstant(clazz, name());

        if (action == null) {
            action = NMSUtils.findEnumConstant(clazz, currentIndex);
        }

        return action;
    }
}
