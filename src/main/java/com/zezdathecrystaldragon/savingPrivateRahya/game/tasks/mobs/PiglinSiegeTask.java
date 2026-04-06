package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs;


import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class PiglinSiegeTask extends MobTask {

    private final float moveSpeed = 0.8F;

    private Location spiralCenter = null;
    private int spiralStep = 0;

    private Location currentStep = null;

    // Anti-stuck
    private Location lastLoc = null;
    private int stuckTicks = 0;

    // Stair slope controller
    private int stairProgress = 0;

    public PiglinSiegeTask(Game game) {
        super(game, EntityType.PIGLIN, game.nether);
        ItemStack pickaxe = ItemStack.of(Material.GOLDEN_PICKAXE);
        if(mob instanceof Ageable age)
        {
            age.setAdult();
        }
        mob.getEquipment().setItemInMainHand(pickaxe);
        mob.getEquipment().setItemInOffHand(ItemStack.of(Material.NETHERRACK));
        mob.getEquipment().setDropChance(EquipmentSlot.HAND, 1.0F);
        mob.getEquipment().setDropChance(EquipmentSlot.OFF_HAND, 1.0F);
        mob.setRemoveWhenFarAway(false);
        mob.setPersistent(true);
    }

    @Override
    public void run() {
        super.run();
        Location mLoc = mob.getLocation();
        Location tLoc = target.getLocation();

        double distSq = mLoc.distanceSquared(tLoc);

        if (distSq < 9) {
            resetSiegeState();
            return;
        }

        // Stuck detection
        if (lastLoc != null && lastLoc.distanceSquared(mLoc) < 0.001) {
            stuckTicks++;
        } else {
            stuckTicks = 0;
        }

        lastLoc = mLoc.clone();

        if (stuckTicks < 20 && hasValidPath()) {
            resetSiegeState();
            return;
        }

        double yDiff = tLoc.getY() - mLoc.getY();
        double flatDistSq =
                Math.pow(mLoc.getX() - tLoc.getX(), 2) +
                        Math.pow(mLoc.getZ() - tLoc.getZ(), 2);

        if (Math.abs(yDiff) > 3 && flatDistSq < 25) {
            executeSpiral(yDiff > 0);
            return;
        }

        resetSpiral();
        executeHorizontalSiege(tLoc);
    }



    private void resetSpiral() {
        spiralCenter = null;
        spiralStep = 0;
    }

    private void resetSiegeState() {
        resetSpiral();
        stairProgress = 0;
    }

    private void executeHorizontalSiege(Location goal) {

        if (currentStep != null &&
                mob.getLocation().distanceSquared(currentStep) > 0.8) {

            mob.getPathfinder().moveTo(currentStep, moveSpeed);
            return;
        }

        Location mLoc =
                mob.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

        Location goalBlock =
                goal.getBlock().getLocation().add(0.5, 0, 0.5);

        Vector dir =
                goalBlock.toVector().subtract(mLoc.toVector());

        Vector horizontalDir =
                dir.clone().setY(0).normalize();

        double yDiff = goalBlock.getY() - mLoc.getY();

        int yStep = 0;

        if (Math.abs(yDiff) > 1.5) {

            stairProgress++;

            if (stairProgress >= 3) {
                yStep = yDiff > 0 ? 1 : -1;
                stairProgress = 0;
            }
        }

        currentStep =
                mLoc.clone()
                        .add(horizontalDir.multiply(1))
                        .add(0, yStep, 0);

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {

                Location col =
                        currentStep.clone().add(x, 0, z);

                Block floor =
                        col.clone().subtract(0, 1, 0).getBlock();

                if (!floor.getType().isSolid()) {
                    floor.setType(Material.NETHERRACK);
                }

                for (int y = 0; y < 4; y++) {

                    Block b =
                            col.clone().add(0, y, 0).getBlock();

                    if (b.getLocation()
                            .distanceSquared(mLoc) < 0.5)
                        continue;

                    if (b.getType().isSolid() || b.isLiquid()) {
                        b.setType(Material.AIR);
                    }
                }
            }
        }

        mob.getPathfinder().moveTo(currentStep, moveSpeed);

        mob.swingMainHand();

        currentStep.getWorld().playSound(
                currentStep,
                Sound.BLOCK_NETHERRACK_BREAK,
                0.5f,
                0.8f
        );
    }

    private void executeSpiral(boolean up) {
        if (spiralCenter == null) {
            // Center on the block to keep the pattern grid-aligned
            spiralCenter = mob.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);
        }

        List<Vector> spiral = List.of(
                new Vector(1, 0, 1),   // 0
                new Vector(0, 0, 1),   // 1
                new Vector(-1, 1, 1),  // 2 (Rise)
                new Vector(-1, 1, 0),  // 3
                new Vector(-1, 2, -1), // 4 (Rise)
                new Vector(0, 2, -1),  // 5
                new Vector(1, 3, -1),  // 6 (Rise)
                new Vector(1, 3, 0)    // 7
        );

        // 1. Calculate Lap and Index
        int index = spiralStep % spiral.size();
        int lap = spiralStep / spiral.size();

        Vector offset = spiral.get(index).clone();

        // Add 4 blocks of height for every completed lap
        double totalY = up ? (offset.getY() + (lap * 4)) : -(offset.getY() + (lap * 4));

        Location step = spiralCenter.clone().add(offset.getX(), totalY, offset.getZ());

        // 2. Surgical Excavation
        // Build floor under the target
        Block floor = step.clone().subtract(0, 1, 0).getBlock();
        if (!floor.getType().isSolid()) floor.setType(Material.NETHERRACK);

        // Clear exactly 3 blocks of air above the target step
        for (int y = 0; y < 3; y++) {
            Block b = step.clone().add(0, y, 0).getBlock();
            if (b.getType().isSolid() || b.isLiquid()) b.setType(Material.AIR);
            mob.swingMainHand();
            step.getWorld().playSound(step, Sound.BLOCK_NETHERRACK_BREAK, 0.5f, 1.2f);
        }

        // 3. Headroom for the "Step Up"
        // If this step is higher than the mob's current Y,
        // we MUST clear air above the mob's CURRENT head.
        if (step.getY() > mob.getLocation().getY()) {
            Block ceilingFix = mob.getLocation().add(0, 2, 0).getBlock();
            if (ceilingFix.getType().isSolid()) ceilingFix.setType(Material.AIR);
            mob.swingMainHand();
            step.getWorld().playSound(step, Sound.BLOCK_NETHERRACK_BREAK, 0.5f, 1.2f);

        }

        // 4. Central Pillar (Optional: keeps the core solid)
        Block pillar = spiralCenter.clone().add(0, totalY - 1, 0).getBlock();
        if (!pillar.getType().isSolid()) pillar.setType(Material.NETHERRACK);

        // 5. Movement & Progress
        currentStep = step;
        mob.getPathfinder().moveTo(currentStep, moveSpeed);

        // Check if we reached the step to increment
        if (mob.getLocation().distanceSquared(step) < 0.6) {
            spiralStep++;

        }
    }
}