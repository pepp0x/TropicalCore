package org.peppox.tropicalcore.jobs.polizia;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.peppox.tropicalcore.jobs.JobManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.ConcurrentHashMap;

public class PoliziaManager {

    private final JobManager jobManager;
    private final Set<UUID> ammanettati = new HashSet<>();
    private final Map<UUID, UUID> giocatoriInScorta = new ConcurrentHashMap<>();

    public PoliziaManager(@NotNull JobManager jobManager) {
        this.jobManager = jobManager;
    }

    public boolean isPoliziotto(@NotNull Player player) {
        return jobManager.getJob(player).equalsIgnoreCase("Polizia");
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

    public void toggleScorta(@NotNull Player poliziotto, @NotNull Player target) {
        if (giocatoriInScorta.containsKey(target.getUniqueId())) {
            giocatoriInScorta.remove(target.getUniqueId());
        } else {
            giocatoriInScorta.put(target.getUniqueId(), poliziotto.getUniqueId());
        }
    }

    public boolean isInScorta(@NotNull Player target) {
        return giocatoriInScorta.containsKey(target.getUniqueId());
    }
}