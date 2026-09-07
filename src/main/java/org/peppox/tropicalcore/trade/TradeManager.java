package org.peppox.tropicalcore.trade;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeManager {

    private final Economy economy;
    private final Map<UUID, UUID> richiesteScambio = new HashMap<>();

    public TradeManager(@NotNull Economy economy) {
        this.economy = economy;
    }

    public void richiediScambio(@NotNull Player mittente, @NotNull Player destinatario) {
        richiesteScambio.put(destinatario.getUniqueId(), mittente.getUniqueId());

        mittente.sendMessage(Component.text("Richiesta di scambio inviata a " + destinatario.getName(), NamedTextColor.GREEN));
        destinatario.sendMessage(Component.text(mittente.getName() + " ti ha chiesto di scambiare. Scrivi /trade accept per accettare.", NamedTextColor.YELLOW));
    }

    public void accettaScambio(@NotNull Player destinatario) {
        UUID mittenteId = richiesteScambio.remove(destinatario.getUniqueId());
        if (mittenteId == null) {
            destinatario.sendMessage(Component.text("Non hai nessuna richiesta di scambio in sospeso.", NamedTextColor.RED));
            return;
        }

        destinatario.sendMessage(Component.text("Scambio accettato!", NamedTextColor.GREEN));
    }

    public void rifiutaScambio(@NotNull Player destinatario) {
        UUID mittenteId = richiesteScambio.remove(destinatario.getUniqueId());
        if (mittenteId == null) {
            destinatario.sendMessage(Component.text("Non hai nessuna richiesta di scambio da rifiutare.", NamedTextColor.RED));
            return;
        }

        destinatario.sendMessage(Component.text("Hai rifiutato lo scambio.", NamedTextColor.RED));
    }
}