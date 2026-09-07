package org.peppox.tropicalcore.util;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VaultHook {

    private final JavaPlugin plugin;
    private Economy economy;

    public VaultHook(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registra il servizio Economy di Vault. Ritorna {@code false} se Vault o il suo provider
     * di economia non sono disponibili.
     */
    public boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().severe("Vault non trovato!");
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().severe("Nessun provider di economia registrato in Vault!");
            return false;
        }

        economy = rsp.getProvider();
        if (economy == null) {
            plugin.getLogger().severe("Provider di economia non valido!");
            return false;
        }
        return true;
    }

    @Nullable
    public Economy getEconomy() {
        return economy;
    }
}
