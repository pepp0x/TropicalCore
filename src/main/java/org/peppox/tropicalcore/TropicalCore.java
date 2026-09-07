package org.peppox.tropicalcore;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.peppox.tropicalcore.actionbar.ActionBarTask;
import org.peppox.tropicalcore.jobs.JobManager;
import org.peppox.tropicalcore.jobs.polizia.PoliziaListener;
import org.peppox.tropicalcore.jobs.polizia.PoliziaManager;
import org.peppox.tropicalcore.terreno.TerrenoCommand;
import org.peppox.tropicalcore.terreno.TerrenoManager;
import org.peppox.tropicalcore.trade.TradeCommand;
import org.peppox.tropicalcore.trade.TradeManager;

@SuppressWarnings("unused")
public final class TropicalCore extends JavaPlugin {

    private static TropicalCore instance;
    private Economy economy;

    private JobManager jobManager;
    private PoliziaManager poliziaManager;
    private TerrenoManager terrenoManager;
    private TradeManager tradeManager;

    @Override
    public void onEnable() {
        instance = this;

        // 1. Setup Vault Economy
        if (!setupEconomy()) {
            getLogger().severe("Vault o un provider di economia non e' stato trovato! Disabilitazione del plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 2. Inizializzazione Manager (passando esattamente gli argomenti richiesti)
        this.jobManager = new JobManager();
        this.poliziaManager = new PoliziaManager(this.jobManager);
        this.terrenoManager = new TerrenoManager(this, this.economy);
        this.tradeManager = new TradeManager(this.economy);

        // 3. Registrazione Listener
        registerListeners();

        // 4. Registrazione Comandi
        registerCommands();

        // 5. Avvio Task ActionBar / HUD (ogni 20 tick = 1 secondo)
        new ActionBarTask(this.economy, this.jobManager).runTaskTimer(this, 0L, 20L);

        getLogger().info("TropicalCore abilitato con successo!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TropicalCore disabilitato.");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PoliziaListener(this.poliziaManager), this);
    }

    private void registerCommands() {
        PluginCommand terrenoCmd = getCommand("terreno");
        if (terrenoCmd != null) {
            TerrenoCommand executor = new TerrenoCommand(this.terrenoManager);
            terrenoCmd.setExecutor(executor);
            terrenoCmd.setTabCompleter(executor);
        } else {
            getLogger().warning("Comando /terreno non presente in plugin.yml");
        }

        PluginCommand tradeCmd = getCommand("trade");
        if (tradeCmd != null) {
            TradeCommand executor = new TradeCommand(this.tradeManager);
            tradeCmd.setExecutor(executor);
            tradeCmd.setTabCompleter(executor);
        } else {
            getLogger().warning("Comando /trade non presente in plugin.yml");
        }
    }

    // Getters
    public static TropicalCore getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public PoliziaManager getPoliziaManager() {
        return poliziaManager;
    }

    public TerrenoManager getTerrenoManager() {
        return terrenoManager;
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }
}