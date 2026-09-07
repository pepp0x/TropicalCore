package org.peppox.tropicalcore.jobs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.peppox.tropicalcore.util.LuckPermsHook;
import org.peppox.tropicalcore.util.Testi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobCommand implements CommandExecutor, TabCompleter {

    private final JobManager jobManager;
    private final LuckPermsHook luckPermsHook;

    public JobCommand(JobManager jobManager, LuckPermsHook luckPermsHook) {
        this.jobManager = jobManager;
        this.luckPermsHook = luckPermsHook;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Testi.colora("&cQuesto comando puo' essere eseguito solo da un giocatore."));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Testi.colora("&e&lLAVORO &8» &7Il tuo lavoro attuale: &a" + jobManager.getJob(player)));
            player.sendMessage(Testi.colora("&7Lavori disponibili: &f" + elencoLavori()));
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 2) {
                player.sendMessage(Testi.colora("&cUso corretto: /lavoro set <lavoro>"));
                return true;
            }

            if (!player.hasPermission("tropicalcore.admin.lavoro")) {
                player.sendMessage(Testi.colora("&cNon hai il permesso per assegnare i lavori."));
                return true;
            }

            String risolto = jobManager.risolviLavoro(args[1]);
            if (risolto == null) {
                player.sendMessage(Testi.colora("&cLavoro non valido. Disponibili: &f" + elencoLavori()));
                return true;
            }

            // Rimuovi il vecchio gruppo (se presente) e assegna il nuovo
            String vecchio = jobManager.getJob(player);
            if (luckPermsHook != null && luckPermsHook.isReady() && !vecchio.equalsIgnoreCase(risolto)) {
                luckPermsHook.removeFromGroup(player.getUniqueId(), vecchio)
                        .thenRun(() -> luckPermsHook.addToGroup(player.getUniqueId(), risolto));
            }

            jobManager.setJob(player, risolto);
            player.sendMessage(Testi.colora("&aOra lavori come: &e" + risolto));
            return true;
        }

        player.sendMessage(Testi.colora("&cUso corretto: /lavoro oppure /lavoro set <lavoro>"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("set");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set") && sender.hasPermission("tropicalcore.admin.lavoro")) {
            for (JobType tipo : JobType.values()) {
                if (tipo.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(tipo.name().toLowerCase());
                }
            }
        }
        return completions;
    }

    private String elencoLavori() {
        return String.join("&7, &f", Arrays.stream(JobType.values()).map(JobType::getNomeFormattato).toList());
    }
}
