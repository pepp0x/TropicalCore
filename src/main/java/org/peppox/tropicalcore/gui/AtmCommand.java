package org.peppox.tropicalcore.gui;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.peppox.tropicalcore.TropicalCore;
import org.peppox.tropicalcore.util.Testi;

public class AtmCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Testi.colora("&cQuesto comando puo' essere eseguito solo in gioco."));
            return true;
        }

        Economy economy = TropicalCore.getInstance() != null ? TropicalCore.getInstance().getEconomy() : null;
        AtmMenu.apriATM(player, economy);
        return true;
    }
}
