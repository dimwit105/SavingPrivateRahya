package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.shield;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.players.tasks.ParticipantTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RegeneratingShieldTask extends ParticipantTask
{
    public static final NamespacedKey VIP_ABSORPTION = new NamespacedKey(SavingPrivateRahya.PLUGIN, "vip_absorption");

    private final VeryImportantParticipant vip;
    private AttributeModifier absorptionModifier;

    private final double BASE_DELAY = 30D;
    private final double BASE_RECHARGE_QUANTITY = 0.3D;
    private final double BASE_CAPACITY = 2D;
    private final HashMap<String, ShieldModifier> modifiers = new HashMap<>();
    private int tickCounter = 0;
    private int effectiveDelay;
    private double effectiveCapacity;
    private double effectiveRechargeQuantity;

    protected RegeneratingShieldTask(VeryImportantParticipant participant) {
        super(participant);
        this.vip = participant;

        effectiveDelay = (int) Math.max(1, calculateStat(BASE_DELAY, ShieldModifier.Target.RECHARGE_DELAY));
        effectiveCapacity = calculateStat(BASE_CAPACITY, ShieldModifier.Target.SHIELD_CAPACITY);
        effectiveRechargeQuantity = calculateStat(BASE_RECHARGE_QUANTITY, ShieldModifier.Target.RECHARGE_QUANTITY);

        setMaxAbsorption(effectiveCapacity);
    }
    private double calculateStat(double base, ShieldModifier.Target target)
    {
        double flat = 0;
        double scalar = 0;
        for(ShieldModifier mod : modifiers.values())
        {
            if(mod.target() != target)
                continue;
            if(mod.op() == ShieldModifier.Operation.ADD_NUMBER)
                flat += mod.value();
            else
                scalar += mod.value();
        }

        return (base + flat) * (1D + scalar);
    }

    @Override
    public void run()
    {
        Player player = vip.getPlayer();
        if(player == null)
            return;
        setMaxAbsorption(effectiveCapacity);
        if(tickCounter < effectiveDelay)
        {
            tickCounter++;
            return;
        }
        if (effectiveRechargeQuantity <= 0) return;

        int amountToAdd = (int) effectiveRechargeQuantity;
        double chance = effectiveRechargeQuantity - amountToAdd;

        if (SavingPrivateRahya.RAND.nextDouble() < chance) {
            amountToAdd++;
        }

        if (amountToAdd > 0) {
            double currentShield = player.getAbsorptionAmount();
            player.setAbsorptionAmount(Math.min(effectiveCapacity, currentShield + amountToAdd));
            tickCounter = 0;
        }

    }

    public void takeHit()
    {
        tickCounter = 0;
    }
    public void setMaxAbsorption(double amount)
    {
        if(vip.getPlayer().getAttribute(Attribute.MAX_ABSORPTION).getValue() == amount)
            return;

        AttributeModifier newMod = new AttributeModifier(VIP_ABSORPTION, amount, AttributeModifier.Operation.ADD_NUMBER);
        vip.getPlayer().getAttribute(Attribute.MAX_ABSORPTION).removeModifier(absorptionModifier);
        vip.getPlayer().getAttribute(Attribute.MAX_ABSORPTION).addModifier(newMod);
        absorptionModifier = newMod;
    }

    public void applyModifier(String id, double val, ShieldModifier.Operation op, ShieldModifier.Target target) {
        modifiers.put(id, new ShieldModifier(id, val, op, target));
        updateCache(target);
    }

    public void removeModifier(String id) {
        ShieldModifier removed = modifiers.remove(id);
        if (removed != null) {
            updateCache(removed.target());
        }
    }

    private void updateCache(ShieldModifier.Target target) {
        switch (target) {
            case RECHARGE_DELAY ->
                    effectiveDelay = (int) Math.max(1, calculateStat(BASE_DELAY, target));

            case RECHARGE_QUANTITY ->
                    effectiveRechargeQuantity = calculateStat(BASE_RECHARGE_QUANTITY, target);

            case SHIELD_CAPACITY -> {
                effectiveCapacity = calculateStat(BASE_CAPACITY, target);
                setMaxAbsorption(effectiveCapacity);
            }
        }
    }
}
