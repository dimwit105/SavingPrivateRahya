package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf.abilities;

import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf.Enderwolf;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class LavaSave extends AbstractAbility
{
    public LavaSave(Enderwolf wolf, int cooldown) {
        super(wolf, cooldown);
    }

    @EventHandler
    public void onLavaDamage(EntityDamageEvent event)
    {
        if(event.getCause() != EntityDamageEvent.DamageCause.LAVA)
            return;
        if(!isReady())
            return;

        Wolf eWolf = enderwolf.getWolf();
        if(event.getEntity() instanceof Player player && player.getHealth() < 8D)
        {
            Location tele = buildPlatform(player);
            if(tele == null)
                return;
            enderwolf.tempOwner(player);
            eWolf.teleport(tele);
            player.teleport(tele);
            //eWolf.addPassenger(player);
            player.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(30*20, 0));
            eWolf.addPotionEffect(PotionEffectType.FIRE_RESISTANCE.createEffect(30*20, 0));

            enterCooldown();
        }
    }

    private Location buildPlatform(Player player)
    {
        Location lavaSurface = findLavaSurface(player);
        if(lavaSurface == null)
            return null;
        for(int x = -2; x < 2; x++)
        {
            for(int z = -2; z < 2; z++)
            {
                lavaSurface.getWorld().getBlockAt(lavaSurface.getBlockX() + x, lavaSurface.getBlockY(), lavaSurface.getBlockZ() + z).setType(Material.CRYING_OBSIDIAN);
            }
        }
        return lavaSurface.clone().add(0,1,0);
    }
    private Location findLavaSurface(Player player)
    {
        Location pLoc = player.getLocation();
        for(int y = pLoc.getBlockY(); y < pLoc.getBlockY() + 32; y++)
        {
            Block block = player.getWorld().getBlockAt(pLoc.getBlockX(), y, pLoc.getBlockZ());
            if(block.isSolid() || block.getType() == Material.LAVA)
                continue;
            return new Location(pLoc.getWorld(), pLoc.getBlockX(), y, pLoc.getBlockZ());
        }
        return null;
    }
}
