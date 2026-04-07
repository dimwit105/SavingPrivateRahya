package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf.abilities;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf.Enderwolf;
import org.bukkit.Bukkit;

public abstract class AbstractAbility implements EnderwolfAbility
{
    protected Enderwolf enderwolf;
    private int maxCooldown;
    protected int cooldown = 0;
    public AbstractAbility(Enderwolf wolf, int cooldown)
    {
        this.enderwolf = wolf;
        maxCooldown = cooldown;
        Bukkit.getPluginManager().registerEvents(this, SavingPrivateRahya.PLUGIN);
    }
    public void enterCooldown()
    {
        cooldown = maxCooldown;
    }
    public boolean isReady()
    {
        return cooldown <= 0 && enderwolf.getGlobalCooldown() <= 0;
    }
    public void decrementCooldown()
    {
        if(cooldown > 0)
            cooldown--;
    }
}
