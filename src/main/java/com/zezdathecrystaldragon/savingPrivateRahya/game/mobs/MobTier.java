package com.zezdathecrystaldragon.savingPrivateRahya.game.mobs;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MobTier
{
    private final List<EntityType> stack;
    private final int minSegment;
    private final List<MobBehavior> behaviors;

    public record MobBehavior(int minSegment, Consumer<LivingEntity> action) {
        public static <T extends LivingEntity> MobBehavior of(int minSegment, Class<T> clazz, Consumer<T> logic) {
            return new MobBehavior(minSegment, ent -> {
                if (clazz.isInstance(ent)) {
                    logic.accept(clazz.cast(ent));
                }
            });
        }
    }
    public MobTier(EntityType stack, int minSegment, List<MobBehavior> behaviors) {
        this(List.of(stack), minSegment, behaviors);
    }

    public MobTier(List<EntityType> stack, int minSegment, List<MobBehavior> behaviors) {
        this.stack = stack;
        this.minSegment = minSegment;
        this.behaviors = behaviors;
    }
    public MobTier(List<EntityType> stack, int minSegment) {
        this(stack, minSegment, List.of());
    }

    public MobTier(EntityType type, int minSegment) {
        this(List.of(type), minSegment);
    }

    public List<Entity> spawn(Location loc, int currentSegments) {
        ArrayList<Entity> spawned = new ArrayList<>();
        Entity last = null;
        for (EntityType type : stack) {
            Entity current = loc.getWorld().spawnEntity(loc, type);
            spawned.add(current);

            current.getPersistentDataContainer().set(MobManager.CUSTOM, PersistentDataType.BYTE, (byte) 1);

            if (current instanceof LivingEntity living) {
                behaviors.stream()
                        .filter(b ->  b.minSegment() >= currentSegments)
                        .forEach(b -> b.action().accept(living));
                living.getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(64D);
            }

            if (last != null) last.addPassenger(current);
            last = current;
        }
        return spawned;
    }


    public int getMinSegment() { return minSegment; }
    public List<EntityType> getStack() {return stack;}
}