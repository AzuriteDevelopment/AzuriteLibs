package plugin.listener;

import dev.azurite.libs.modules.tablist.reflection.TablistReflection;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Copyright (c) 2026. Keano
 * Use or redistribution of source or file is
 * only permitted if given explicit permission.
 */
public class TestListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        TablistReflection tablistReflection = new TablistReflection(player);
        tablistReflection.sendPacket(new PacketPlayOutChat(new ChatComponentText("Test")));
        tablistReflection.test();
    }
}
