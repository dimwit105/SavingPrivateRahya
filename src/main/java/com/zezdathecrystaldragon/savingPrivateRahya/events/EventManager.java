package com.zezdathecrystaldragon.savingPrivateRahya.events;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import org.bukkit.Bukkit;

public class EventManager
{
    public EventManager()
    {
        SavingPrivateRahya spr = SavingPrivateRahya.PLUGIN;
        Bukkit.getPluginManager().registerEvents(new OnPlayersConnect(), spr);

    }
}
