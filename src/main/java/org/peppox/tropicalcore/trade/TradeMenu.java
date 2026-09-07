package org.peppox.tropicalcore.trade;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.peppox.tropicalcore.gui.GuiMenu;
import org.peppox.tropicalcore.util.Testi;

import java.util.ArrayList;
import java.util.List;

/**
 * Finestra di scambio condivisa tra i due giocatori.
 *
 * <p>Layout (27 slot):
 * <pre>
 *   riga 0 (slot 0-8):  offerta di A
 *   riga 1 (slot 9-17): decorazioni + soldi/conferme/info
 *   riga 2 (slot 18-26): offerta di B
 * </pre>
 * I click sui pulsanti sono delegati a {@link TradeManager#handleTradeClick(TradeSession, int, Player)}.
 */
public class TradeMenu implements GuiMenu {

    private final TradeManager tradeManager;
    private final TradeSession session;
    private Inventory inventory;

    public TradeMenu(@NotNull TradeManager tradeManager, @NotNull TradeSession session) {
        this.tradeManager = tradeManager;
        this.session = session;
    }

    @Override
    public void open(Player player) {
        if (inventory == null) {
            inventory = buildInventory();
        }
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(int slot, Player player) {
        tradeManager.handleTradeClick(session, slot, player);
    }

    @Override
    public boolean isEditableSlot(int slot, Player player) {
        if (slot >= TradeSession.START_A && slot < TradeSession.START_A + TradeSession.SLOTS_PER_PLAYER) {
            return session.isPlayerA(player.getUniqueId());
        }
        if (slot >= TradeSession.START_B && slot < TradeSession.START_B + TradeSession.SLOTS_PER_PLAYER) {
            return session.isPlayerB(player.getUniqueId());
        }
        return false;
    }

    private Inventory buildInventory() {
        Player a = Bukkit.getPlayer(session.getPlayerA());
        Player b = Bukkit.getPlayer(session.getPlayerB());
        String nomeA = a != null ? a.getName() : "Giocatore A";
        String nomeB = b != null ? b.getName() : "Giocatore B";

        Inventory inv = Bukkit.createInventory(null, TradeSession.SIZE,
                Component.text(Testi.colora("&8&lScambio: &e" + nomeA + " &8<-> &e" + nomeB)));

        ItemStack vetro = creaItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int slot : new int[]{9, 10, 16, 17}) {
            inv.setItem(slot, vetro);
        }

        inv.setItem(TradeSession.SLOT_SOLDI_A, creaPulsanteSoldi("&6&lSOLDI DI " + nomeA));
        inv.setItem(TradeSession.SLOT_SOLDI_B, creaPulsanteSoldi("&6&lSOLDI DI " + nomeB));

        refresh();
        return inv;
    }

    /**
     * Aggiorna i pulsanti di conferma e il pannello info sullo stato corrente della sessione.
     */
    public void refresh() {
        if (inventory == null) {
            return;
        }
        inventory.setItem(TradeSession.SLOT_CONFERMA_A, creaConferma(session.isConfirmed(session.getPlayerA())));
        inventory.setItem(TradeSession.SLOT_CONFERMA_B, creaConferma(session.isConfirmed(session.getPlayerB())));

        List<String> lore = new ArrayList<>();
        lore.add(Testi.colora("&7Denaro offerto:"));
        lore.add(Testi.colora("&eA: &a€" + String.format("%.2f", session.getMoneyA())));
        lore.add(Testi.colora("&eB: &a€" + String.format("%.2f", session.getMoneyB())));
        lore.add("");
        lore.add(Testi.colora("&7Entrambi devono premere la &a&lCONFERMA&7."));
        inventory.setItem(TradeSession.SLOT_INFO, creaItemConLore(Material.EMERALD, "&b&lSTATO SCAMBIO", lore));
    }

    private ItemStack creaPulsanteSoldi(String nome) {
        List<String> lore = new ArrayList<>();
        lore.add(Testi.colora("&7Clicca per impostare l'importo"));
        lore.add(Testi.colora("&7di denaro da offrire."));
        return creaItemConLore(Material.GOLD_INGOT, nome, lore);
    }

    private ItemStack creaConferma(boolean confermato) {
        Material materiale = confermato ? Material.LIME_WOOL : Material.RED_WOOL;
        String nome = confermato ? "&a&lCONFERMATO" : "&c&lCONFERMA";
        List<String> lore = new ArrayList<>();
        lore.add(Testi.colora(confermato ? "&7In attesa dell'altro giocatore..." : "&7Clicca per confermare lo scambio."));
        return creaItemConLore(materiale, nome, lore);
    }

    @NotNull
    private ItemStack creaItem(Material mat, String nome) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Testi.componente(nome));
            item.setItemMeta(meta);
        }
        return item;
    }

    @NotNull
    private ItemStack creaItemConLore(Material mat, String nome, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Testi.componente(nome));
            List<Component> componentLore = new ArrayList<>();
            for (String riga : lore) {
                componentLore.add(Testi.componente(riga));
            }
            meta.lore(componentLore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
