package dev.azurite.libs;

import dev.azurite.libs.framework.lib.LibLoader;
import dev.azurite.libs.libs.board.BoardLib;
import dev.azurite.libs.libs.commands.CommandLib;
import dev.azurite.libs.libs.nametags.NametagLib;
import dev.azurite.libs.libs.netty.NettyLib;
import dev.azurite.libs.libs.tablist.TablistLib;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
@Getter
public class AzuriteLibs extends LibLoader {

    private final NettyLib nettyLib;
    private final TablistLib tablistModule;
    private final NametagLib nametagModule;
    private final BoardLib boardModule;
    private final CommandLib commandLib;

    public AzuriteLibs(JavaPlugin plugin) {
        super(plugin);
        this.nettyLib = new NettyLib(this);
        this.tablistModule = new TablistLib(this);
        this.nametagModule = new NametagLib(this);
        this.boardModule = new BoardLib(this);
        this.commandLib = new CommandLib(this);

//        boardModule.enable(BoardSettings.builder()
//                .tickTime(200L)
//                .adapter(new BoardAdapter() {
//                    @Override
//                    public String getTitle(Player player) {
//                        return player.isSneaking() ? "Sneaking Title" : "Normal Title";
//                    }
//
//                    @Override
//                    public List<String> getLines(Player player) {
//                        if (player.isFlying()) {
//                            return null;
//                        }
//
//                        List<String> lines = new ArrayList<>();
//
//                        lines.add("line1");
//                        lines.add("line2");
//                        lines.add("line3");
//                        lines.add("line4");
//
//                        StringBuilder builder = new StringBuilder();
//
//                        for (int i = 1; i <= 16; i++) {
//                            builder.append("p");
//                        }
//
//                        for (int i = 1; i <= 16; i++) {
//                            builder.append("e");
//                        }
//
//                        for (int i = 1; i <= 16; i++) {
//                            builder.append("s");
//                        }
//
//                        lines.add(builder.toString());
//
//                        if (player.isSneaking()) {
//                            lines.add("Sneaking: " + player.isSneaking());
//                            lines.add(builder.toString());
//                        }
//
//                        return lines;
//                    }
//                })
//                .build());
//
//        tablistModule.enable(TablistSettings.builder()
//                .tickTime(500L)
//                .modernHideRealPlayers(true)
//                .adapter(new TablistAdapter() {
//                    @Override
//                    public String[] getHeader(Player player) {
//                        return new String[]{
//                                "&5Hello",
//                                "&cLine 2 Hello",
//                                player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()
//                        };
//                    }
//
//                    @Override
//                    public String[] getFooter(Player player) {
//                        return new String[]{
//                                "&5End Hello",
//                                "&cEnd Line 2 Hello",
//                                player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()
//                        };
//                    }
//
//                    @Override
//                    public int getColumns(Player player, int version) {
//                        return player.isSneaking() ? 0 : 4;
//                    }
//
//                    @Override
//                    public void updateEntries(Player player, Tablist tablist) {
//                        for (int row = 0; row < 20; row++) {
//                            for (int col = 0; col < tablist.getMaxColumns(); col++) {
//                                tablist.setEntry(col, row, "&5Col: " + col + " Row: " + row);
//                            }
//                        }
//
//                        TablistSkin skin = ThreadLocalRandom.current().nextBoolean() ? Skins.GRAY : Skins.LIGHT_GRAY;
//                        tablist.setEntry(0, 0, "&c" + player.getName() + " " + "&9" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ(), skin);
//                    }
//                })
//                .build());
//
//        nametagModule.enable(NametagSettings.builder()
//                .tickTime(500L)
//                .adapter(new NametagAdapter() {
//                    @Override
//                    public NametagUpdate getNametagUpdate(Player viewer, Player target) {
//                        String team;
//                        String prefix;
//                        NametagColor color;
//                        int random = ThreadLocalRandom.current().nextInt(5 + 1);
//
//                        if (random == 1) {
//                            team = "Enemy";
//                            prefix = "&4[Enemy] ";
//                            color = NametagColor.RED;
//
//                        } else if (random == 2) {
//                            team = "Ally";
//                            prefix = "&5[Ally] ";
//                            color = NametagColor.LIGHT_PURPLE;
//
//                        } else if (random == 3) {
//                            team = "Team";
//                            prefix = "&2[Team] ";
//                            color = NametagColor.GREEN;
//
//                        } else if (random == 4) {
//                            team = "Tagged";
//                            prefix = "&6[Tagged] ";
//                            color = NametagColor.YELLOW;
//
//                        } else {
//                            team = "Unknown";
//                            prefix = "&3[Unknown] ";
//                            color = NametagColor.AQUA;
//                        }
//
//                        return new NametagUpdate(team, prefix, "", color);
//                    }
//
//                    @Override
//                    public List<String> getLunarNametags(Player viewer, Player target) {
//                        String prefix;
//                        NametagColor color;
//                        int random = ThreadLocalRandom.current().nextInt(5 + 1);
//
//                        if (random == 1) {
//                            prefix = "&b&o[L] &4[Enemy]";
//                            color = NametagColor.RED;
//
//                        } else if (random == 2) {
//                            prefix = "&b&o[L] &5[Ally]";
//                            color = NametagColor.LIGHT_PURPLE;
//
//                        } else if (random == 3) {
//                            prefix = "&b&o[L] &2[Team]";
//                            color = NametagColor.GREEN;
//
//                        } else if (random == 4) {
//                            prefix = "&b&o[L] &6[Tagged]";
//                            color = NametagColor.YELLOW;
//
//                        } else {
//                            prefix = "&b&o[L] &3[Unknown]";
//                            color = NametagColor.AQUA;
//                        }
//
//                        return Arrays.asList(
//                                prefix,
//                                color.toColorString() + target.getName()
//                        );
//                    }
//                })
//                .build());
    }
}
