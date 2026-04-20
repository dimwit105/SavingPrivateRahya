package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf.abilities;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf.Enderwolf;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class ReviveDowned extends AbstractAbility
{

    public ReviveDowned(Enderwolf wolf, int cooldown) {
        super(wolf, cooldown);
    }

    @EventHandler
    public void onDownedPlayerDamaged(EntityDamageEvent event)
    {
        if(onCooldown())
            return;
        if(!(event.getEntity() instanceof Player player))
            return;
        if(!SavingPrivateRahya.FOURTH_CHANCE.isDowned(player))
            return;
        if (player.getHealth() - event.getFinalDamage() <= 0 && !SavingPrivateRahya.FOURTH_CHANCE.isDowned(player)) {
            return;
        }
        double healthPercentage = player.getHealth() / player.getAttribute(Attribute.MAX_HEALTH).getValue();
        if(SavingPrivateRahya.RAND.nextDouble() > healthPercentage / 2 + 0.81666666F)
            return;

        Wolf ewolf = enderwolf.getWolf().orElseThrow();

        ewolf.teleport(player);
        enderwolf.tempOwner(player);

        AreaEffectCloud aec = player.getWorld().createEntity(player.getLocation(), AreaEffectCloud.class);
        int duration = 60*20;
        float radius = 3F;
        aec.addCustomEffect(PotionEffectType.REGENERATION.createEffect(duration, 0), true);
        aec.setReapplicationDelay(duration * 3);
        aec.setDuration(duration);
        aec.setDurationOnUse(0);
        aec.setRadius(3F);
        aec.setRadiusOnUse(0F);
        aec.setRadiusPerTick(radius / duration);

        aec.spawnAt(player.getLocation());
        enterCooldown();

    }
}
