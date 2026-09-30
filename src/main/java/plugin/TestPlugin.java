package plugin;

import dev.azurite.libs.AzuriteLibs;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.listener.TestListener;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        AzuriteLibs azuriteLibs = new AzuriteLibs(this);
        //Bukkit.getPluginManager().registerEvents(new TestListener(this), this);
    }

    @Override
    public void onDisable() {
    }
}
