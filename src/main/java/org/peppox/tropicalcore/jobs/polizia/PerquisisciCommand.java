package org.peppox.tropicalcore.jobs.polizia;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.peppox.tropicalcore.util.Testi;

public class PerquisisciCommand implements CommandExecutor {

    private final PoliziaManager poliziaManager;

    public PerquisisciCommand(PoliziaManager poliziaManager) {
        this.poliziaManager = poliziaManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player poliziotto)) {
            sender.sendMessage(Testi.colora("&cQuesto comando puo' essere eseguito solo in gioco."));
            return true;
        }

        if (!poliziotto.hasPermission("tropicalcore.polizia.perquisisci")) {
            poliziotto.sendMessage(Testi.colora("&cNon hai i permessi per perquisire i cittadini."));
            return true;
        }

        if (!poliziaManager.isPoliziotto(poliziotto)) {
            poliziotto.sendMessage(Testi.colora("&cSolo un Agente di Polizia puo' perquisire i cittadini."));
            return true;
        }

        if (args.length < 1) {
            poliziotto.sendMessage(Testi.colora("&cUso corretto: /perquisisci <giocatore>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            poliziotto.sendMessage(Testi.colora("&cGiocatore non trovato o offline."));
            return true;
        }

        if (target.equals(poliziotto)) {
            poliziotto.sendMessage(Testi.colora("&cNon puoi perquisire te stesso."));
            return true;
        }

        if (poliziotto.getLocation().distance(target.getLocation()) > PoliziaManager.RAGGIO_MANETTE) {
            poliziotto.sendMessage(Testi.colora("&cIl cittadino e' troppo lontano per essere perquisito."));
            return true;
        }

        // Apre l'inventario reale del target (sola lettura per il poliziotto)
        poliziotto.openInventory(target.getInventory());
        poliziotto.sendMessage(Testi.colora("&aStai perquisendo l'inventario di &e" + target.getName()));
        target.sendMessage(Testi.colora("&cL'agente &e" + poliziotto.getName() + " &csta controllando le tue tasche."));
        return true;
    }
}
