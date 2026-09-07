package org.peppox.tropicalcore.trade;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.ConcurrentHashMap;

public class TradeMenu {

    private static final Map<UUID, TradeMenu> sessioniAttive = new ConcurrentHashMap<>();

    private final Player player;

    public TradeMenu(Player player) {
        this.player = player;
    }

    public static void register(Player player, TradeMenu menu) {
        if (menu == null) {
            sessioniAttive.remove(player.getUniqueId());
        } else {
            sessioniAttive.put(player.getUniqueId(), menu);
        }
    }

    public static TradeMenu getSessione(Player player) {
        return sessioniAttive.get(player.getUniqueId());
    }

    public Player getPlayer() {
        return player;
    }
}