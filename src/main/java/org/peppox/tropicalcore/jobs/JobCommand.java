package org.peppox.tropicalcore.jobs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.peppox.tropicalcore.util.Testi;

public class JobCommand implements CommandExecutor {

    private final JobManager jobManager;

    public JobCommand(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Testi.colora("&cQuesto comando può essere eseguito solo da un giocatore."));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(Testi.colora("&e:lavoro: Il tuo lavoro attuale: &a" + jobManager.getJob(player)));
            player.sendMessage(Testi.colora("&7Usa /lavoro set <nome> per assegnarti un lavoro (test)."));
            return true;
        }

        if (args[0].equalsIgnoreCase("set") && args.length >= 2) {
            String job = args[1];
            jobManager.setJob(player, job);
            player.sendMessage(Testi.colora("&aOra lavori come: &e" + job));
            return true;
        }

        player.sendMessage(Testi.colora("&cUso corretto: /lavoro oppure /lavoro set <lavoro>"));
        return true;
    }
}