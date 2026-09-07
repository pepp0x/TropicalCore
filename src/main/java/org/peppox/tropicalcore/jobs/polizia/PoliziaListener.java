package org.peppox.tropicalcore.jobs.polizia;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.peppox.tropicalcore.util.Testi;

import java.util.UUID;

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
            if (poliziotto.getLocation().distance(sospetto.getLocation()) > PoliziaManager.RAGGIO_MANETTE) {
                poliziotto.sendMessage(Testi.colora("&cIl cittadino e' troppo lontano per essere ammanettato."));
                return;
            }
            event.setCancelled(true);
            poliziaManager.gestisciAmmanettamento(poliziotto, sospetto);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMoveAmmanettato(PlayerMoveEvent event) {
        Player target = event.getPlayer();
        if (!poliziaManager.isAmmanettato(target)) {
            return;
        }

        boolean stessoBlocco = event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ();
        if (stessoBlocco) {
            return;
        }

        // In scorta: mantieni il cittadino vicino al poliziotto che lo scorta
        UUID poliziottoId = poliziaManager.getPoliziottoScorta(target);
        if (poliziottoId != null) {
            Player poliziotto = target.getServer().getPlayer(poliziottoId);
            if (poliziotto != null && poliziotto.isOnline()) {
                Location posizione = poliziotto.getLocation()
                        .add(poliziotto.getLocation().getDirection().setY(0).multiply(-1).normalize().multiply(1.2));
                posizione.setYaw(target.getLocation().getYaw());
                posizione.setPitch(target.getLocation().getPitch());
                target.teleportAsync(posizione);
                return;
            }
            // Il poliziotto non e' piu' online: termina scorta e manette
            poliziaManager.setAmmanettato(target, false);
            poliziaManager.cleanup(target);
            return;
        }

        // Ammanettato ma non in scorta: non puo' muoversi
        Location fermo = event.getFrom();
        fermo.setYaw(event.getTo().getYaw());
        fermo.setPitch(event.getTo().getPitch());
        event.setTo(fermo);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        poliziaManager.cleanup(event.getPlayer());
    }
}
