package dev.azurite.libs.modules.commands.settings;

import lombok.Builder;
import lombok.Getter;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Builder
@Getter
public class CommandSettings {

    private final String noPermissionMessage;
    private final String onlyPlayerMessage;
    private final String onlyConsoleMessage;

}
