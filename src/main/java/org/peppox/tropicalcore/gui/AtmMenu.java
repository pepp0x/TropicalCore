package org.peppox.tropicalcore.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.peppox.tropicalcore.util.Testi;

import java.util.ArrayList;
import java.util.List;

public class AtmMenu {

    public static final int SLOT_SALDO = 13;
    public static final int SLOT_PRELEVA = 11;
    public static final int SLOT_DEPOSITA = 15;
    public static final int SLOT_ESCI = 22;

    /** Titolo dell'ATM, usato anche come identificativo univoco dal MenuListener. */
    public static final Component TITOLO_ATM = Component.text("BANCOMAT / ATM", NamedTextColor.DARK_BLUE, TextDecoration.BOLD);

    /**
     * Apre l'ATM per il giocatore.
     *
     * @param economy provider di economia (puo' essere {@code null}: il saldo risultera' 0).
     */
    public static void apriATM(@NotNull Player player, @Nullable Economy economy) {
        Inventory gui = Bukkit.createInventory(null, 27, TITOLO_ATM);

        ItemStack vetroChiaro = creaItem(Material.BLUE_STAINED_GLASS_PANE, " ");
        ItemStack vetroScuro = creaItem(Material.BLACK_STAINED_GLASS_PANE, " ");

        for (int i = 0; i < 27; i++) {
            gui.setItem(i, i % 2 == 0 ? vetroChiaro : vetroScuro);
        }

        double saldo = (economy != null) ? economy.getBalance(player) : 0.0;

        List<String> loreInfo = new ArrayList<>();
        loreInfo.add(Testi.colora("&7Cointestatario: &e" + player.getName()));
        loreInfo.add(Testi.colora("&7Saldo sul conto: &a€" + String.format("%.2f", saldo)));
        loreInfo.add("");
        loreInfo.add(Testi.colora("&eBanca Centrale Roleplay"));
        gui.setItem(SLOT_SALDO, creaItemConLore(Material.EMERALD_BLOCK, "&a&lINFORMAZIONI CONTO", loreInfo));

        List<String> lorePreleva = new ArrayList<>();
        lorePreleva.add(Testi.colora("&7Clicca per prelevare denaro"));
        lorePreleva.add(Testi.colora("&7dal tuo conto bancario."));
        lorePreleva.add("");
        lorePreleva.add(Testi.colora("&e▶ Fai click per inserire l'importo"));
        gui.setItem(SLOT_PRELEVA, creaItemConLore(Material.GOLD_INGOT, "&c&lPRELEVA CONTANTI", lorePreleva));

        List<String> loreDeposita = new ArrayList<>();
        loreDeposita.add(Testi.colora("&7Clicca per depositare contanti"));
        loreDeposita.add(Testi.colora("&7nel tuo conto bancario."));
        loreDeposita.add("");
        loreDeposita.add(Testi.colora("&e▶ Fai click per inserire l'importo"));
        gui.setItem(SLOT_DEPOSITA, creaItemConLore(Material.CHEST, "&a&lDEPOSITA CONTANTI", loreDeposita));

        List<String> loreChiudi = new ArrayList<>();
        loreChiudi.add(Testi.colora("&7Clicca per uscire dallo sportello."));
        gui.setItem(SLOT_ESCI, creaItemConLore(Material.BARRIER, "&c&lESCI", loreChiudi));

        player.openInventory(gui);
    }

    private static ItemStack creaItem(Material mat, String nome) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Testi.componente(nome));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack creaItemConLore(Material mat, String nome, List<String> lore) {
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
