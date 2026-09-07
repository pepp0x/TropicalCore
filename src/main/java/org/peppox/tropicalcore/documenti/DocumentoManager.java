package org.peppox.tropicalcore.documenti;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.peppox.tropicalcore.TropicalCore;
import org.peppox.tropicalcore.util.EmojiParser;
import org.peppox.tropicalcore.util.Testi;

import java.util.ArrayList;
import java.util.List;

public class DocumentoManager {

    private final TropicalCore plugin;
    private final NamespacedKey keyProprietario;

    public DocumentoManager(TropicalCore plugin) {
        this.plugin = plugin;
        this.keyProprietario = new NamespacedKey(plugin, "cdi_proprietario");
    }

    public ItemStack creaCartaIdentita(Player target, String lavoro) {
        ItemStack carta = new ItemStack(Material.PAPER);
        ItemMeta meta = carta.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(Testi.colora("&e&lCARTA D'IDENTITÀ"));

            meta.getPersistentDataContainer().set(keyProprietario, PersistentDataType.STRING, target.getUniqueId().toString());

            List<String> lore = new ArrayList<>();
            lore.add(Testi.colora("&7------------------------------"));
            lore.add(Testi.colora(EmojiParser.parse(":user: &fCittadino: &e" + target.getName())));
            lore.add(Testi.colora(EmojiParser.parse(":job: &fLavoro: &a" + lavoro)));
            lore.add(Testi.colora(EmojiParser.parse(":card: &fStato: &bUfficiale")));
            lore.add(Testi.colora("&7------------------------------"));

            meta.setLore(lore);
            carta.setItemMeta(meta);
        }

        return carta;
    }

    public boolean isCartaIdentita(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(keyProprietario, PersistentDataType.STRING);
    }
}