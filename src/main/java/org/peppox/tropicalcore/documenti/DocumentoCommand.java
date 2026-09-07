package org.peppox.tropicalcore.documenti;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.peppox.tropicalcore.TropicalCore;
import org.peppox.tropicalcore.util.Testi;

public class DocumentoCommand implements CommandExecutor {

    private final TropicalCore plugin;
    private final DocumentoManager documentoManager;

    public DocumentoCommand(TropicalCore plugin, DocumentoManager documentoManager) {
        this.plugin = plugin;
        this.documentoManager = documentoManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Testi.colora("&cQuesto comando può essere eseguito solo in gioco."));
            return true;
        }

        Player player = (Player) sender;

        // Uso: /cdi give <giocatore>
        if (args.length >= 2 && args[0].equalsIgnoreCase("give")) {
            if (!player.hasPermission("tropicalcore.admin.cdi")) {
                player.sendMessage(Testi.colora("&cNon hai il permesso per rilasciare documenti."));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(Testi.colora("&cGiocatore non trovato o offline."));
                return true;
            }

            String lavoro = "Disoccupato";
            if (plugin.getJobManager() != null) {
                lavoro = plugin.getJobManager().getJob(target);
            }

            ItemStack carta = documentoManager.creaCartaIdentita(target, lavoro);
            target.getInventory().addItem(carta);

            target.sendMessage(Testi.colora("&aHai ricevuto la tua Carta d'Identità!"));
            player.sendMessage(Testi.colora("&aCarta d'Identità consegnata con successo a &e" + target.getName()));
            return true;
        }

        player.sendMessage(Testi.colora("&cUso corretto: /cdi give <giocatore>"));
        return true;
    }
}