package com.zezdathecrystaldragon.savingPrivateRahya.players;

import com.zezdathecrystaldragon.savingPrivateRahya.game.GameEndReason;
import org.bukkit.World;
import org.bukkit.event.entity.PlayerDeathEvent;

public class VeryImportantParticipant extends Participant
{
    VeryImportantParticipant(Participant p)
    {
        super(p);
        this.spawnLocation = SpawnLocation.NETHER;
    }

    /**
     * This will only succeed if you specify Nether, but their Spawn Location is already Nether, so there is little point in reaffirming it.
     * @param location Either Nether for VIP spawning, or Overworld for regular spawning.
     * @return Whether setting was successful. VIPs can only spawn in the nether, and may not have an overworld spawn.
     */
    public boolean electSpawn(SpawnLocation location)
    {
        if(location == SpawnLocation.NETHER)
            return super.electSpawn(location);
        return false;
    }

    public void onDeath(PlayerDeathEvent event)
    {
        eliminate();
        game.endGame(GameEndReason.VIP_DIED);
    }

    @Override
    public VeryImportantParticipant toVIP() {return this;}

    public Participant unVIP()
    {
        return new Participant(this);
    }
}
