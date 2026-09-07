package org.peppox.tropicalcore.jobs.polizia;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.peppox.tropicalcore.jobs.JobManager;
import org.peppox.tropicalcore.util.Testi;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PoliziaManager {

    /** Raggio (in blocchi) entro cui il poliziotto puo' ammanettare un sospetto. */
    public static final double RAGGIO_MANETTE = 4.0;
    /** Raggio (in blocchi) entro cui il poliziotto puo' prendere in scorta un cittadino. */
    public static final double RAGGIO_SCORTA = 5.0;

    /** Testo semplice dell'item manette, usato per riconoscerlo. */
    private static final String NOME_MANETTE = "MANETTE";

    private static final ItemStack ITEM_MANETTE = creaManetteItem();

    private final JobManager jobManager;
    private final Set<UUID> ammanettati = new HashSet<>();
    private final Map<UUID, UUID> giocatoriInScorta = new ConcurrentHashMap<>();

    public PoliziaManager(@NotNull JobManager jobManager) {
        this.jobManager = jobManager;
    }

    /**
     * Ritorna l'oggetto "manette" che il poliziotto usa cliccando un cittadino.
     */
    public ItemStack getItemManette() {
        return ITEM_MANETTE.clone();
    }

    /**
     * Verifica che l'item sia una coppia di manette (tramite display name e materiale).
     */
    public boolean isOggettoManette(ItemStack item) {
        if (item == null || item.getType() != Material.IRON_INGOT || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            return false;
        }
        return NOME_MANETTE.equals(PlainTextComponentSerializer.plainText().serialize(meta.displayName()));
    }

    public boolean isPoliziotto(@NotNull Player player) {
        return jobManager.getJob(player).equalsIgnoreCase("Polizia")
                || jobManager.getJob(player).equalsIgnoreCase("Agente di Polizia");
    }

    public void setAmmanettato(@NotNull Player target, boolean ammanettato) {
        if (ammanettato) {
            ammanettati.add(target.getUniqueId());
        } else {
            ammanettati.remove(target.getUniqueId());
        }
    }

    public boolean isAmmanettato(@NotNull Player target) {
        return ammanettati.contains(target.getUniqueId());
    }

    /**
     * Ammanetta o libera il sospetto, mostrando i messaggi ai giocatori coinvolti.
     */
    public void gestisciAmmanettamento(@NotNull Player poliziotto, @NotNull Player sospetto) {
        if (isAmmanettato(sospetto)) {
            setAmmanettato(sospetto, false);
            sospetto.sendMessage(Testi.colora("&aTi sono state rimosse le manette."));
            poliziotto.sendMessage(Testi.colora("&aHai rimosso le manette a &e" + sospetto.getName()));
        } else {
            setAmmanettato(sospetto, true);
            sospetto.sendMessage(Testi.colora("&cSei stato ammanettato dall'agente &e" + poliziotto.getName()));
            poliziotto.sendMessage(Testi.colora("&aHai ammanettato &e" + sospetto.getName()));
        }
    }

    /**
     * Inserisce/rimuove il target dalla scorta del poliziotto.
     * Ritorna {@code true} se il target e' ora in scorta.
     */
    public boolean toggleScorta(@NotNull Player poliziotto, @NotNull Player target) {
        if (giocatoriInScorta.containsKey(target.getUniqueId())) {
            giocatoriInScorta.remove(target.getUniqueId());
            return false;
        }
        giocatoriInScorta.put(target.getUniqueId(), poliziotto.getUniqueId());
        return true;
    }

    public boolean isInScorta(@NotNull Player target) {
        return giocatoriInScorta.containsKey(target.getUniqueId());
    }

    /**
     * Ritorna l'UUID del poliziotto che sta scortando il target, o {@code null}.
     */
    public UUID getPoliziottoScorta(@NotNull Player target) {
        return giocatoriInScorta.get(target.getUniqueId());
    }

    /**
     * Ripulisce i dati di un giocatore (ammanettato / scorta) quando esce.
     */
    public void cleanup(@NotNull Player player) {
        ammanettati.remove(player.getUniqueId());
        giocatoriInScorta.remove(player.getUniqueId());
        giocatoriInScorta.values().removeIf(uuid -> uuid.equals(player.getUniqueId()));
    }

    private static ItemStack creaManetteItem() {
        ItemStack manette = new ItemStack(Material.IRON_INGOT);
        ItemMeta meta = manette.getItemMeta();
        if (meta != null) {
            meta.displayName(Testi.componente("&7&lMANETTE"));
            meta.lore(List.of(
                    Testi.componente("&8» &7Clicca destro su un cittadino"),
                    Testi.componente("&8» &7per ammanettarlo o liberarlo.")
            ));
            manette.setItemMeta(meta);
        }
        return manette;
    }
}
