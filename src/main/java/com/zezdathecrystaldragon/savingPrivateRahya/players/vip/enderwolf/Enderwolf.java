package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf.abilities.*;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.logging.Level;

public class Enderwolf
{
    private VeryImportantParticipant vip;
    private EnderwolfCooldownTask cooldownTask;
    private int respawnTime = 0;
    private int globalCooldown = 0;
    @Nullable
    private Wolf enderWolf;
    private ArrayList<AbstractAbility> abilities = new ArrayList<>();

    public Enderwolf(VeryImportantParticipant vip)
    {
        this.vip = vip;
        cooldownTask = new EnderwolfCooldownTask(vip, this);
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(vip.getPlayer(), cooldownTask, 0, 20);
        enderWolf = spawnWolf();
        populateAbilities(SavingPrivateRahya.FOURTH_CHANCE != null);
    }
    private void populateAbilities(boolean fourthChanceEnabled)
    {
        abilities.add(new LavaSave(this, 300));
        abilities.add(new VerySuspiciousStew(this, 90));
        if(fourthChanceEnabled)
        {
            abilities.add(new ReviveDowned(this, 600));
        }
    }
    @Nullable
    public Wolf getWolf()
    {
        if(respawnTime > 0)
            return null;
        return enderWolf;
    }

    public void wolfDied(EntityDeathEvent event)
    {
        Location deathspot = getWolf().getLocation();
        getWolf().remove();
        deathspot.getWorld().playSound(deathspot, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        event.setShouldPlayDeathSound(false);
        respawnTime = 90;
        enderWolf = null;
    }
    public void respawnWolf()
    {
        if(respawnTime > 0 || enderWolf != null || vip.getPlayer() == null)
            return;
        enderWolf = spawnWolf();
        vip.getPlayer().getWorld().playSound(vip.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }

    public void tempOwner(Player p)
    {
        if(enderWolf == null || p == vip.getPlayer())
            return;
        getWolf().setSitting(false);
        getWolf().setOwner(p);
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runLater(() -> {
            if(getWolf() != null && vip.getPlayer() != null){
                getWolf().setSitting(false);
                getWolf().setOwner(vip.getPlayer());
                getWolf().teleport(vip.getPlayer());
            }

        }, 300);
    }
    public void reOwn()
    {
        if(getWolf() == null)
            return;
        cooldownTask = cooldownTask.copy();
        respawnWolf();
        getWolf().setSitting(false);
        getWolf().setOwner(vip.getPlayer());
    }
    public void tickAbilities()
    {
        if(globalCooldown > 0)
            globalCooldown--;
        if(respawnTime > 0)
            respawnTime--;
        SavingPrivateRahya.PLUGIN.getLogger().log(Level.INFO, String.format("Global cooldown: %d, Respawn time: %d", globalCooldown, respawnTime));
        respawnWolf();
        for(AbstractAbility ability : abilities)
        {
            ability.decrementCooldown();
        }
    }
    private Wolf spawnWolf()
    {
        Wolf wolf = vip.getPlayer().getWorld().spawn(vip.getPlayer().getLocation(), Wolf.class, CreatureSpawnEvent.SpawnReason.CUSTOM);
        wolf.getPersistentDataContainer().set(SavingPrivateRahya.PLUGIN.VIP_MOB, PersistentDataType.BOOLEAN, true);
        wolf.getAttribute(Attribute.MAX_HEALTH).addModifier(new AttributeModifier(SavingPrivateRahya.PLUGIN.VIP_MOB, 20D, AttributeModifier.Operation.ADD_NUMBER));
        wolf.getAttribute(Attribute.ARMOR).setBaseValue(10D);
        wolf.getAttribute(Attribute.BURNING_TIME).addModifier(new AttributeModifier(SavingPrivateRahya.PLUGIN.VIP_MOB, -1D, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        wolf.setOwner(vip.getPlayer());
        wolf.setCollarColor(DyeColor.YELLOW);
        wolf.setVariant(Wolf.Variant.PALE);
        wolf.setAdult();
        return wolf;
    }

    public int getGlobalCooldown()
    {
        return globalCooldown;
    }
    public void enterGlobalCooldown()
    {
        globalCooldown = 30;
    }
    public void cleanupAbilities()
    {
        cooldownTask.cancel();
        for(AbstractAbility ability : abilities)
        {
            ability.remove();
        }
    }
}
