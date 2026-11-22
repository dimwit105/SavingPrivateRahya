package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Snowman;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.logging.Level;

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
        if (target != null && target.isValid() &&
                target.getLocation().distanceSquared(snowman.getLocation()) < 16384) {
            return false;
        }

        target = snowman.getWorld().getEntitiesByClass(Ghast.class).stream()
                .filter(g -> g.getLocation().distanceSquared(snowman.getLocation()) <= 16384)
                .filter(snowman::hasLineOfSight)
                .min(Comparator.comparingDouble(g -> g.getLocation().distanceSquared(snowman.getLocation())))
                .orElse(null);
        if(target != null)
        {
            plugin.getLogger().log(Level.INFO, "Snow golem has a target, should be activating!");
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldStayActive() {
        var shouldBeActive = target != null && target.isValid() &&
                target.getLocation().distanceSquared(snowman.getLocation()) <= 16384 &&
                snowman.hasLineOfSight(target);
        if(shouldBeActive)
        {
            plugin.getLogger().log(Level.INFO, "Staying active!");
            return true;
        }
        return false;
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
        if (target != null) {
            snowman.setTarget(target);
        }
        plugin.getLogger().log(Level.INFO, "Should be targetting " + target.getType());
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