package org.peppox.tropicalcore.jobs.polizia;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.peppox.tropicalcore.util.Testi;

public class ManetteCommand implements CommandExecutor {

    private final PoliziaManager poliziaManager;

    public ManetteCommand(PoliziaManager poliziaManager) {
        this.poliziaManager = poliziaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player poliziotto)) {
            sender.sendMessage(Testi.colora("&cQuesto comando può essere eseguito solo in gioco."));
            return true;
        }

        if (!poliziotto.hasPermission("tropicalcore.polizia.manette")) {
            poliziotto.sendMessage(Testi.colora("&cNon hai i permessi da Forze dell'Ordine per usare questo comando."));
            return true;
        }

        if (args.length < 1) {
            poliziotto.sendMessage(Testi.colora("&cUso corretto: /manette <giocatore>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            poliziotto.sendMessage(Testi.colora("&cGiocatore non trovato o offline."));
            return true;
        }

        if (poliziotto.getLocation().distance(target.getLocation()) > 4.0) {
            poliziotto.sendMessage(Testi.colora("&cIl cittadino è troppo lontano per essere ammanettato."));
            return true;
        }

        boolean statoAttuale = poliziaManager.isAmmanettato(target);

        if (statoAttuale) {
            poliziaManager.setAmmanettato(target, false);
            target.sendMessage(Testi.colora("&aTi sono state rimosse le manette."));
            poliziotto.sendMessage(Testi.colora("&aHai rimosso le manette a &e" + target.getName()));
        } else {
            poliziaManager.setAmmanettato(target, true);
            target.sendMessage(Testi.colora("&cSei stato ammanettato dall'agente &e" + poliziotto.getName()));
            poliziotto.sendMessage(Testi.colora("&aHai ammanettato &e" + target.getName()));
        }

        return true;
    }
}