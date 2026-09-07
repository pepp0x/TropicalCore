package org.peppox.tropicalcore.actionbar;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;
import org.peppox.tropicalcore.jobs.JobManager;

public class ActionBarTask extends BukkitRunnable {

    private static final Key ICONS_FONT = Key.key("tropicalcore", "icons");
    private static final String ICONA_SOLDI = "\uD83E\uDE99";

    private final Economy economy;
    private final JobManager jobManager;

    public ActionBarTask(@Nullable Economy economy, @Nullable JobManager jobManager) {
        this.economy = economy;
        this.jobManager = jobManager;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            double balance = (economy != null) ? economy.getBalance(player) : 0.0;
            String job = (jobManager != null) ? jobManager.getJob(player) : "Disoccupato";

            Component icona = Component.text(ICONA_SOLDI)
                    .style(Style.style().font(ICONS_FONT).build());

            Component testo = Component.text()
                    .append(icona)
                    .append(Component.text(" " + String.format("%.2f$", balance), NamedTextColor.GREEN))
                    .append(Component.text("  |  ", NamedTextColor.GRAY))
                    .append(Component.text(job, NamedTextColor.AQUA))
                    .build();

            player.sendActionBar(testo);
        }
    }
}
