package org.peppox.tropicalcore.jobs;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JobManager {

    private final Map<UUID, String> ruoliGiocatori = new HashMap<>();

    public String getJob(@NotNull Player player) {
        return ruoliGiocatori.getOrDefault(player.getUniqueId(), "Disoccupato");
    }

    public void setJob(@NotNull Player player, @NotNull String lavoro) {
        ruoliGiocatori.put(player.getUniqueId(), lavoro);
    }

    public String getLavoro(@NotNull Player player) {
        return getJob(player);
    }

    public void setLavoro(@NotNull Player player, @NotNull String lavoro) {
        setJob(player, lavoro);
    }

    public boolean haLavoro(@NotNull Player player, @NotNull String lavoro) {
        return getJob(player).equalsIgnoreCase(lavoro);
    }
}