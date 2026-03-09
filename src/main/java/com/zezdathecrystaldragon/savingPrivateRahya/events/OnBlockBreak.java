package com.zezdathecrystaldragon.savingPrivateRahya.events;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.util.GameMath;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class OnBlockBreak implements Listener
{
    Game game = SavingPrivateRahya.PLUGIN.getGame();
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(game.isPreGame() || game.getHeat() == null)
            return;

        game.getHeat().incrementHeat();
        int heat = game.getHeat().getHeatValue();
        ItemStack item = event.getPlayer().getActiveItem();
        if(heat > game.getHeat().heatEffectsStarting && item.getItemMeta() instanceof Damageable meta)
        {
            int damageToDeal = GameMath.stochasticRounding((heat - game.getHeat().heatEffectsStarting) / 100D);
            if(damageToDeal > 0)
            {
                meta.setDamage(meta.getDamage() + damageToDeal);
                event.getBlock().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
            }
        }
        if(heat > game.getHeat().heatEffectsStarting * 3 && SavingPrivateRahya.RAND.nextInt(10) == 0)
            event.getBlock().setType(Material.LAVA);
    }
}
