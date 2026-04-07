package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.util.CancellableRunnable;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class HeatSeekingFireball extends CancellableRunnable
{
    public static HashMap<Entity, HeatSeekingFireball> heatSeekers = new HashMap<>();
    Entity heatSeeker;
    LivingEntity target;
    float turnSensitivity;
    boolean speedKick = false;
    int stunned = 0;

    public HeatSeekingFireball(Entity heatSeeker, LivingEntity target, float turnSensitivity)
    {
        this.heatSeeker = heatSeeker;
        this.target = target;
        this.turnSensitivity = turnSensitivity;
        heatSeekers.put(heatSeeker, this);
    }
    @Override
    public void cancel()
    {
        heatSeekers.remove(heatSeeker);
    }

    @Override
    public void run()
    {
        if(heatSeeker == null || target == null || !heatSeeker.isValid() || heatSeeker.isDead() || !target.isValid() || target.isDead() || heatSeeker.getVelocity().lengthSquared() <= 0)
        {
            this.cancel();
            return;
        }
        if(!speedKick)
        {
            heatSeeker.setVelocity(heatSeeker.getVelocity().multiply(4));
            heatSeeker.getWorld().playSound(heatSeeker.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
            speedKick = true;
            return;
        }
        if(stunned > 0)
        {
            stunned--;
            return;
        }

        Vector currentTrajectory = heatSeeker.getVelocity();
        double currentSpeed = currentTrajectory.length();

        Vector us = heatSeeker.getLocation().toVector();
        Vector them = target.getLocation().toVector();
        Vector directionToThem = them.subtract(us).normalize();
        Vector newVelocity = currentTrajectory.add(directionToThem.multiply(turnSensitivity * currentSpeed));

        heatSeeker.setVelocity(newVelocity.normalize().multiply(currentSpeed));

    }
    public void stun()
    {
        stunned = 21 + SavingPrivateRahya.RAND.nextInt(20);
    }
}
