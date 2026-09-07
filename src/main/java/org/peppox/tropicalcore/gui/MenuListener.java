package org.peppox.tropicalcore.gui;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.peppox.tropicalcore.util.Testi;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestisce i click della GUI dell'ATM e l'inserimento degli importi da chat.
 */
public class MenuListener implements Listener {

    private enum TipoImporto {
        PRELEVA,
        DEPOSITA
    }

    private final Economy economy;
    private final Map<UUID, TipoImporto> importiInAttesa = new ConcurrentHashMap<>();

    public MenuListener(@Nullable Economy economy) {
        this.economy = economy;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!event.getView().title().equals(AtmMenu.TITOLO_ATM)) {
            return;
        }

        event.setCancelled(true);

        ItemStack itemCorrente = event.getCurrentItem();
        if (itemCorrente == null || itemCorrente.getType() == Material.AIR) {
            return;
        }

        int slot = event.getSlot();
        if (slot == AtmMenu.SLOT_PRELEVA) {
            importiInAttesa.put(player.getUniqueId(), TipoImporto.PRELEVA);
            player.sendMessage(Testi.colora("&eScrivi in chat l'importo da &cPRELEVARE&e:"));
        } else if (slot == AtmMenu.SLOT_DEPOSITA) {
            importiInAttesa.put(player.getUniqueId(), TipoImporto.DEPOSITA);
            player.sendMessage(Testi.colora("&eScrivi in chat l'importo da &aDEPOSITARE&e:"));
        } else if (slot == AtmMenu.SLOT_ESCI) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            importiInAttesa.remove(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        TipoImporto tipo = importiInAttesa.remove(player.getUniqueId());
        if (tipo == null) {
            return;
        }

        event.setCancelled(true);

        String testo = PlainTextComponentSerializer.plainText().serialize(event.message()).trim().replace(',', '.');
        double importo;
        try {
            importo = Double.parseDouble(testo);
        } catch (NumberFormatException e) {
            player.sendMessage(Testi.colora("&cImporto non valido. Inserisci un numero (es. 100 oppure 50.5)."));
            return;
        }

        if (!Double.isFinite(importo) || importo <= 0) {
            player.sendMessage(Testi.colora("&cL'importo deve essere un numero positivo."));
            return;
        }

        if (economy == null) {
            player.sendMessage(Testi.colora("&cEconomy non disponibile: operazione annullata."));
            return;
        }

        if (tipo == TipoImporto.PRELEVA) {
            if (economy.has(player, importo)) {
                economy.withdrawPlayer(player, importo);
                player.sendMessage(Testi.colora("&aHai prelevato &e€" + String.format("%.2f", importo) + " &adal tuo conto."));
            } else {
                player.sendMessage(Testi.colora("&cSaldo insufficiente per prelevare questo importo."));
            }
        } else {
            if (economy.has(player, importo)) {
                economy.depositPlayer(player, importo);
                player.sendMessage(Testi.colora("&aHai depositato &e€" + String.format("%.2f", importo) + " &anel tuo conto."));
            } else {
                player.sendMessage(Testi.colora("&cNon possiedi questo importo da depositare."));
            }
        }

        // Riapri l'ATM per aggiornare il saldo visualizzato
        AtmMenu.apriATM(player, economy);
    }
}
