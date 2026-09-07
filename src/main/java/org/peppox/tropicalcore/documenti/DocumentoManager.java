package org.peppox.tropicalcore.documenti;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.peppox.tropicalcore.TropicalCore;
import org.peppox.tropicalcore.util.EmojiParser;
import org.peppox.tropicalcore.util.Testi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DocumentoManager {

    private final TropicalCore plugin;
    private final NamespacedKey keyProprietario;

    public DocumentoManager(TropicalCore plugin) {
        this.plugin = plugin;
        this.keyProprietario = new NamespacedKey(plugin, "cdi_proprietario");
    }

    /**
     * Crea la Carta d'Identita RP del giocatore.
     */
    @NotNull
    public ItemStack creaCartaIdentita(@NotNull Player target, @NotNull String lavoro) {
        ItemStack carta = new ItemStack(Material.PAPER);
        ItemMeta meta = carta.getItemMeta();
        if (meta == null) {
            return carta;
        }

        meta.displayName(Testi.componente("&e&lCARTA D'IDENTITÀ"));
        meta.getPersistentDataContainer().set(keyProprietario, PersistentDataType.STRING, target.getUniqueId().toString());

        List<Component> lore = new ArrayList<>();
        lore.add(Testi.componente("&7------------------------------"));
        lore.add(Testi.componente(EmojiParser.parse(":utente: &fCittadino: &e" + target.getName())));
        lore.add(Testi.componente(EmojiParser.parse(":lavoro: &fLavoro: &a" + lavoro)));
        lore.add(Testi.componente(EmojiParser.parse(":documento: &fStato: &bUfficiale")));
        lore.add(Testi.componente("&7------------------------------"));
        meta.lore(lore);

        carta.setItemMeta(meta);
        return carta;
    }

    /**
     * Verifica che l'item sia una Carta d'Identita valida.
     */
    public boolean isCartaIdentita(@Nullable ItemStack item) {
        if (item == null || item.getType() != Material.PAPER || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(keyProprietario, PersistentDataType.STRING);
    }

    /**
     * Ritorna l'UUID del proprietario della carta, o {@code null} se non e' una carta valida.
     */
    @Nullable
    public UUID getProprietario(@Nullable ItemStack item) {
        if (!isCartaIdentita(item)) {
            return null;
        }
        String raw = item.getItemMeta().getPersistentDataContainer().get(keyProprietario, PersistentDataType.STRING);
        if (raw == null) {
            return null;
        }
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
