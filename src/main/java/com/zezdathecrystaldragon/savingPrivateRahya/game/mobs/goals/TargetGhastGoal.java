package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Snowman;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.EnumSet;

public class TargetGhastGoal implements Goal<Snowman> {

    private final Plugin plugin;
    private final Snowman snowman;
    private final GoalKey<Snowman> key;
    private Ghast target;

    public TargetGhastGoal(Plugin plugin, Snowman snowman) {
        this.plugin = plugin;
        this.snowman = snowman;
        // Unique key for this goal
        this.key = GoalKey.of(Snowman.class, new NamespacedKey(plugin, "target_ghasts"));
    }

    @Override
    public boolean shouldActivate() {
        // If we already have a valid target in range, no need to search
        if (target != null && target.isValid() &&
                target.getLocation().distanceSquared(snowman.getLocation()) < 16384) {
            return false;
        }

        // Search for the closest Ghast within 128 blocks
        target = snowman.getWorld().getEntitiesByClass(Ghast.class).stream()
                .filter(g -> g.getLocation().distanceSquared(snowman.getLocation()) <= 16384)
                .filter(snowman::hasLineOfSight) // Only target what we can hit
                .min(Comparator.comparingDouble(g -> g.getLocation().distanceSquared(snowman.getLocation())))
                .orElse(null);

        return target != null;
    }

    @Override
    public boolean shouldStayActive() {
        return target != null && target.isValid() &&
                target.getLocation().distanceSquared(snowman.getLocation()) <= 16384;
    }

    @Override
    public void start() {
        snowman.setTarget(target);
    }

    @Override
    public void stop() {
        snowman.setTarget(null);
        target = null;
    }

    @Override
    public void tick() {
        // Ensure the target remains set in the entity's brain
        if (target != null) {
            snowman.setTarget(target);
        }
    }

    @Override
    public @NotNull GoalKey<Snowman> getKey() {
        return key;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        // This marks it as a Targeting goal so it conflicts with other target goals
        return EnumSet.of(GoalType.TARGET);
    }
}