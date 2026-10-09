package com.zezdathecrystaldragon.savingPrivateRahya.game.world;

import com.sun.jdi.InvalidTypeException;
import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks.CreateCageTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks.CreateNetherPortalTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks.CreateOverworldPortalTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.world.tasks.WorldTask;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.tags.BiomeTagKeys;
import org.bukkit.*;
import org.bukkit.block.Beacon;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Jukebox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BiomeSearchResult;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class WorldModifier
{
    private static final Material[] STAINED_GLASS_BLOCKS = Arrays.stream(Material.values())
            .filter(m -> m.name().endsWith("_STAINED_GLASS") && !m.name().contains("PANE"))
            .toArray(Material[]::new);
    /**
     * Materials we will absolutely bulldoze without a care in the world.
     */
    public final List<Material> nonBlockers;
    private final int cageSize = 5;
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

        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runTimer(cageSearchTask, 5, 1);

        new CreateOverworldPortalTask(game, this).run();
        new CreateNetherPortalTask(game, this).run();
    }

    public static int getAndIncrementGameIndex(World world) {
        PersistentDataContainer pdc = world.getPersistentDataContainer();

        int currentIndex = pdc.getOrDefault(SavingPrivateRahya.PLUGIN.GAME_INDEX_KEY, PersistentDataType.INTEGER, 0);

        int nextIndex = currentIndex + 1;
        pdc.set(SavingPrivateRahya.PLUGIN.GAME_INDEX_KEY, PersistentDataType.INTEGER, nextIndex);

        return currentIndex;
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
                        block.setType(Material.COPPER_GRATE);
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
        w.getBlockAt(cageCenter.clone().subtract(1,0,0)).setType(Material.JUKEBOX);
        Block tunes = w.getBlockAt(cageCenter.clone().subtract(1,0,0));
        if(tunes instanceof Jukebox jb)
        {
            jb.setRecord(ItemStack.of(Material.MUSIC_DISC_FAR));
        }

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
    public Beacon buildBeaconPyramid(Location loc, int height) {
        if (height < 1) throw new IllegalArgumentException("Height must be more than one!");

        for (int yOffset = 0; yOffset < height; yOffset++) {

            int radius = height - yOffset;

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    // Place the block relative to the center location
                    loc.clone().add(x, yOffset, z).getBlock().setType(getBeaconMaterial());
                }
            }
        }

        loc.clone().add(0, height, 0).getBlock().setType(Material.BEACON);
        loc.clone().add(0, height+1, 0).getBlock().setType(getRandomStainedGlass());
        var beacon = loc.getWorld().getBlockAt(loc.clone().add(0, height, 0)).getState();
        if(beacon instanceof Beacon)
            return (Beacon) beacon;
        else
            return null;
    }
    private Material getBeaconMaterial()
    {
        var roll = SavingPrivateRahya.RAND.nextInt(100);
        if(roll == 0) return Material.DIAMOND_BLOCK;
        if(roll < 33) return Material.IRON_BLOCK;
        if(roll < 50) return Material.GOLD_BLOCK;
        return Material.EMERALD_BLOCK;
    }

    private Material getRandomStainedGlass() {
        return STAINED_GLASS_BLOCKS[SavingPrivateRahya.RAND.nextInt(STAINED_GLASS_BLOCKS.length)];
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
    public static List<Biome> getNonOceanBiomes()
    {
        var biomeRegistry = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME);
        return biomeRegistry.stream()
                .filter(biome -> {
                    TypedKey<Biome> typedKey = TypedKey.create(RegistryKey.BIOME, biome.getKey());
                    boolean isOcean = biomeRegistry.getTag(BiomeTagKeys.IS_OCEAN).contains(typedKey);
                    boolean isBeach = biomeRegistry.getTag(BiomeTagKeys.IS_BEACH).contains(typedKey);
                    boolean isRiver = biomeRegistry.getTag(BiomeTagKeys.IS_RIVER).contains(typedKey);
                    boolean isUnderground = (biome == Biome.LUSH_CAVES || biome == Biome.DRIPSTONE_CAVES || biome == Biome.DEEP_DARK);
                    boolean otherForbidden = biome == Biome.STONY_SHORE;

                    if (isOcean || isBeach || isRiver || isUnderground || otherForbidden) {
                        return false;
                    }
                    return true;
                })
                .toList();
    }
    public static Location filterStartLocation(World overworld, Location toFilter)
    {
        var biomeList = getNonOceanBiomes();
        BiomeSearchResult result = overworld.locateNearestBiome(toFilter, 4000, biomeList.toArray(new Biome[0]));

        if (result != null) {
            SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, String.format("Biome result: %s, Location: %s", result.getBiome().getKey().asString(), result.getLocation()));
            return result.getLocation();
        }
        return null;
    }
}
