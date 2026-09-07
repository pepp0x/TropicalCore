package org.peppox.tropicalcore.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.peppox.tropicalcore.util.Testi;
import org.peppox.tropicalcore.util.VaultHook;

import java.util.ArrayList;
import java.util.List;

public class AtmMenu {

    public static final String TITOLO_ATM = Testi.colora("&1&lBANCOMAT / ATM");

    public static void apriATM(Player player) {
        // Creazione dell'inventario tramite Component per evitare deprecazioni 1.21
        Inventory gui = Bukkit.createInventory(null, 27, Component.text(TITOLO_ATM));

        // 1. Vetri decorativi per lo sfondo
        ItemStack vetroChiaro = creaItem(Material.BLUE_STAINED_GLASS_PANE, " ");
        ItemStack vetroScuro = creaItem(Material.BLACK_STAINED_GLASS_PANE, " ");

        for (int i = 0; i < 27; i++) {
            if (i % 2 == 0) {
                gui.setItem(i, vetroChiaro);
            } else {
                gui.setItem(i, vetroScuro);
            }
        }

        // 2. Recupero del saldo tramite VaultHook (chiamata statica)
        double saldo = VaultHook.getEconomy() != null ? VaultHook.getEconomy().getBalance(player) : 0.0;

        // 3. Pulsante Info Saldo (Centro - Slot 13)
        List<String> loreInfo = new ArrayList<>();
        loreInfo.add(Testi.colora("&7Cointestatario: &e" + player.getName()));
        loreInfo.add(Testi.colora("&7Saldo sul conto: &a€" + String.format("%.2f", saldo)));
        loreInfo.add("");
        loreInfo.add(Testi.colora("&eBanca Centrale Roleplay"));
        ItemStack itemSaldo = creaItemConLore(Material.EMERALD_BLOCK, "&a&lINFORMAZIONI CONTO", loreInfo);
        gui.setItem(13, itemSaldo);

        // 4. Pulsante Preleva Contanti (Sinistra - Slot 11)
        List<String> lorePreleva = new ArrayList<>();
        lorePreleva.add(Testi.colora("&7Clicca per prelevare denaro"));
        lorePreleva.add(Testi.colora("&7dal tuo conto bancario."));
        lorePreleva.add("");
        lorePreleva.add(Testi.colora("&e▶ Fai click per inserire l'importo"));
        ItemStack itemPreleva = creaItemConLore(Material.GOLD_INGOT, "&c&lPRELEVA CONTANTI", lorePreleva);
        gui.setItem(11, itemPreleva);

        // 5. Pulsante Deposita Contanti (Destra - Slot 15)
        List<String> loreDeposita = new ArrayList<>();
        loreDeposita.add(Testi.colora("&7Clicca per depositare contanti"));
        loreDeposita.add(Testi.colora("&7nel tuo conto bancario."));
        loreDeposita.add("");
        loreDeposita.add(Testi.colora("&e▶ Fai click per inserire l'importo"));
        ItemStack itemDeposita = creaItemConLore(Material.CHEST, "&a&lDEPOSITA CONTANTI", loreDeposita);
        gui.setItem(15, itemDeposita);

        // 6. Pulsante Chiudi (Basso Centro - Slot 22)
        List<String> loreChiudi = new ArrayList<>();
        loreChiudi.add(Testi.colora("&7Clicca per uscire dallo sportello."));
        ItemStack itemChiudi = creaItemConLore(Material.BARRIER, "&c&lESCI", loreChiudi);
        gui.setItem(22, itemChiudi);

        player.openInventory(gui);
    }

    private static ItemStack creaItem(Material mat, String nome) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(Testi.colora(nome)));
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack creaItemConLore(Material mat, String nome, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(Testi.colora(nome)));
            List<Component> componentLore = new ArrayList<>();
            for (String s : lore) {
                componentLore.add(Component.text(s));
            }
            meta.lore(componentLore);
            item.setItemMeta(meta);
        }
        return item;
    }
}