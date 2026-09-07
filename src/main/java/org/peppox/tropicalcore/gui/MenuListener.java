package org.peppox.tropicalcore.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.peppox.tropicalcore.util.Testi;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        // Controllo titolo della GUI
        if (event.getView().getTitle().equals(AtmMenu.TITOLO_ATM)) {
            event.setCancelled(true); // Previene la sottrazione di item

            ItemStack itemCorrente = event.getCurrentItem();
            if (itemCorrente == null || itemCorrente.getType() == Material.AIR) {
                return;
            }

            int slot = event.getSlot();

            if (slot == 11) { // Preleva
                player.closeInventory();
                player.sendMessage(Testi.colora("&eScrivi in chat l'importo da &cPRELEVARE&e:"));
            } else if (slot == 15) { // Deposita
                player.closeInventory();
                player.sendMessage(Testi.colora("&eScrivi in chat l'importo da &aDEPOSITARE&e:"));
            } else if (slot == 22) { // Chiudi
                player.closeInventory();
            }
        }
    }
}