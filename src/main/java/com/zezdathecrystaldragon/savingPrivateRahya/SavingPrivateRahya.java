package com.zezdathecrystaldragon.savingPrivateRahya;

import com.tcoded.folialib.FoliaLib;
import com.zezdathecrystaldragon.savingPrivateRahya.events.OnPlayersConnect;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.GameEndReason;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if(cmd.getName().equalsIgnoreCase("svp"))
        {
            String firstArg = args[0].toLowerCase();
            switch (firstArg)
            {
                case "start":
                    GAME.startGame(sender);
                    return true;
                case "cancel":
                    GAME.endGame(GameEndReason.CANCELLED);
                    return true;
                default:
                    return false;
            }
        }
        if(cmd.getName().equalsIgnoreCase("setvip"))
        {
            String playerName = args[0];
            Player newVIP = Bukkit.getPlayer(playerName);
            if(newVIP == null)
                return false;
            GAME.setVeryImportantParticipant(GAME.getParticipants().get(newVIP.getUniqueId()));
            return true;
        }
        return false;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
    {
        if(cmd.getName().equalsIgnoreCase("setvip"))
        {
            List<String> names = new ArrayList<String>();
            for(Player player : Bukkit.getOnlinePlayers())
            {
                names.add(player.getName());
            }
            return names;
        }
        return null;
    }
    public FoliaLib getFoliaLib()
    {
        return foliaLib;
    }
}
