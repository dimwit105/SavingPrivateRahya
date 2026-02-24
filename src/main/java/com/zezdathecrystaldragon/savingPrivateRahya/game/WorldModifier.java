package com.zezdathecrystaldragon.savingPrivateRahya.game;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.CreateCageTask;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class WorldModifier
{
    /**
     * Materials we will absolutely bulldoze without a care in the world.
     */
    public final List<Material> nonBlockers;
    private final int cageSize = 5;
    private boolean ready = false;
    Game game;
    Location cageCenter;
    private final CreateCageTask cageSearchTask;
    public WorldModifier(Game game)
    {
        this.game = game;
        cageSearchTask = new CreateCageTask(game, this, cageSize + Math.floorDiv(Bukkit.getOnlinePlayers().size(), 5));
        ArrayList<Material> nb = new ArrayList<>();
        nb.addAll(Tag.LEAVES.getValues());
        nb.addAll(Tag.WART_BLOCKS.getValues());
        nb.add(Material.SHROOMLIGHT);
        nonBlockers = Collections.unmodifiableList(nb);
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runTimer(cageSearchTask, 5, 1);
    }

    public void createVIPCage(World w, Location startCorner) {

        // Cast doubles to ints for block coordinates
        int x0 = startCorner.getBlockX();
        int y0 = startCorner.getBlockY();
        int z0 = startCorner.getBlockZ();

        for (int x = x0; x < x0 + cageSize; x++) {
            for (int y = y0; y < y0 + cageSize; y++) {
                for (int z = z0; z < z0 + cageSize; z++) {

                    // Determine if we are on the very outer boundary of the cube
                    boolean isXBoundary = (x == x0 || x == x0 + cageSize - 1);
                    boolean isYBoundary = (y == y0 || y == y0 + cageSize - 1);
                    boolean isZBoundary = (z == z0 || z == z0 + cageSize - 1);

                    // An edge occurs where at least TWO boundaries meet (e.g., X and Y)
                    int boundaryCount = (isXBoundary ? 1 : 0) + (isYBoundary ? 1 : 0) + (isZBoundary ? 1 : 0);

                    Block block = w.getBlockAt(x, y, z);
                    if (y == cageSize && boundaryCount == 1) {
                        //Roofing, transparent, ghastproof block.
                        block.setType(Material.WAXED_COPPER_GRATE);
                    }
                    else if(y == y0 && boundaryCount == 1) {
                        //Flooring, standing on copper bars is a bad idea.
                        block.setType(Material.BLACKSTONE);
                    }
                    else if (boundaryCount >= 2) {
                        // Frame/Edges
                        block.setType(Material.BLACKSTONE);
                    } else if (boundaryCount == 1) {
                        // Flat walls/Faces
                        block.setType(Material.COPPER_BARS);
                    } else {
                        // Interior
                        block.setType(Material.AIR);
                    }
                }
            }
        }
        ready = true;
        cageCenter = startCorner.add(Math.floorDiv(cageSize, 2),1,Math.floorDiv(cageSize, 2));
        SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, String.format("Cage generated at %d, %d, %d", cageCenter.getBlockX(), cageCenter.getBlockY(), cageCenter.getBlockZ()));
    }
    public Location getCageCenter() {return cageCenter;}
    public boolean isReady() {return ready;}
}
