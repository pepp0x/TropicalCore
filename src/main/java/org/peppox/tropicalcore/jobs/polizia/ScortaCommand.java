package org.peppox.tropicalcore.jobs.polizia;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.peppox.tropicalcore.util.Testi;

public class ScortaCommand implements CommandExecutor {

    private final PoliziaManager poliziaManager;

    public ScortaCommand(PoliziaManager poliziaManager) {
        this.poliziaManager = poliziaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player poliziotto)) {
            sender.sendMessage(Testi.colora("&cQuesto comando può essere eseguito solo in gioco."));
            return true;
        }

        if (!poliziotto.hasPermission("tropicalcore.polizia.scorta")) {
            poliziotto.sendMessage(Testi.colora("&cNon hai i permessi per scortare i cittadini."));
            return true;
        }

        if (args.length < 1) {
            poliziotto.sendMessage(Testi.colora("&cUso corretto: /scorta <giocatore>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            poliziotto.sendMessage(Testi.colora("&cGiocatore non trovato o offline."));
            return true;
        }

        if (!poliziaManager.isAmmanettato(target)) {
            poliziotto.sendMessage(Testi.colora("&cDevi ammanettare il cittadino prima di poterlo scortare!"));
            return true;
        }

        if (poliziotto.getLocation().distance(target.getLocation()) > 5.0) {
            poliziotto.sendMessage(Testi.colora("&cIl cittadino è troppo lontano per essere preso in scorta."));
            return true;
        }

        poliziaManager.toggleScorta(target, poliziotto);

        if (poliziaManager.isInScorta(target)) {
            poliziotto.sendMessage(Testi.colora("&aStai scortando &e" + target.getName()));
            target.sendMessage(Testi.colora("&cL'agente &e" + poliziotto.getName() + " &cti sta scortando."));
        } else {
            poliziotto.sendMessage(Testi.colora("&aHai rilasciato dalla scorta &e" + target.getName()));
            target.sendMessage(Testi.colora("&aL'agente ti ha rilasciato dalla scorta."));
        }

        return true;
    }
}