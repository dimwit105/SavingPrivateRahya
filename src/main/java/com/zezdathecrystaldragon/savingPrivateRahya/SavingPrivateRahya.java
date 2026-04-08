package com.zezdathecrystaldragon.savingPrivateRahya;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import com.zezdathecrystaldragon.savingPrivateRahya.events.EventManager;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.game.GameEndReason;
import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.SpawnLocation;
import com.zezdathecrystaldragon.savingPrivateRahya.util.FourthChanceHook;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public final class SavingPrivateRahya extends JavaPlugin
{
    private Game game;
    public static SavingPrivateRahya PLUGIN;
    public final NamespacedKey GAME_INDEX_KEY = new NamespacedKey(this, "last_game_index");
    public final NamespacedKey VIP_MOB = new NamespacedKey(this, "vip_mob");
    public static FourthChanceHook FOURTH_CHANCE = null;
    public static Random RAND = new Random();
    private FoliaLib foliaLib;

    @Override
    public void onEnable()
    {
        PLUGIN = this;
        foliaLib = new FoliaLib(this);
        game = new Game();
        new EventManager();
        if (getServer().getPluginManager().isPluginEnabled("FourthChance")) {
            FOURTH_CHANCE = new FourthChanceHook();
        }
    }

    @Override
    public void onDisable()
    {
        for(Participant part : game.getParticipants().values())
        {
            part.cleanupParticipant();
        }
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
            Bukkit.broadcast(Component.text(playerName + " is the new VIP!"));
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("choosespawn"))
        {
            if(sender instanceof Player p)
            {
                String spawn = args[0];
                Participant part = game.getParticipants().get(p.getUniqueId());
                try
                {
                    SpawnLocation where = SpawnLocation.valueOf(spawn);
                    Bukkit.broadcast(Component.text(String.format("%s has chose to spawn in the %s", sender.getName(), spawn.toLowerCase())));
                    return part.electSpawn(where);
                }
                catch (IllegalArgumentException e)
                {
                    sender.sendMessage("Thats not a valid place to spawn doofus. Try NETHER or OVERWORLD");
                    return false;
                }
            }
            else
                return false;
        }
        if(cmd.getName().equals("testsiege") && sender instanceof Player player)
        {
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("snowmanjoke")) {
            Player player = (Player) sender;

            player.getNearbyEntities(50, 50, 50).stream()
                    .filter(e -> e instanceof Snowman)
                    .map(e -> (Snowman) e)
                    .forEach(s -> {
                        s.setTarget(player);
                        player.sendMessage("Sentry " + s.getEntityId() + " is now tracking you.");
                    });
        }
        if(cmd.getName().equals("glowme") && sender instanceof Player player)
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300,0));
            player.sendMessage("Your friends can see you now!");
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
        if(cmd.getName().equalsIgnoreCase("choosespawn"))
        {
            return List.of("NETHER", "OVERWORLD");
        }
        if(cmd.getName().equalsIgnoreCase("svp"))
        {
            String firstArg = args[0].toLowerCase();
            List<String> subCommands = new ArrayList<>(List.of("start", "reset", "cancel"));
            return StringUtil.copyPartialMatches(args[0], subCommands, new ArrayList<>());
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
