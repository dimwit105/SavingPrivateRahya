package com.zezdathecrystaldragon.savingPrivateRahya.events.player;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.util.GameMath;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class OnBlockBreak implements Listener
{
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        Game game = SavingPrivateRahya.PLUGIN.getGame();
        if(game.isPreGame() || game.getHeat() == null)
            return;
        if(event.getBlock().getWorld().getEnvironment() != World.Environment.NETHER)
            return;

        game.getHeat().incrementHeat();
        int heat = game.getHeat().getHeatValue();
        double ratio = (double) heat / game.getHeat().heatEffectsStarting;
        int particleCount = GameMath.stochasticRounding((Math.pow(ratio, 2) * 3));
        Location loc = event.getBlock().getLocation();
        if(particleCount > 0)
        {
            loc.getWorld().spawnParticle(
                    Particle.FLAME,
                    loc.clone().add(0.5, 0.5, 0.5),
                    particleCount,
                    0.25, 0.25, 0.25,
                    0.02
            );
        }
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if(heat > game.getHeat().heatEffectsStarting && item.getItemMeta() instanceof Damageable meta)
        {
            int damageToDeal = GameMath.stochasticRounding((heat - game.getHeat().heatEffectsStarting) / 100D);
            if(damageToDeal > 0)
            {
                meta.setDamage(meta.getDamage() + damageToDeal);
                item.setItemMeta(meta);
                event.getBlock().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
            }
        }
        if(heat > game.getHeat().heatEffectsStarting * 3 && SavingPrivateRahya.RAND.nextInt(10) == 0) {
            event.setCancelled(true);
            event.getBlock().setType(Material.LAVA);}
    }
}
