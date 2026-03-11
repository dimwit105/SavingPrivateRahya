package com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.util.CancellableRunnable;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;

import java.util.ArrayList;

public abstract class MobTask extends CancellableRunnable
{
    Mob mob;
    Player target;
    Game game;
    protected MobTask(Game game, EntityType toSpawn, World world)
    {
        this.game = game;
        this.target = getRandomTarget(world);

        if (this.target != null) {
            this.mob = spawnAroundTarget(target, toSpawn);
        }
        if (this.mob == null) {
            this.cancel();
            return;
        }
        SavingPrivateRahya.PLUGIN.getFoliaLib()
                .getScheduler()
                .runAtEntityTimer(mob, this, 0, 3);
    }
    public void cancel()
    {
        super.cancel();
        game.getMobs().onMobTaskCancelled();
    }
    public void run() {
        if (!mob.isValid() || mob.isDead() || !target.isOnline() || target.getGameMode() != GameMode.SURVIVAL || target.isDead() || SavingPrivateRahya.PLUGIN.getGame() != game) {
            this.cancel();
            return;
        }
    }
    protected boolean hasValidPath() {

        var path = mob.getPathfinder().getCurrentPath();

        if (path == null || path.getFinalPoint() == null)
            return false;

        return path.getFinalPoint()
                .distanceSquared(target.getLocation()) < 4;
    }

    protected Player getRandomTarget(World world)
    {
        ArrayList<Player> potentialTargets = new ArrayList<>();
        for(Participant part : game.getParticipants().values())
        {
            if(part.getPlayer() == null || part.getPlayer().getWorld() != world || part.isEliminated())
                continue;
            potentialTargets.add(part.getPlayer());
        }
        if(potentialTargets.isEmpty())
            return null;

        return potentialTargets.get(SavingPrivateRahya.RAND.nextInt(potentialTargets.size()));
    }
    protected Mob spawnAroundTarget(Player target, EntityType toSpawn)
    {
        Location loc = target.getLocation();
        Location spawnLocation = null;
        for(int attempts = 0; attempts < 66; attempts++)
        {
            Location attempt = loc.clone().add(-32 + SavingPrivateRahya.RAND.nextInt(64), 0, -32 + SavingPrivateRahya.RAND.nextInt(64));
            for(int y = Math.min(attempt.getBlockY()+32, 112); y > 32; y--)
            {
                Block block = target.getWorld().getBlockAt(attempt.getBlockX(), y, attempt.getBlockZ());
                if(!block.isSolid())
                    continue;
                if(block.getWorld().getBlockAt(block.getLocation().add(0,1,0)).isSolid() || block.getWorld().getBlockAt(block.getLocation().add(0,2,0)).isSolid())
                    continue;
                spawnLocation = new Location(target.getWorld(), attempt.getBlockX(), y+1, attempt.getBlockZ());
                break;
            }
        }
        if(spawnLocation == null)
            return null;
        return (Mob) target.getWorld().spawnEntity(spawnLocation, toSpawn);
    }
}
