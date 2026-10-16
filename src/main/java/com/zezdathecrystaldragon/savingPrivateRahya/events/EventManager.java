package com.zezdathecrystaldragon.savingPrivateRahya.events;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.events.blocks.OnBeaconDeactivate;
import com.zezdathecrystaldragon.savingPrivateRahya.events.blocks.OnBlockBreak;
import com.zezdathecrystaldragon.savingPrivateRahya.events.blocks.OnReceiveBeaconEffect;
import com.zezdathecrystaldragon.savingPrivateRahya.events.entity.*;
import com.zezdathecrystaldragon.savingPrivateRahya.events.player.*;
import org.bukkit.Bukkit;

public class EventManager
{
    public EventManager()
    {
        SavingPrivateRahya spr = SavingPrivateRahya.PLUGIN;
        Bukkit.getPluginManager().registerEvents(new OnPlayersConnect(), spr);
        Bukkit.getPluginManager().registerEvents(new OnPlayersDeath(), spr);
        Bukkit.getPluginManager().registerEvents(new OnPlayersDisconnect(), spr);
        Bukkit.getPluginManager().registerEvents(new OnPlayersRespawn(), spr);
        Bukkit.getPluginManager().registerEvents(new OnPortalCreated(), spr);
        Bukkit.getPluginManager().registerEvents(new OnPlayerPortal(), spr);
        Bukkit.getPluginManager().registerEvents(new OnPlayerDamaged(), spr);
        Bukkit.getPluginManager().registerEvents(new OnBlockBreak(), spr);
        Bukkit.getPluginManager().registerEvents(new OnPlayerInteract(), spr);
        Bukkit.getPluginManager().registerEvents(new OnMobSpawn(), spr);
        Bukkit.getPluginManager().registerEvents(new OnMobPreSpawn(), spr);
        Bukkit.getPluginManager().registerEvents(new OnProjectileLaunch(), spr);
        Bukkit.getPluginManager().registerEvents(new OnProjectileImpact(), spr);
        Bukkit.getPluginManager().registerEvents(new OnEntityAttacked(), spr);
        Bukkit.getPluginManager().registerEvents(new OnEntityDeath(), spr);
        Bukkit.getPluginManager().registerEvents(new OnEntityTarget(), spr);
        Bukkit.getPluginManager().registerEvents(new OnBeaconDeactivate(), spr);
        Bukkit.getPluginManager().registerEvents(new OnReceiveBeaconEffect(), spr);
        Bukkit.getPluginManager().registerEvents(new OnEntityDespawn(), spr);
    }
}
