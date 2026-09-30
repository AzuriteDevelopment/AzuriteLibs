package dev.azurite.libs;

import dev.azurite.libs.loader.ModuleLoader;
import dev.azurite.libs.modules.commands.CommandModule;
import dev.azurite.libs.modules.netty.NettyModule;
import dev.azurite.libs.modules.tablist.TablistModule;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class AzuriteLibs extends ModuleLoader {

    private final NettyModule nettyModule;
    private final TablistModule tablistModule;
    private final CommandModule commandModule;

    public AzuriteLibs(JavaPlugin plugin) {
        super(plugin);
        this.nettyModule = new NettyModule(this);
        this.tablistModule = new TablistModule(this);
        this.commandModule = new CommandModule(this);
    }
}
