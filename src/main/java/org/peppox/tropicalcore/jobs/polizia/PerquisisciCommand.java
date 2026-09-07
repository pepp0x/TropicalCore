package org.peppox.tropicalcore.jobs.polizia;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.peppox.tropicalcore.util.Testi;

public class PerquisisciCommand implements CommandExecutor {

    private final PoliziaManager poliziaManager;

    public PerquisisciCommand(PoliziaManager poliziaManager) {
        this.poliziaManager = poliziaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player poliziotto)) {
            poliziottoSendMessage(sender, "&cQuesto comando può essere eseguito solo in gioco.");
            return true;
        }

        if (!poliziotto.hasPermission("tropicalcore.polizia.perquisisci")) {
            poliziotto.sendMessage(Testi.colora("&cNon hai i permessi per perquisire i cittadini."));
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

        if (poliziotto.getLocation().distance(target.getLocation()) > 4.0) {
            poliziotto.sendMessage(Testi.colora("&cIl cittadino è troppo lontano per essere perquisito."));
            return true;
        }

        // Apre l'inventario reale del target
        poliziotto.openInventory(target.getInventory());
        poliziotto.sendMessage(Testi.colora("&aStai perquisendo l'inventario di &e" + target.getName()));
        target.sendMessage(Testi.colora("&cL'agente &e" + poliziotto.getName() + " &csta controllando le tue tasche."));

        return true;
    }

    private void poliziottoSendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Testi.colora(msg));
    }
}