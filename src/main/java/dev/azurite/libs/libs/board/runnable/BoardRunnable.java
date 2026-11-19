package dev.azurite.libs.libs.board.runnable;

import dev.azurite.libs.libs.board.Board;
import dev.azurite.libs.libs.board.BoardLib;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class BoardRunnable implements Runnable {

    private final BoardLib module;

    public BoardRunnable(BoardLib module) {
        this.module = module;
    }

    @Override
    public void run() {
        for (Board board : module.getCachedData().values()) {
            board.update();
        }
    }
}
