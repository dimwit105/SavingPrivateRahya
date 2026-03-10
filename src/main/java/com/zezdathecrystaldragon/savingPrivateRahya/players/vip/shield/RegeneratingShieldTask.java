package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.shield;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.players.util.VIPTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import com.zezdathecrystaldragon.savingPrivateRahya.util.GameMath;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.logging.Level;

public class RegeneratingShieldTask extends VIPTask
{
    public static final NamespacedKey VIP_ABSORPTION = new NamespacedKey(SavingPrivateRahya.PLUGIN, "vip_absorption");

    private final VeryImportantParticipant vip;
    private AttributeModifier absorptionModifier;

    private final double BASE_DELAY = 60D;
    private final double BASE_RECHARGE_QUANTITY = 0.2D;
    private final double BASE_CAPACITY = 4D;
    private final HashMap<String, ShieldModifier> modifiers = new HashMap<>();
    private int tickCounter = 0;
    private int effectiveDelay;
    private double effectiveCapacity;
    private double effectiveRechargeQuantity;
    private boolean playSound = false;

    public RegeneratingShieldTask(VeryImportantParticipant participant) {
        super(participant);
        this.vip = participant;
        vip.addTask(this);

        effectiveDelay = (int) Math.max(1, calculateStat(BASE_DELAY, ShieldModifier.Target.RECHARGE_DELAY));
        effectiveCapacity = calculateStat(BASE_CAPACITY, ShieldModifier.Target.SHIELD_CAPACITY);
        effectiveRechargeQuantity = calculateStat(BASE_RECHARGE_QUANTITY, ShieldModifier.Target.RECHARGE_QUANTITY);
        tickCounter = effectiveDelay;

        setMaxAbsorption(effectiveCapacity);
    }

    @Override
    public void cancel()
    {
        super.cancel();
        if(absorptionModifier != null)
            vip.getPlayer().getAttribute(Attribute.MAX_ABSORPTION).removeModifier(absorptionModifier);
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
        if(!playSound)
        {
            playSound = true;
            player.getWorld().playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
        }
        if (effectiveRechargeQuantity <= 0) return;

        int amountToAdd = GameMath.stochasticRounding(effectiveRechargeQuantity);
        double currentShield = player.getAbsorptionAmount();
        player.setAbsorptionAmount(Math.min(effectiveCapacity, currentShield + amountToAdd));

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

    public void takeHit()
    {
        tickCounter = 0;
        playSound = false;
    }
    public void setMaxAbsorption(double amount)
    {
        if(absorptionModifier != null && absorptionModifier.getAmount() == amount)
            return;

        AttributeModifier newMod = new AttributeModifier(VIP_ABSORPTION, amount, AttributeModifier.Operation.ADD_NUMBER);
        if(absorptionModifier != null)
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
