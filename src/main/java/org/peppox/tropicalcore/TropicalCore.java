package org.peppox.tropicalcore;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.peppox.tropicalcore.actionbar.ActionBarTask;
import org.peppox.tropicalcore.chat.ChatListener;
import org.peppox.tropicalcore.documenti.DocumentoCommand;
import org.peppox.tropicalcore.documenti.DocumentoListener;
import org.peppox.tropicalcore.documenti.DocumentoManager;
import org.peppox.tropicalcore.gui.AtmCommand;
import org.peppox.tropicalcore.gui.MenuListener;
import org.peppox.tropicalcore.jobs.JobCommand;
import org.peppox.tropicalcore.jobs.JobManager;
import org.peppox.tropicalcore.jobs.polizia.ManetteCommand;
import org.peppox.tropicalcore.jobs.polizia.PerquisisciCommand;
import org.peppox.tropicalcore.jobs.polizia.PoliziaListener;
import org.peppox.tropicalcore.jobs.polizia.PoliziaManager;
import org.peppox.tropicalcore.jobs.polizia.ScortaCommand;
import org.peppox.tropicalcore.terreno.TerrenoCommand;
import org.peppox.tropicalcore.terreno.TerrenoManager;
import org.peppox.tropicalcore.trade.TradeCommand;
import org.peppox.tropicalcore.trade.TradeManager;
import org.peppox.tropicalcore.util.LuckPermsHook;
import org.peppox.tropicalcore.util.VaultHook;

public final class TropicalCore extends JavaPlugin {

    private static TropicalCore instance;

    private VaultHook vaultHook;
    private LuckPermsHook luckPermsHook;

    private JobManager jobManager;
    private PoliziaManager poliziaManager;
    private TerrenoManager terrenoManager;
    private TradeManager tradeManager;
    private DocumentoManager documentoManager;

    @Override
    public void onEnable() {
        instance = this;

        // 1. Setup Vault Economy (hard dependency)
        this.vaultHook = new VaultHook(this);
        if (!vaultHook.setupEconomy()) {
            getLogger().severe("Vault o un provider di economia non e' stato trovato! Disabilitazione del plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 2. Setup LuckPerms (soft dependency)
        this.luckPermsHook = new LuckPermsHook();
        if (luckPermsHook.setup()) {
            getLogger().info("Hook LuckPerms inizializzato.");
        } else {
            getLogger().warning("LuckPerms non trovato: la gestione dei gruppi lavoro sara' disabilitata.");
        }

        // 3. Inizializzazione dei Manager
        this.jobManager = new JobManager();
        this.poliziaManager = new PoliziaManager(this.jobManager);
        this.terrenoManager = new TerrenoManager(this, this.vaultHook.getEconomy());
        this.tradeManager = new TradeManager(this.vaultHook.getEconomy());
        this.documentoManager = new DocumentoManager(this);

        // 4. Registrazione Listener
        registerListeners();

        // 5. Registrazione Comandi
        registerCommands();

        // 6. Avvio Task ActionBar / HUD (ogni 20 tick = 1 secondo)
        new ActionBarTask(this.vaultHook.getEconomy(), this.jobManager).runTaskTimer(this, 0L, 20L);

        getLogger().info("TropicalCore abilitato con successo!");
    }

    @Override
    public void onDisable() {
        if (tradeManager != null) {
            tradeManager.closeAllSessions();
        }
        getLogger().info("TropicalCore disabilitato.");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PoliziaListener(this.poliziaManager), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this.vaultHook.getEconomy()), this);
        getServer().getPluginManager().registerEvents(this.tradeManager, this);
        getServer().getPluginManager().registerEvents(new DocumentoListener(this.documentoManager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }

    private void registerCommands() {
        // /atm (GUI bancomat)
        PluginCommand atmCmd = getCommand("atm");
        if (atmCmd != null) {
            atmCmd.setExecutor(new AtmCommand());
        } else {
            getLogger().warning("Comando /atm non presente in plugin.yml");
        }

        // /lavoro
        PluginCommand lavoroCmd = getCommand("lavoro");
        if (lavoroCmd != null) {
            lavoroCmd.setExecutor(new JobCommand(this.jobManager, this.luckPermsHook));
        } else {
            getLogger().warning("Comando /lavoro non presente in plugin.yml");
        }

        // /trade
        PluginCommand tradeCmd = getCommand("trade");
        if (tradeCmd != null) {
            TradeCommand executor = new TradeCommand(this.tradeManager);
            tradeCmd.setExecutor(executor);
            tradeCmd.setTabCompleter(executor);
        } else {
            getLogger().warning("Comando /trade non presente in plugin.yml");
        }

        // /terreno
        PluginCommand terrenoCmd = getCommand("terreno");
        if (terrenoCmd != null) {
            TerrenoCommand executor = new TerrenoCommand(this.terrenoManager);
            terrenoCmd.setExecutor(executor);
            terrenoCmd.setTabCompleter(executor);
        } else {
            getLogger().warning("Comando /terreno non presente in plugin.yml");
        }

        // /cdi (documenti)
        PluginCommand cdiCmd = getCommand("cdi");
        if (cdiCmd != null) {
            cdiCmd.setExecutor(new DocumentoCommand(this, this.documentoManager));
        } else {
            getLogger().warning("Comando /cdi non presente in plugin.yml");
        }

        // /manette
        PluginCommand manetteCmd = getCommand("manette");
        if (manetteCmd != null) {
            manetteCmd.setExecutor(new ManetteCommand(this.poliziaManager));
        } else {
            getLogger().warning("Comando /manette non presente in plugin.yml");
        }

        // /perquisisci
        PluginCommand perquisisciCmd = getCommand("perquisisci");
        if (perquisisciCmd != null) {
            perquisisciCmd.setExecutor(new PerquisisciCommand(this.poliziaManager));
        } else {
            getLogger().warning("Comando /perquisisci non presente in plugin.yml");
        }

        // /scorta
        PluginCommand scortaCmd = getCommand("scorta");
        if (scortaCmd != null) {
            scortaCmd.setExecutor(new ScortaCommand(this.poliziaManager));
        } else {
            getLogger().warning("Comando /scorta non presente in plugin.yml");
        }
    }

    // Getters
    public static TropicalCore getInstance() {
        return instance;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public LuckPermsHook getLuckPermsHook() {
        return luckPermsHook;
    }

    public Economy getEconomy() {
        return vaultHook != null ? vaultHook.getEconomy() : null;
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

    public DocumentoManager getDocumentoManager() {
        return documentoManager;
    }
}
