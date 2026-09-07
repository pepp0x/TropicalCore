package org.peppox.tropicalcore.terreno;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TerrenoCommand implements CommandExecutor, TabCompleter {

    private final TerrenoManager terrenoManager;

    public TerrenoCommand(@NotNull TerrenoManager terrenoManager) {
        this.terrenoManager = terrenoManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Questo comando puo' essere eseguito solo da un giocatore.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            mostraHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "buy", "acquista" -> terrenoManager.acquistaTerreno(player);
            case "info" -> terrenoManager.mostraInfoTerreno(player);
            case "add" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Uso corretto: /terreno add <giocatore>", NamedTextColor.RED));
                    return true;
                }
                terrenoManager.aggiungiMembro(player, args[1]);
            }
            case "remove" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.text("Uso corretto: /terreno remove <giocatore>", NamedTextColor.RED));
                    return true;
                }
                terrenoManager.rimuoviMembro(player, args[1]);
            }
            default -> mostraHelp(player);
        }

        return true;
    }

    private void mostraHelp(@NotNull Player player) {
        player.sendMessage(Component.text("--- [ Gestione Terreni ] ---", NamedTextColor.GOLD));
        player.sendMessage(Component.text("/terreno acquista ", NamedTextColor.YELLOW)
                .append(Component.text("- Compra il terreno in cui ti trovi", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/terreno info ", NamedTextColor.YELLOW)
                .append(Component.text("- Visualizza info sul terreno", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/terreno add <player> ", NamedTextColor.YELLOW)
                .append(Component.text("- Aggiungi un membro al tuo terreno", NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/terreno remove <player> ", NamedTextColor.YELLOW)
                .append(Component.text("- Rimuovi un membro dal terreno", NamedTextColor.GRAY)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> subCommands = List.of("acquista", "info", "add", "remove");
            for (String sub : subCommands) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        }
        return completions;
    }
}