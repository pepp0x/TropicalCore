package org.peppox.tropicalcore.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsHook {

    private LuckPerms luckPerms;

    /**
     * Inizializza l'hook. Ritorna {@code false} se LuckPerms non e' installato o non e' pronto.
     * E' sicuro da chiamare anche quando LuckPerms manca (soft dependency).
     */
    public boolean setup() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            return false;
        }
        try {
            this.luckPerms = LuckPermsProvider.get();
            return this.luckPerms != null;
        } catch (Throwable t) {
            // LuckPerms presente ma non ancora pronto
            return false;
        }
    }

    public boolean isReady() {
        return luckPerms != null;
    }

    /**
     * Aggiunge il player al gruppo specificato (es. "poliziotto"),
     * mantenendo eventuali altri gruppi gia' presenti.
     */
    public CompletableFuture<Void> addToGroup(UUID playerUuid, String groupName) {
        return luckPerms.getUserManager().modifyUser(playerUuid, user -> {
            Node node = InheritanceNode.builder(groupName).build();
            user.data().add(node);
        });
    }

    /**
     * Rimuove il player da un gruppo specifico (es. quando cambia lavoro).
     */
    public CompletableFuture<Void> removeFromGroup(UUID playerUuid, String groupName) {
        return luckPerms.getUserManager().modifyUser(playerUuid, user -> {
            Node node = InheritanceNode.builder(groupName).build();
            user.data().remove(node);
        });
    }

    public LuckPerms getApi() {
        return luckPerms;
    }
}
