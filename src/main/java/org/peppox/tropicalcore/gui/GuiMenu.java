package org.peppox.tropicalcore.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface GuiMenu {
    void open(Player player);
    Inventory getInventory();
    void onClick(int slot, Player player);

    /**
     * Ritorna true se lo slot puo' essere modificato liberamente dal player
     * (es. mettere/togliere un item), invece di essere un pulsante di sola lettura.
     * Di default nessuno slot e' libero (comportamento invariato per ATM/Shop).
     */
    default boolean isEditableSlot(int slot, Player player) {
        return false;
    }
}