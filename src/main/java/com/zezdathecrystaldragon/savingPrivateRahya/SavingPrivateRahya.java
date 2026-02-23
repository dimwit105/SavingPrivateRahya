package com.zezdathecrystaldragon.savingPrivateRahya;

import com.tcoded.folialib.FoliaLib;
import com.zezdathecrystaldragon.savingPrivateRahya.events.OnPlayersConnect;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class SavingPrivateRahya extends JavaPlugin
{
    public static Game GAME;
    public static SavingPrivateRahya PLUGIN;
    public static Random RAND;
    private FoliaLib foliaLib;

    @Override
    public void onEnable()
    {
        Bukkit.getPluginManager().registerEvents(new OnPlayersConnect(), this);
        GAME = new Game();
        PLUGIN = this;
        RAND = new Random();
        foliaLib = new FoliaLib(this);
    }

    @Override
    public void onDisable()
    {
        foliaLib.getScheduler().cancelAllTasks();
    }
    public FoliaLib getFoliaLib()
    {
        return foliaLib;
    }
}
