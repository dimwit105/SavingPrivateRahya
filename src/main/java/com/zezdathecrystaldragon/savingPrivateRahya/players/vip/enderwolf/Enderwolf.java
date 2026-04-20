package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf.abilities.*;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(vip.getPlayer().orElseThrow(), cooldownTask, 0, 20);
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
    public Optional<Wolf> getWolf()
    {
        if(respawnTime > 0)
            return Optional.empty();
        return Optional.ofNullable(enderWolf);
    }

    public void wolfDied(EntityDeathEvent event)
    {
        getWolf().ifPresent(wolf ->
        {
            Location deathspot = wolf.getLocation();
            wolf.remove();
            deathspot.getWorld().playSound(deathspot, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            event.setShouldPlayDeathSound(false);
            respawnTime = 90;
            enderWolf = null;
        });
    }

    public void onWolfTarget(EntityTargetEvent event)
    {
        if(event.getTarget() instanceof Player p) {
            event.setCancelled(true);
            getWolf().ifPresent(w -> w.setAngry(false));
        }
    }
    public void respawnWolf()
    {
        if(respawnTime > 0 || enderWolf != null || vip.getPlayer() == null)
            return;
        enderWolf = spawnWolf();
        vip.getPlayer().ifPresent(player -> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1));
    }

    public void tempOwner(Player p)
    {
        if (vip.getPlayer().filter(p::equals).isPresent()) return;

        getWolf().ifPresent(wolf -> {
            wolf.setSitting(false);
            wolf.setOwner(p);
        });

        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runLater(() -> {

            getWolf()
                    .flatMap(wolf -> vip.getPlayer().map(player -> List.of(wolf, player)))
                    .ifPresent(pair -> {
                        Wolf wolf = (Wolf) pair.get(0);
                        Player vipPlayer = (Player) pair.get(1);

                        wolf.setSitting(false);
                        wolf.setOwner(vipPlayer);
                        wolf.teleport(vipPlayer);
                    });

        }, 300);
    }
    public void reOwn()
    {
        if(getWolf().isEmpty())
            return;
        cooldownTask = cooldownTask.copy();
        respawnWolf();
        getWolf()
                .flatMap(wolf -> vip.getPlayer().map(player -> List.of(wolf, player)))
                .ifPresent(pair -> {
                    Wolf wolf = (Wolf) pair.get(0);
                    Player vipPlayer = (Player) pair.get(1);

                    wolf.setSitting(false);
                    wolf.setOwner(vipPlayer);
                });

    }
    public void tickAbilities()
    {
        if(globalCooldown > 0)
            globalCooldown--;
        if(respawnTime > 0)
            respawnTime--;
        respawnWolf();
        for(AbstractAbility ability : abilities)
        {
            ability.decrementCooldown();
        }
    }
    private Wolf spawnWolf()
    {
        var player = vip.getPlayer().orElseThrow();
        Wolf wolf = player.getWorld().spawn(player.getLocation(), Wolf.class, CreatureSpawnEvent.SpawnReason.CUSTOM);
        wolf.getPersistentDataContainer().set(SavingPrivateRahya.PLUGIN.VIP_MOB, PersistentDataType.BOOLEAN, true);
        wolf.getAttribute(Attribute.MAX_HEALTH).addModifier(new AttributeModifier(SavingPrivateRahya.PLUGIN.VIP_MOB, 20D, AttributeModifier.Operation.ADD_NUMBER));
        wolf.getAttribute(Attribute.ARMOR).setBaseValue(10D);
        wolf.getAttribute(Attribute.BURNING_TIME).addModifier(new AttributeModifier(SavingPrivateRahya.PLUGIN.VIP_MOB, -1D, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        wolf.setOwner(player);
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
        getWolf().ifPresent(Entity::remove);
    }
}
