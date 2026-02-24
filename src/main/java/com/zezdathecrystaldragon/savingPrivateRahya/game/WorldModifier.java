package com.zezdathecrystaldragon.savingPrivateRahya.game;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.CreateCageTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class WorldModifier
{
    List<Material> nonBlockers;
    private final int cageSize = 5;
    private boolean ready = false;
    Game game;
    Location cageCenter;
    private final CreateCageTask cageSearchTask;
    public WorldModifier(Game game)
    {
        this.game = game;
        cageSearchTask = new CreateCageTask(game, this);
        nonBlockers.addAll(Tag.LEAVES.getValues());
        nonBlockers.addAll(Tag.WART_BLOCKS.getValues());
        nonBlockers.add(Material.SHROOMLIGHT);
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
    public Location findOneRandomLocation(World nether) {
        Random random = SavingPrivateRahya.RAND;

        // 1. Pick a random angle
        double theta = random.nextDouble() * 2 * Math.PI;

        // 2. Pick a radius between 1500 and 1700
        double skew = random.nextDouble();
        double r = 1500 + (skew * 200);

        // 3. Convert Polar to Cartesian (X, Z)
        int x = (int) (r * Math.cos(theta));
        int z = (int) (r * Math.sin(theta));

        // 4. Scan Y levels (standard Nether range)
        for (int y = 30; y < 110; y++) {
            Block floor = nether.getBlockAt(x, y, z);

            if (floor.getType().isSolid() && floor.getType() != Material.LAVA) {
                // Ensure 5x5x5 clear space above
                if (isAreaClear(nether, x, y + 1, z, cageSize)) {
                    // Ensure no lava directly beneath
                    if (nether.getBlockAt(x, y - 1, z).getType() != Material.LAVA) {
                        return new Location(nether, x, y, z);
                    }
                }
            }
        }
        return null;
    }
    private boolean isAreaClear(World w, int x0, int y0, int z0, int size) {
        for (int x = x0; x < x0 + size; x++) {
            // We check the 4 layers above the floor (y0 to y0 + size - 2)
            for (int y = y0; y < y0 + size - 1; y++) {
                for (int z = z0; z < z0 + size; z++) {
                    Block block = w.getBlockAt(x, y, z);
                    Material type = block.getType();

                    // 1. If it's air, it's definitely clear
                    if (type.isAir()) continue;
                    // 2. Ignore anything thats not important enough to keep
                    if (nonBlockers.contains(type)) {
                        continue;
                    }
                    // 3. Check for general non-colliding "clutter"
                    // (Vines, Weeping Vines, Crimson Roots, Sprouts, etc.)
                    if (!type.isSolid()) {
                        continue;
                    }
                    // If it's solid and not a "leaf" block, the area is blocked
                    return false;
                }
            }
        }
        return true;
    }
    public Location getCageCenter() {return cageCenter;}
    public boolean isReady() {return ready;}
}
