package dev.azurite.libs;

import dev.azurite.libs.loader.ModuleLoader;
import dev.azurite.libs.modules.commands.CommandModule;
import dev.azurite.libs.modules.nametags.NametagModule;
import dev.azurite.libs.modules.nametags.adapter.NametagAdapter;
import dev.azurite.libs.modules.nametags.enums.NametagColor;
import dev.azurite.libs.modules.nametags.settings.NametagSettings;
import dev.azurite.libs.modules.nametags.update.NametagUpdate;
import dev.azurite.libs.modules.netty.NettyModule;
import dev.azurite.libs.modules.tablist.Tablist;
import dev.azurite.libs.modules.tablist.TablistModule;
import dev.azurite.libs.modules.tablist.adapter.TablistAdapter;
import dev.azurite.libs.modules.tablist.settings.TablistSettings;
import dev.azurite.libs.modules.tablist.skin.Skins;
import dev.azurite.libs.modules.tablist.skin.TablistSkin;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
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
    private final NametagModule nametagModule;
    private final CommandModule commandModule;

    public AzuriteLibs(JavaPlugin plugin) {
        super(plugin);
        this.nettyModule = new NettyModule(this);
        this.tablistModule = new TablistModule(this);
        this.nametagModule = new NametagModule(this);
        this.commandModule = new CommandModule(this);

        tablistModule.enable(TablistSettings.builder()
                .tickTime(500L)
                .modernHideRealPlayers(true)
                .adapter(new TablistAdapter() {
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
                    public int getColumns(Player player, int version) {
                        return player.isSneaking() ? 0 : 4;
                    }

                    @Override
                    public void updateEntries(Player player, Tablist tablist) {
                        for (int row = 0; row < 20; row++) {
                            for (int col = 0; col < tablist.getMaxColumns(); col++) {
                                tablist.setEntry(col, row, "&5Col: " + col + " Row: " + row);
                            }
                        }

                        TablistSkin skin = ThreadLocalRandom.current().nextBoolean() ? Skins.GRAY : Skins.LIGHT_GRAY;
                        tablist.setEntry(0, 0, "&c" + player.getName() + " " + "&9" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ(), skin);
                    }
                })
                .build());

        nametagModule.enable(NametagSettings.builder()
                .tickTime(500L)
                .adapter(new NametagAdapter() {
                    @Override
                    public NametagUpdate getNametagUpdate(Player viewer, Player target) {
                        String team;
                        String prefix;
                        NametagColor color;
                        int random = ThreadLocalRandom.current().nextInt(5 + 1);

                        if (random == 1) {
                            team = "Enemy";
                            prefix = "&4[Enemy] ";
                            color = NametagColor.RED;

                        } else if (random == 2) {
                            team = "Ally";
                            prefix = "&5[Ally] ";
                            color = NametagColor.LIGHT_PURPLE;

                        } else if (random == 3) {
                            team = "Team";
                            prefix = "&2[Team] ";
                            color = NametagColor.GREEN;

                        } else if (random == 4) {
                            team = "Tagged";
                            prefix = "&6[Tagged] ";
                            color = NametagColor.YELLOW;

                        } else {
                            team = "Unknown";
                            prefix = "&3[Unknown] ";
                            color = NametagColor.AQUA;
                        }

                        return new NametagUpdate(team, prefix, "", color);
                    }

                    @Override
                    public List<String> getLunarNametags(Player viewer, Player target) {
                        String prefix;
                        NametagColor color;
                        int random = ThreadLocalRandom.current().nextInt(5 + 1);

                        if (random == 1) {
                            prefix = "&b&o[L] &4[Enemy]";
                            color = NametagColor.RED;

                        } else if (random == 2) {
                            prefix = "&b&o[L] &5[Ally]";
                            color = NametagColor.LIGHT_PURPLE;

                        } else if (random == 3) {
                            prefix = "&b&o[L] &2[Team]";
                            color = NametagColor.GREEN;

                        } else if (random == 4) {
                            prefix = "&b&o[L] &6[Tagged]";
                            color = NametagColor.YELLOW;

                        } else {
                            prefix = "&b&o[L] &3[Unknown]";
                            color = NametagColor.AQUA;
                        }

                        return Arrays.asList(
                                prefix,
                                color.toColorString() + target.getName()
                        );
                    }
                })
                .build());
    }
}
