package org.peppox.tropicalcore.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Contratto base per una GUI a inventario.
 */
public interface GuiMenu {

    /**
     * Apre la GUI al giocatore.
     */
    void open(Player player);

    /**
     * Ritorna l'inventario che rappresenta la GUI (puo' essere {@code null} se mai aperta).
     */
    Inventory getInventory();

    /**
     * Gestisce un click del giocatore su uno slot della GUI.
     */
    void onClick(int slot, Player player);

    /**
     * Ritorna {@code true} se lo slot puo' essere modificato liberamente dal giocatore
     * (es. mettere/togliere un item), invece di essere un pulsante di sola lettura.
     * Di default nessuno slot e' libero.
     */
    default boolean isEditableSlot(int slot, Player player) {
        return false;
    }
}
