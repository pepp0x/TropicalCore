package org.peppox.tropicalcore.jobs.polizia;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.peppox.tropicalcore.util.Testi;

public class ManetteCommand implements CommandExecutor {

    private final PoliziaManager poliziaManager;

    public ManetteCommand(PoliziaManager poliziaManager) {
        this.poliziaManager = poliziaManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player poliziotto)) {
            sender.sendMessage(Testi.colora("&cQuesto comando puo' essere eseguito solo in gioco."));
            return true;
        }

        if (!poliziotto.hasPermission("tropicalcore.polizia.manette")) {
            poliziotto.sendMessage(Testi.colora("&cNon hai i permessi da Forze dell'Ordine per usare questo comando."));
            return true;
        }

        if (!poliziaManager.isPoliziotto(poliziotto)) {
            poliziotto.sendMessage(Testi.colora("&cSolo un Agente di Polizia puo' usare le manette."));
            return true;
        }

        // Senza argomenti: consegna le manette all'agente
        if (args.length == 0) {
            if (poliziotto.getInventory().contains(poliziaManager.getItemManette())) {
                poliziotto.sendMessage(Testi.colora("&eHai gia' le manette in inventario."));
            } else {
                poliziotto.getInventory().addItem(poliziaManager.getItemManette());
                poliziotto.sendMessage(Testi.colora("&aHai ricevuto le &7&lMANETTE&a."));
            }
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            poliziotto.sendMessage(Testi.colora("&cGiocatore non trovato o offline."));
            return true;
        }

        if (target.equals(poliziotto)) {
            poliziotto.sendMessage(Testi.colora("&cNon puoi ammanettare te stesso."));
            return true;
        }

        if (poliziotto.getLocation().distance(target.getLocation()) > PoliziaManager.RAGGIO_MANETTE) {
            poliziotto.sendMessage(Testi.colora("&cIl cittadino e' troppo lontano per essere ammanettato."));
            return true;
        }

        poliziaManager.gestisciAmmanettamento(poliziotto, target);
        return true;
    }
}
