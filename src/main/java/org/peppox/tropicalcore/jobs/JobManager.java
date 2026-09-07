package org.peppox.tropicalcore.jobs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobManager {

    private final Map<UUID, String> ruoliGiocatori = new HashMap<>();

    /**
     * Ritorna il lavoro del giocatore, o "Disoccupato" se non ne ha uno.
     */
    @NotNull
    public String getJob(@NotNull Player player) {
        return ruoliGiocatori.getOrDefault(player.getUniqueId(), JobType.DISOCCUPATO.getNomeFormattato());
    }

    public void setJob(@NotNull Player player, @NotNull String lavoro) {
        ruoliGiocatori.put(player.getUniqueId(), lavoro);
    }

    /**
     * Alias di {@link #getJob(Player)} mantenuto per compatibilita'.
     */
    @NotNull
    public String getLavoro(@NotNull Player player) {
        return getJob(player);
    }

    /**
     * Alias di {@link #setJob(Player, String)} mantenuto per compatibilita'.
     */
    public void setLavoro(@NotNull Player player, @NotNull String lavoro) {
        setJob(player, lavoro);
    }

    public boolean haLavoro(@NotNull Player player, @NotNull String lavoro) {
        return getJob(player).equalsIgnoreCase(lavoro);
    }

    /**
     * Risolve un lavoro a partire dal nome digitato, confrontando anche i nomi formattati
     * dell'enum {@link JobType}. Ritorna {@code null} se il lavoro non esiste.
     */
    public String risolviLavoro(@NotNull String nomeDigitato) {
        String richiesto = nomeDigitato.trim();
        for (JobType tipo : JobType.values()) {
            if (tipo.name().equalsIgnoreCase(richiesto) || tipo.getNomeFormattato().equalsIgnoreCase(richiesto)) {
                return tipo.getNomeFormattato();
            }
        }
        return null;
    }

    /**
     * Pulisce i dati dei giocatori non piu' online.
     */
    public void cleanup() {
        for (UUID uuid : ruoliGiocatori.keySet()) {
            if (Bukkit.getPlayer(uuid) == null) {
                ruoliGiocatori.remove(uuid);
            }
        }
    }
}
