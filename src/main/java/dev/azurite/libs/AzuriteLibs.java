package dev.azurite.libs;

import dev.azurite.libs.loader.ModuleLoader;
import dev.azurite.libs.modules.commands.CommandModule;
import dev.azurite.libs.modules.netty.NettyModule;
import dev.azurite.libs.modules.tablist.Tablist;
import dev.azurite.libs.modules.tablist.TablistModule;
import dev.azurite.libs.modules.tablist.adapter.TablistAdapter;
import dev.azurite.libs.modules.tablist.skin.TablistSkin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

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

        tablistModule.setTickingTime(500L);
        tablistModule.setAdapter(new TablistAdapter() {
            @Override
            public String[] getHeader(Player player) {
                return new String[]{
                        "&5Hello",
                        "&cLine 2 Hello",
                        player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()
                };
            }

            @Override
            public String[] getFooter(Player player) {
                return new String[]{
                        "&5End Hello",
                        "&cEnd Line 2 Hello",
                        player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()
                };
            }

            @Override
            public void updateEntries(Player player, Tablist tablist) {
                for (int row = 0; row < 20; row++) {
                    for (int col = 0; col < tablist.getMaxColumns(); col++) {
                        tablist.setEntry(col, row, "&5Col: " + col + " Row: " + row);
                    }
                }

                int random = Bukkit.getOnlinePlayers().size();
                Player randomPlayer = new ArrayList<>(Bukkit.getOnlinePlayers()).get(ThreadLocalRandom.current().nextInt(random));

                tablist.setEntry(0, 0, "&c" + player.getName() + " " + "&9" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ(), TablistSkin.getFromPlayer(randomPlayer));
            }
        });
    }
}
