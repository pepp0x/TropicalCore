package org.peppox.tropicalcore.trade;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TradeCommand implements CommandExecutor, TabCompleter {

    private final TradeManager tradeManager;

    public TradeCommand(@NotNull TradeManager tradeManager) {
        this.tradeManager = tradeManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player mittente)) {
            sender.sendMessage(Component.text("Questo comando puo' essere eseguito solo da un giocatore.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            mittente.sendMessage(Component.text("Uso corretto: /trade <giocatore> oppure /trade accept/deny", NamedTextColor.RED));
            return true;
        }

        String targetName = args[0];

        if (targetName.equalsIgnoreCase("accept")) {
            tradeManager.accettaScambio(mittente);
            return true;
        }

        if (targetName.equalsIgnoreCase("deny") || targetName.equalsIgnoreCase("refuse")) {
            tradeManager.rifiutaScambio(mittente);
            return true;
        }

        Player destinatario = Bukkit.getPlayer(targetName);
        if (destinatario == null || !destinatario.isOnline()) {
            mittente.sendMessage(Component.text("Giocatore non trovato o offline.", NamedTextColor.RED));
            return true;
        }

        if (destinatario.equals(mittente)) {
            mittente.sendMessage(Component.text("Non puoi avviare uno scambio con te stesso!", NamedTextColor.RED));
            return true;
        }

        tradeManager.richiediScambio(mittente, destinatario);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("accept");
            completions.add("deny");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getName().equalsIgnoreCase(sender.getName()) && player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        return completions;
    }
}