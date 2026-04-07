package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.GameState;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs.MobTask;
import com.zezdathecrystaldragon.savingPrivateRahya.game.tasks.mobs.PiglinSiegeTask;
import com.zezdathecrystaldragon.savingPrivateRahya.util.GameMath;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class MobManager
{
    public static final NamespacedKey CUSTOM = new NamespacedKey(SavingPrivateRahya.PLUGIN, "custom");
    public static final NamespacedKey TRACKING = new NamespacedKey(SavingPrivateRahya.PLUGIN, "tracking");

    public final List<MobTier> bonusMobs = List.of(
            new MobTier(EntityType.BREEZE, 2, List.of(MobTier.MobBehavior.of(-2, Breeze.class, breeze -> {
                breeze.getPersistentDataContainer().set(TRACKING, PersistentDataType.BOOLEAN, true);
            }))),
            new MobTier(EntityType.WITHER_SKELETON, 1, List.of(MobTier.MobBehavior.of(1, WitherSkeleton.class, ws -> {
                if(SavingPrivateRahya.RAND.nextBoolean())
                    ws.getEquipment().setItemInMainHand(ItemStack.of(Material.BOW));
            }))),
            new MobTier(EntityType.BLAZE, 1),
            new MobTier(EntityType.PIGLIN_BRUTE, 1),
            new MobTier(List.of(EntityType.SPIDER, EntityType.WITHER_SKELETON), 0, List.of(MobTier.MobBehavior.of(0, WitherSkeleton.class, ws -> {
                if(SavingPrivateRahya.RAND.nextBoolean())
                    ws.getEquipment().setItemInMainHand(ItemStack.of(Material.BOW));
            }))),
            new MobTier(EntityType.PILLAGER, 0),
            new MobTier(EntityType.WITCH, 0),
            new MobTier(EntityType.GHAST, 0, List.of(MobTier.MobBehavior.of(-2, Ghast.class, ghast -> {
                ghast.getPersistentDataContainer().set(TRACKING, PersistentDataType.BOOLEAN, true);
            }))),
            new MobTier(EntityType.VINDICATOR, -1),

            new MobTier(EntityType.EVOKER, -3),
            new MobTier(List.of(EntityType.CAVE_SPIDER, EntityType.CREEPER), -3),
            new MobTier(EntityType.RAVAGER, -4),
            new MobTier(EntityType.WARDEN, -5));


    private ArrayList<MobTask> siegers = new ArrayList<>();
    Game game;
    public MobManager(Game game)
    {
        this.game = game;
    }



    public void onMobTaskCancelled()
    {
        cleanSiegerList();
    }
    private void cleanSiegerList()
    {
        siegers.removeIf(MobTask::isCancelled);
    }

    public boolean rollSpawnSieger()
    {
        if(siegers.size() >= game.getParticipants().size())
            return false;
        int excess = game.getHeat().getHeatValue() - game.getHeat().heatEffectsStarting;
        if(excess <= 0)
            return false;
        double excessCubed = Math.pow(excess, 3);
        double kCubed = Math.pow(game.getHeat().heatEffectsStarting, 3) * GameMath.getHarmonicNumber(game.getParticipants().size());
        double probability = excessCubed / (excessCubed + kCubed);

        if (SavingPrivateRahya.RAND.nextDouble() < probability) {
            PiglinSiegeTask sieger = spawnSieger();
            if (sieger != null && !sieger.isCancelled()) siegers.add(sieger);
            return true;
        }
        return false;
    }
    private PiglinSiegeTask spawnSieger()
    {
        Bukkit.broadcast(Component.text("You hear the distant sound of blocks breaking"));
        return new PiglinSiegeTask(game);
    }
    public void handleRandomSpawns(EntitySpawnEvent event)
    {
        if(event.getEntity().getPersistentDataContainer().has(CUSTOM))
            return;
        if (game.getState() != GameState.IN_PROGRESS) return;

        int segmentsRemaining = game.getTime().getSegmentsRemaining();
        if(SavingPrivateRahya.RAND.nextInt(Math.max(1, 7 + segmentsRemaining)) == 0)
        {
            List<MobTier> eligible = bonusMobs.stream()
                    .filter(tier -> tier.getMinSegment() >= segmentsRemaining)
                    .toList();

            if (eligible.isEmpty()) return;
            event.setCancelled(true);
            MobTier chosen = eligible.get(SavingPrivateRahya.RAND.nextInt(eligible.size()));
            chosen.spawn(event.getLocation(), segmentsRemaining);
        }
        if(event.getEntity() instanceof Strider strider && SavingPrivateRahya.RAND.nextInt(3) == 0)
        {
            bonusMobs.stream()
                    .filter(mt -> mt.getStack().size() == 1
                            && mt.getStack().contains(EntityType.WITHER_SKELETON)
                            && mt.getMinSegment() >= segmentsRemaining) // Use >= for countdowns
                    .findAny()
                    .ifPresent(rider -> {
                        WitherSkeleton spawnedRider = (WitherSkeleton) rider.spawn(event.getLocation(), segmentsRemaining).get(0);
                        spawnedRider.getEquipment().setItemInOffHand(ItemStack.of(Material.WARPED_FUNGUS_ON_A_STICK));
                        spawnedRider.getEquipment().setDropChance(EquipmentSlot.OFF_HAND, 1F);
                        strider.setSaddle(true);
                        strider.addPassenger(spawnedRider);
                    });

        }

    }

}
