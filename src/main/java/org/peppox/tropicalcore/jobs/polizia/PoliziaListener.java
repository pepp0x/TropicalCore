package org.peppox.tropicalcore.jobs.polizia;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PoliziaListener implements Listener {

    private final PoliziaManager poliziaManager;

    public PoliziaListener(PoliziaManager poliziaManager) {
        this.poliziaManager = poliziaManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player poliziotto = event.getPlayer();

        if (!(event.getRightClicked() instanceof Player sospetto)) {
            return;
        }

        if (!poliziaManager.isPoliziotto(poliziotto)) {
            return;
        }

        if (poliziaManager.isOggettoManette(poliziotto.getInventory().getItemInMainHand())) {
            event.setCancelled(true);
            poliziaManager.gestisciAmmanettamento(poliziotto, sospetto);
        }
    }
}