package org.peppox.tropicalcore.terreno;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.peppox.tropicalcore.TropicalCore;
import org.peppox.tropicalcore.util.Testi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TerrenoManager {

    private static final double PREZZO_TERRENO = 5000.0;

    private final TropicalCore plugin;
    private final Economy economy;
    private final WorldGuardHook worldGuardHook;
    private final Map<String, Terreno> terreni = new HashMap<>();

    public TerrenoManager(@NotNull TropicalCore plugin, @NotNull Economy economy) {
        this.plugin = plugin;
        this.economy = economy;
        this.worldGuardHook = new WorldGuardHook(plugin);
    }

    public void acquistaTerreno(@NotNull Player player) {
        if (!worldGuardHook.isPresent()) {
            player.sendMessage(Testi.colora("&cWorldGuard non e' installato: impossibile acquistare terreni."));
            return;
        }

        int centroX = player.getLocation().getBlockX();
        int centroZ = player.getLocation().getBlockZ();
        String id = worldGuardHook.generaId(player.getWorld(), centroX, centroZ);

        if (terreni.containsKey(id)) {
            player.sendMessage(Testi.colora("&cHai gia' acquistato questo terreno."));
            return;
        }

        if (!worldGuardHook.areaLibera(player.getWorld(), centroX, centroZ)) {
            player.sendMessage(Testi.colora("&cQuesta area e' gia' occupata da un altro terreno."));
            return;
        }

        if (economy == null || !economy.has(player, PREZZO_TERRENO)) {
            player.sendMessage(Testi.colora("&cNon hai abbastanza soldi: servono &e€" + String.format("%.2f", PREZZO_TERRENO)));
            return;
        }

        if (!worldGuardHook.creaRegion(id, player.getWorld(), centroX, centroZ, player)) {
            player.sendMessage(Testi.colora("&cImpossibile creare la region del terreno."));
            return;
        }

        economy.withdrawPlayer(player, PREZZO_TERRENO);

        Terreno terreno = new Terreno(id, player.getUniqueId(), player.getWorld().getName(), centroX, centroZ);
        terreni.put(id, terreno);

        player.sendMessage(Testi.colora("&aTerreno acquistato per &e€" + String.format("%.2f", PREZZO_TERRENO) + "&a!"));
    }

    public void mostraInfoTerreno(@NotNull Player player) {
        ProtectedRegion region = regioneAllaPosizione(player);
        if (region == null) {
            player.sendMessage(Testi.colora("&cNon ti trovi in nessun terreno registrato."));
            return;
        }

        player.sendMessage(Testi.colora("&e&lTERRENO &8» &7ID: &f" + region.getId()));
        player.sendMessage(Testi.colora("&7Proprietario: &f" + nomeProprietario(region)));
        player.sendMessage(Testi.colora("&7Membri: &f" + nomiMembri(region)));
    }

    public void aggiungiMembro(@NotNull Player proprietario, @NotNull String nomeMembro) {
        if (!proprietario.hasPermission("tropicalcore.terreno.add")) {
            proprietario.sendMessage(Testi.colora("&cNon hai il permesso di aggiungere membri."));
            return;
        }

        ProtectedRegion region = regioneDelGiocatore(proprietario);
        if (region == null) {
            proprietario.sendMessage(Testi.colora("&cNon sei il proprietario di nessun terreno qui."));
            return;
        }

        Player membro = Bukkit.getPlayerExact(nomeMembro);
        if (membro == null) {
            proprietario.sendMessage(Testi.colora("&cGiocatore non trovato o offline."));
            return;
        }

        if (region.getMembers().getUniqueIds().contains(membro.getUniqueId())) {
            proprietario.sendMessage(Testi.colora("&e" + membro.getName() + " &ce' gia' membro di questo terreno."));
            return;
        }

        worldGuardHook.aggiungiMembro(region, membro.getUniqueId());
        proprietario.sendMessage(Testi.colora("&aAggiunto &e" + membro.getName() + " &aal terreno."));
        membro.sendMessage(Testi.colora("&aSei stato aggiunto al terreno di &e" + proprietario.getName()));
    }

    public void rimuoviMembro(@NotNull Player proprietario, @NotNull String nomeMembro) {
        if (!proprietario.hasPermission("tropicalcore.terreno.remove")) {
            proprietario.sendMessage(Testi.colora("&cNon hai il permesso di rimuovere membri."));
            return;
        }

        ProtectedRegion region = regioneDelGiocatore(proprietario);
        if (region == null) {
            proprietario.sendMessage(Testi.colora("&cNon sei il proprietario di nessun terreno qui."));
            return;
        }

        OfflinePlayer membro = Bukkit.getOfflinePlayer(nomeMembro);
        if (membro.getUniqueId().equals(proprietario.getUniqueId())) {
            proprietario.sendMessage(Testi.colora("&cNon puoi rimuovere te stesso dal terreno."));
            return;
        }

        if (!region.getMembers().getUniqueIds().contains(membro.getUniqueId())) {
            proprietario.sendMessage(Testi.colora("&c" + (membro.getName() != null ? membro.getName() : nomeMembro) + " non e' membro di questo terreno."));
            return;
        }

        worldGuardHook.rimuoviMembro(region, membro.getUniqueId());
        proprietario.sendMessage(Testi.colora("&aRimosso &e" + nomeMembro + " &adal terreno."));

        Player online = membro.getPlayer();
        if (online != null) {
            online.sendMessage(Testi.colora("&cSei stato rimosso dal terreno di &e" + proprietario.getName()));
        }
    }

    /**
     * Region TropicalCore alla posizione del player.
     */
    @Nullable
    private ProtectedRegion regioneAllaPosizione(@NotNull Player player) {
        return worldGuardHook.getRegionAt(player.getWorld(), BlockVector3.at(
                player.getLocation().getBlockX(),
                player.getWorld().getMinHeight() + 1,
                player.getLocation().getBlockZ()));
    }

    /**
     * Region TropicalCore alla posizione del player, solo se il player ne e' proprietario.
     */
    @Nullable
    private ProtectedRegion regioneDelGiocatore(@NotNull Player player) {
        ProtectedRegion region = regioneAllaPosizione(player);
        if (region == null || !worldGuardHook.isProprietario(region, player.getUniqueId())) {
            return null;
        }
        return region;
    }

    private String nomeProprietario(@NotNull ProtectedRegion region) {
        Set<UUID> proprietari = region.getOwners().getUniqueIds();
        if (proprietari.isEmpty()) {
            return "Nessuno";
        }
        UUID uuid = proprietari.iterator().next();
        OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
        return offline.getName() != null ? offline.getName() : uuid.toString();
    }

    private String nomiMembri(@NotNull ProtectedRegion region) {
        Set<UUID> membri = region.getMembers().getUniqueIds();
        if (membri.isEmpty()) {
            return "Nessuno";
        }
        StringBuilder sb = new StringBuilder();
        for (UUID uuid : membri) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
            if (sb.length() > 0) {
                sb.append("&7, &f");
            }
            sb.append(offline.getName() != null ? offline.getName() : uuid.toString());
        }
        return sb.toString();
    }
}
