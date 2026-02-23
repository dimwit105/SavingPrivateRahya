package com.zezdathecrystaldragon.savingPrivateRahya.game;

import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.CreateCageTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class WorldModifier
{
    private World nether;
    Game game;
    private CreateCageTask searchTask;
    public WorldModifier(World nether, Game game)
    {
        this.game = game;
        this.nether = nether;
        searchTask = new CreateCageTask(nether, this);
    }

    public void createVIPCage(World w, Location startCorner) {
        int cageSize = 5;
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

                    if (boundaryCount >= 2) {
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
    }
    public Location findOneRandomLocation(World nether) {
        java.util.Random random = new java.util.Random();

        // 1. Pick a random angle
        double theta = random.nextDouble() * 2 * Math.PI;

        // 2. Pick a radius between 1500 and 1700
        // To "skew" it outward, we raise the random 0.0-1.0 to a power < 1 (like 0.5)
        // or simply use Math.max(random, random) to favor higher numbers.
        double skew = Math.sqrt(random.nextDouble()); // Biases towards 1.0
        double r = 1500 + (skew * 200);

        // 3. Convert Polar to Cartesian (X, Z)
        int x = (int) (r * Math.cos(theta));
        int z = (int) (r * Math.sin(theta));

        // 4. Scan Y levels (standard Nether range)
        for (int y = 30; y < 110; y++) {
            Block floor = nether.getBlockAt(x, y, z);

            if (floor.getType().isSolid() && floor.getType() != Material.LAVA) {
                // Ensure 5x5x5 clear space above
                if (isAreaClear(nether, x, y + 1, z, 5)) {
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
                    // 2. Check if it's "Nether Tree Leaves"
                    if (type == Material.WARPED_WART_BLOCK || type == Material.NETHER_WART_BLOCK || type == Material.SHROOMLIGHT) {
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
}
