package org.peppox.tropicalcore.terreno;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.domains.DefaultDomain;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldGuardHook {

    private static final int RAGGIO = 8; // 16x16 totale, 8 blocchi per lato dal centro

    /**
     * Crea una region WorldGuard 16x16 centrata sulla posizione del player,
     * con il player come proprietario (puo' costruire, altri no).
     */
    public boolean creaRegion(String regionId, World world, int centroX, int centroZ, Player proprietario) {
        RegionManager regionManager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(world));

        if (regionManager == null) return false;

        // Verifica che non ci sia gia' una region con lo stesso nome
        if (regionManager.hasRegion(regionId)) return false;

        BlockVector3 min = BlockVector3.at(centroX - RAGGIO, world.getMinHeight(), centroZ - RAGGIO);
        BlockVector3 max = BlockVector3.at(centroX + RAGGIO, world.getMaxHeight(), centroZ + RAGGIO);

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionId, min, max);

        DefaultDomain owners = new DefaultDomain();
        owners.addPlayer(BukkitAdapter.adapt(proprietario).getUniqueId());
        region.setOwners(owners);

        // Impedisce build/interazione a chiunque non sia proprietario
        region.setFlag(Flags.BUILD, com.sk89q.worldguard.protection.flags.StateFlag.State.DENY);

        regionManager.addRegion(region);
        return true;
    }

    /**
     * Verifica se la posizione indicata e' gia' dentro una region esistente
     * (per impedire acquisti sovrapposti).
     */
    public boolean areaLibera(World world, int centroX, int centroZ) {
        RegionManager regionManager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(world));

        if (regionManager == null) return false;

        BlockVector3 punto = BlockVector3.at(centroX, world.getMinHeight() + 1, centroZ);

        ProtectedRegion punto_check = new ProtectedCuboidRegion(
                "check_temp",
                punto,
                BlockVector3.at(centroX, world.getMinHeight() + 1, centroZ)
        );

        return regionManager.getApplicableRegions(punto_check).size() == 0;
    }

    public boolean rimuoviRegion(String regionId, World world) {
        RegionManager regionManager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(world));

        if (regionManager == null) return false;

        regionManager.removeRegion(regionId);
        return true;
    }
}