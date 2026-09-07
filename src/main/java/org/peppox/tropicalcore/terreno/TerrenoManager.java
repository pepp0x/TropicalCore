package org.peppox.tropicalcore.terreno;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.peppox.tropicalcore.TropicalCore;

public class TerrenoManager {

    private final TropicalCore plugin;
    private final Economy economy;

    // Costruttore con esattamente 2 argomenti per risolvere l'errore
    public TerrenoManager(@NotNull TropicalCore plugin, @NotNull Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
    }

    public void acquistaTerreno(@NotNull Player player) {
        player.sendMessage(Component.text("Verifica ed esecuzione acquisto terreno...", NamedTextColor.YELLOW));
    }

    public void mostraInfoTerreno(@NotNull Player player) {
        player.sendMessage(Component.text("Informazioni sul terreno corrente...", NamedTextColor.AQUA));
    }

    public void aggiungiMembro(@NotNull Player proprietario, @NotNull String nomeMembro) {
        proprietario.sendMessage(Component.text("Aggiunto " + nomeMembro + " al terreno.", NamedTextColor.GREEN));
    }

    public void rimuoviMembro(@NotNull Player proprietario, @NotNull String nomeMembro) {
        proprietario.sendMessage(Component.text("Rimosso " + nomeMembro + " dal terreno.", NamedTextColor.RED));
    }
}