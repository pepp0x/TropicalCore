package org.peppox.tropicalcore.terreno;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.peppox.tropicalcore.TropicalCore;

import java.util.UUID;

/**
 * Wrapper per le operazioni WorldGuard: crea/rimuove region 16x16, verifica la
 * sovrapposizione e gestisce proprietari e membri.
 */
public class WorldGuardHook {

    /** Metà lato del terreno: 8 blocchi => plot 16x16 (esclusi i bordi delimitanti). */
    public static final int RAGGIO = 8;
    private static final String PREFISSO = "tropicalcore_";

    private final TropicalCore plugin;

    public WorldGuardHook(@NotNull TropicalCore plugin) {
        this.plugin = plugin;
    }

    public boolean isPresent() {
        return plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null;
    }

    @Nullable
    private RegionManager getRegionManager(@NotNull World world) {
        try {
            return WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(BukkitAdapter.adapt(world));
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Genera un id di region stabile a partire dalle coordinate centrali.
     */
    @NotNull
    public String generaId(@NotNull World world, int centroX, int centroZ) {
        return PREFISSO + world.getName() + "_" + centroX + "_" + centroZ;
    }

    /**
     * Crea una region 16x16 centrata sulle coordinate, con il player come proprietario.
     */
    public boolean creaRegion(@NotNull String regionId, @NotNull World world, int centroX, int centroZ, @NotNull Player proprietario) {
        RegionManager regionManager = getRegionManager(world);
        if (regionManager == null || regionManager.hasRegion(regionId)) {
            return false;
        }

        BlockVector3 min = BlockVector3.at(centroX - RAGGIO, world.getMinHeight(), centroZ - RAGGIO);
        BlockVector3 max = BlockVector3.at(centroX + RAGGIO, world.getMaxHeight() - 1, centroZ + RAGGIO);

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionId, min, max);

        DefaultDomain owners = new DefaultDomain();
        owners.addPlayer(BukkitAdapter.adapt(proprietario).getUniqueId());
        region.setOwners(owners);

        // Solo proprietari e membri possono costruire/interagire
        region.setFlag(Flags.BUILD, StateFlag.State.DENY);
        region.setFlag(Flags.INTERACT, StateFlag.State.DENY);

        regionManager.addRegion(region);
        return true;
    }

    /**
     * Verifica che l'area 16x16 centrata sulle coordinate sia libera da altre region.
     */
    public boolean areaLibera(@NotNull World world, int centroX, int centroZ) {
        RegionManager regionManager = getRegionManager(world);
        if (regionManager == null) {
            return true;
        }

        BlockVector3 min = BlockVector3.at(centroX - RAGGIO, world.getMinHeight(), centroZ - RAGGIO);
        BlockVector3 max = BlockVector3.at(centroX + RAGGIO, world.getMaxHeight() - 1, centroZ + RAGGIO);
        ProtectedCuboidRegion query = new ProtectedCuboidRegion("__tropicalcore_check__", min, max);

        return regionManager.getApplicableRegions(query).isEmpty();
    }

    /**
     * Ritorna la region TropicalCore alla posizione indicata, o {@code null}.
     */
    @Nullable
    public ProtectedRegion getRegionAt(@NotNull World world, @NotNull BlockVector3 posizione) {
        RegionManager regionManager = getRegionManager(world);
        if (regionManager == null) {
            return null;
        }
        for (ProtectedRegion region : regionManager.getApplicableRegions(posizione)) {
            if (region.getId().startsWith(PREFISSO)) {
                return region;
            }
        }
        return null;
    }

    public boolean isProprietario(@NotNull ProtectedRegion region, @NotNull UUID uuid) {
        return region.getOwners().getUniqueIds().contains(uuid);
    }

    public boolean aggiungiMembro(@NotNull ProtectedRegion region, @NotNull UUID uuid) {
        region.getMembers().addPlayer(uuid);
        return true;
    }

    public boolean rimuoviMembro(@NotNull ProtectedRegion region, @NotNull UUID uuid) {
        region.getMembers().removePlayer(uuid);
        return true;
    }

    public boolean rimuoviRegion(@NotNull String regionId, @NotNull World world) {
        RegionManager regionManager = getRegionManager(world);
        if (regionManager == null) {
            return false;
        }
        regionManager.removeRegion(regionId);
        return true;
    }
}
