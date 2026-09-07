package org.peppox.tropicalcore.documenti;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.peppox.tropicalcore.util.Testi;

public class DocumentoListener implements Listener {

    private final DocumentoManager documentoManager;
    private static final double RAGGIO_VISIONE = 4.0;

    public DocumentoListener(DocumentoManager documentoManager) {
        this.documentoManager = documentoManager;
    }

    @EventHandler
    public void onRightClickCarta(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();

            if (documentoManager.isCartaIdentita(itemInHand)) {
                Player target = getTargetPlayer(player, RAGGIO_VISIONE);

                if (target != null) {
                    target.sendMessage(Testi.colora("&e" + player.getName() + " &7ti sta mostrando la sua Carta d'Identita."));
                    player.sendMessage(Testi.colora("&aHai mostrato la tua Carta d'Identita a &e" + target.getName()));
                } else {
                    player.sendMessage(Testi.colora("&cNon c'e nessun cittadino vicino a te a cui mostrare il documento."));
                }
            }
        }
    }

    private Player getTargetPlayer(Player player, double raggio) {
        for (Entity entity : player.getNearbyEntities(raggio, raggio, raggio)) {
            if (entity instanceof Player target && target != player) {
                if (player.hasLineOfSight(target)) {
                    return target;
                }
            }
        }
        return null;
    }
}