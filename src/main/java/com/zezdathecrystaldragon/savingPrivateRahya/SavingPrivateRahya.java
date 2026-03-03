package com.zezdathecrystaldragon.savingPrivateRahya;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import com.zezdathecrystaldragon.savingPrivateRahya.events.EventManager;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.GameEndReason;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public final class SavingPrivateRahya extends JavaPlugin
{
    public Game game;
    public static SavingPrivateRahya PLUGIN;
    public static Random RAND = new Random();
    private FoliaLib foliaLib;

    @Override
    public void onEnable()
    {
        PLUGIN = this;
        foliaLib = new FoliaLib(this);
        game = new Game();
        new EventManager();
    }

    @Override
    public void onDisable()
    {
        foliaLib.getScheduler().cancelAllTasks();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if(args.length == 0){
            sender.sendMessage("We need some arguments bruv");
            return false;}
        if(cmd.getName().equalsIgnoreCase("svp"))
        {
            String firstArg = args[0].toLowerCase();
            switch (firstArg)
            {
                case "start":
                    game.startGame(sender);
                    return true;
                case "cancel":
                    game.endGame(GameEndReason.CANCELLED);
                    return true;
                case "reset":
                    game = game.newGame();
                    return true;
                default:
                    return false;
            }
        }
        if(cmd.getName().equalsIgnoreCase("setvip"))
        {
            String playerName = args[0];
            Player newVIP = Bukkit.getPlayer(playerName);
            if(newVIP == null) {
                sender.sendMessage("I couldn't find that player");
                return false;}
            game.makeVIP(newVIP);
            sender.sendMessage(playerName + " is the new VIP!");
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
            String name = args[0];
            for(Player player : Bukkit.getOnlinePlayers())
            {
                names.add(player.getName());
            }
            return StringUtil.copyPartialMatches(args[0], names, new ArrayList<>());
        }
        return null;
    }
    public FoliaLib getFoliaLib()
    {
        return foliaLib;
    }
    public Game getGame()
    {
        return game;
    }

    public static void runNextTick(Consumer<WrappedTask> runnable)
    {
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runNextTick(runnable);
    }
}
