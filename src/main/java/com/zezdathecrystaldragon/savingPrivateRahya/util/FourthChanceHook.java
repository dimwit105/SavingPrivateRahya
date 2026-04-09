package com.zezdathecrystaldragon.savingPrivateRahya.util;

import com.zezdathecrystaldragon.fourthChance.FourthChance;
import com.zezdathecrystaldragon.fourthChance.downedplayer.DownedPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class FourthChanceHook
{
    private DownedPlayerManager downedPlayerManager;
    public FourthChanceHook()
    {
        grabService();
    }
    private void grabService()
    {
        RegisteredServiceProvider<DownedPlayerManager> rsp =
                Bukkit.getServer().getServicesManager().getRegistration(DownedPlayerManager.class);
        if (rsp != null) {
            this.downedPlayerManager = rsp.getProvider();
        }
    }
    public boolean isDowned(Player p)
    {
        if(downedPlayerManager == null)
            grabService();
        return downedPlayerManager.isDowned(p);
    }

}
