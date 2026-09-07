package org.peppox.tropicalcore.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsHook {

    private LuckPerms luckPerms;

    public boolean setup() {
        try {
            this.luckPerms = LuckPermsProvider.get();
            return true;
        } catch (IllegalStateException e) {
            // LuckPerms non è installato o non è ancora pronto
            return false;
        }
    }

    /**
     * Aggiunge il player al gruppo specificato (es. "poliziotto"),
     * mantenendo eventuali altri gruppi già presenti.
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