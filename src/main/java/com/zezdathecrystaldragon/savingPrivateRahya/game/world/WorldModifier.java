package com.zezdathecrystaldragon.savingPrivateRahya.game.world;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks.CreateCageTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks.CreateNetherPortalTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks.CreateOverworldPortalTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks.WorldTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.logging.Level;

public class WorldModifier
{
    /**
     * Materials we will absolutely bulldoze without a care in the world.
     */
    public final List<Material> nonBlockers;
    private final int cageSize = 5;
    private final NamespacedKey OVERWORLD_PORTAL_KEY;
    private final NamespacedKey NETHER_PORTAL_KEY;
    private boolean ready = false;
    Game game;
    Location cageCenter;
    Location nethersidePortal;
    private final CreateCageTask cageSearchTask;
    public WorldModifier(Game game)
    {
        this.game = game;
        cageSearchTask = new CreateCageTask(game, this, cageSize);
        ArrayList<Material> nb = new ArrayList<>();
        nb.addAll(Tag.LEAVES.getValues());
        nb.addAll(Tag.WART_BLOCKS.getValues());
        nb.add(Material.SHROOMLIGHT);
        nonBlockers = Collections.unmodifiableList(nb);
        this.OVERWORLD_PORTAL_KEY = new NamespacedKey(SavingPrivateRahya.PLUGIN, "overworld_portal_generated");
        this.NETHER_PORTAL_KEY = new NamespacedKey(SavingPrivateRahya.PLUGIN, "nether_portal_generated");

        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runTimer(cageSearchTask, 5, 1);

        checkAndGeneratePortal(game.overworld, OVERWORLD_PORTAL_KEY, new CreateOverworldPortalTask(game, this));

        checkAndGeneratePortal(game.nether, NETHER_PORTAL_KEY, new CreateNetherPortalTask(game, this));
    }

    private void checkAndGeneratePortal(World world, NamespacedKey key, WorldTask task) {
        PersistentDataContainer pdc = world.getPersistentDataContainer();

        // If the flag doesn't exist (Byte 0 or null), run the task
        if (!pdc.has(key, PersistentDataType.BYTE)) {
            // We run the task. We'll handle the "Success" flag inside the task's completion.
            task.run();
        } else {
            SavingPrivateRahya.PLUGIN.getLogger().info("Skipping portal generation for " + world.getName() + ": Already exists.");
        }
    }
    //TODO: mark portals as generated. Purposefully disabled right now for testing.
    public void markPortalGenerated(World world, boolean isNether) {
        NamespacedKey key = isNether ? NETHER_PORTAL_KEY : OVERWORLD_PORTAL_KEY;
        world.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
        SavingPrivateRahya.PLUGIN.getLogger().info("Portal flag saved to PDC for " + world.getName());
    }

    public void createVIPCage(World w, Location startCorner) {

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
                    if (y == y0 + cageSize - 1 && boundaryCount == 1) {
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
                        if (block.getBlockData() instanceof org.bukkit.block.data.MultipleFacing bars) {
                            for (org.bukkit.block.BlockFace face : new org.bukkit.block.BlockFace[]{
                                    org.bukkit.block.BlockFace.NORTH, org.bukkit.block.BlockFace.SOUTH,
                                    org.bukkit.block.BlockFace.EAST, org.bukkit.block.BlockFace.WEST
                            }) {
                                Block neighbor = block.getRelative(face);
                                if (!neighbor.getType().isAir()) {
                                    bars.setFace(face, true);
                                }
                            }
                            block.setBlockData(bars);
                        }
                    } else {
                        // Interior
                        block.setType(Material.AIR);
                    }
                    block.getState().update(true);
                }
            }
        }
        ready = true;
        cageCenter = startCorner.clone().add(Math.floorDiv(cageSize, 2),1,Math.floorDiv(cageSize, 2));
        SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, String.format("Cage generated at %d, %d, %d", cageCenter.getBlockX(), cageCenter.getBlockY(), cageCenter.getBlockZ()));
    }

    public void buildPortal(World w, Location startCorner, PortalOrientation orientation) {
        for (int width = 0; width < 4; width++) {
            for (int height = 0; height < 5; height++) {

                Location current = orientation.getRelative(startCorner, width, height);

                // Logic: If on the "rim" of the 4x5 rectangle, set Obsidian
                if (width == 0 || width == 3 || height == 0 || height == 4) {
                    current.getBlock().setType(Material.OBSIDIAN);
                }
                else
                    current.getBlock().setType(Material.AIR);
            }
        }
        if(w.getEnvironment().equals(World.Environment.NETHER))
            nethersidePortal = startCorner;

        SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, String.format("Portal generated in the %s, at %d, %d, %d.", w.getEnvironment().toString(), startCorner.getBlockX(), startCorner.getBlockY(), startCorner.getBlockZ()));
        orientation.getRelative(startCorner, 1, 1).getBlock().setType(Material.FIRE);
    }
    public void buildPortalWithPlatform(World w, Location start, PortalOrientation orientation, boolean platform) {
        buildPortal(w, start, orientation);

        if(!platform)
            return;

        for (int width = 0; width < 4; width++) {
            for (int depth = -3; depth <= 3; depth++) {
                Location plat = orientation.getRelative(start, width, 0);

                if (orientation == PortalOrientation.X_AXIS) {
                    plat.add(0, 0, depth);
                } else {
                    plat.add(depth, 0, 0);
                }

                plat.getBlock().setType(Material.OBSIDIAN);
            }
        }
    }

    public Location getCageCenter() {return cageCenter;}
    public boolean isReady() {return ready;}
    public Location getNethersidePortal() {return nethersidePortal;}

    public enum PortalOrientation {
        X_AXIS(1, 0),
        Z_AXIS(0, 1);

        private final int xStep;
        private final int zStep;

        PortalOrientation(int xStep, int zStep) {
            this.xStep = xStep;
            this.zStep = zStep;
        }

        // Helper to calculate the location based on width and height offsets
        public Location getRelative(Location start, int width, int height) {
            return start.clone().add(width * xStep, height, width * zStep);
        }
    }
}
