package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.util.CancellableRunnable;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;

import java.util.Comparator;

public class FuckGhastsTasks extends CancellableRunnable
{
    private int ticksRunning = 0;
    private Snowman snowman;
    public FuckGhastsTasks(Snowman snowman)
    {
        this.snowman = snowman;
    }
    @Override
    public void run()
    {
        if(!snowman.isValid())
        {
            cancel();
            return;
        }
        ticksRunning++;
        var ghastsInRange = snowman.getWorld().getEntitiesByClass(Ghast.class).stream()
                .filter(g -> g.isValid() && !g.isDead())
                .filter(g -> g.getLocation().distanceSquared(snowman.getLocation()) <= 16384)
                .filter(snowman::hasLineOfSight)
                .toList();
        if(ghastsInRange.isEmpty())
            return;

        var numGhasts = ghastsInRange.size();
        if( numGhasts > 1)
        {
            launchFireball(ghastsInRange.get(ticksRunning % numGhasts));
        }
        else
            launchFireball(ghastsInRange.getFirst());
    }
    private void launchFireball(LivingEntity target)
    {
        Fireball fireball = snowman.getWorld().createEntity(snowman.getLocation(), Fireball.class);
        fireball.setShooter(snowman);
        fireball.setYield(2F);
        fireball.setIsIncendiary(true);
        fireball.setVelocity(new Vector(0,0.1,0));
        fireball.spawnAt(snowman.getLocation(), CreatureSpawnEvent.SpawnReason.CUSTOM);

        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(fireball, new HeatSeekingFireball(fireball, target, 0.5F), 6, 1);
    }
}
