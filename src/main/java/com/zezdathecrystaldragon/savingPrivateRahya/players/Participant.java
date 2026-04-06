package com.zezdathecrystaldragon.savingPrivateRahya.players;

import com.zezdathecrystaldragon.savingPrivateRahya.players.tasks.EliminatedParticipant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.players.util.ParticipantTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.tasks.RespawningParticipant;
import com.zezdathecrystaldragon.savingPrivateRahya.util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Participant
{
    protected Game game;
    final UUID playerID;
    protected ArrayList<ParticipantTask> tasks = new ArrayList<ParticipantTask>();
    protected SpawnLocation spawnLocation;
    boolean eliminated = false;
    Location eliminationLocation;
    boolean respawning = false;
    public VeryImportantParticipant toVIP()
    {
        return new VeryImportantParticipant(this);
    }
    public Participant(UUID playerID, Game game)
    {
        this.playerID = playerID;
        this.game = game;
        spawnLocation = SpawnLocation.OVERWORLD;
    }

    public Participant(Participant other)
    {
        this.playerID = other.playerID;
        this.spawnLocation = other.spawnLocation;
        this.eliminated = other.eliminated;
        this.eliminationLocation = other.eliminationLocation;
        this.respawning = other.respawning;
        this.game = other.game;
        this.tasks = new ArrayList<>(other.tasks);
    }

    /**
     * Set where this participant should spawn. NETHER spawns with the VIP, OVERWORLD spawns with everyone else.
     * @param location Either Nether for VIP spawning, or Overworld for regular spawning.
     * @return Whether setting was successful. VIPs can only spawn in the nether, and can not have an overworld spawn.
     */
    public boolean electSpawn(SpawnLocation location)
    {
        spawnLocation = location;
        return true;
    }

    /**
     * Marks the player as eliminated, and will respawn them on their elimination location when they respawn. Note, this does not kill the player itself,
     * it merely marks them as out of the game, and spectators them when they respawn.
     */
    public void eliminate()
    {
        eliminated = true;
        eliminationLocation = getPlayer().getLocation();
        addTask(new EliminatedParticipant(this));
    }
    public void onDeath(PlayerDeathEvent event)
    {
        if(event.getPlayer().getWorld().getEnvironment() == World.Environment.NORMAL)
        {
            respawning = true;
        }
        else
            eliminate();
    }
    public void onRespawn(PlayerRespawnEvent event)
    {
        if(game.isPreGame() || game.getVip() == null)
        {
            event.setRespawnLocation(game.gameCenter);
        }
        if(respawning)
        {
            respawning = false;
            getPlayer().setGameMode(GameMode.SPECTATOR);
            event.setRespawnLocation(game.getVip().getPlayer().getLocation());
            addTask(new RespawningParticipant(this));
        }
        if(eliminated)
        {
            getPlayer().setGameMode(GameMode.SPECTATOR);
            event.setRespawnLocation(eliminationLocation);
        }
    }
    public void onDisconnect(PlayerQuitEvent event)
    {
        cancelAllTasks();
    }

    /**
     * Adds a ParticipantTask to the participant. This will manage them in a list and stop them when needed.
     * All ParticipantTask's added this way will run once every 20 ticks, starting immediately.
     * @param task the task to add.
     */
    public void addTask(ParticipantTask task)
    {
        tasks.add(task);
        SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(getPlayer(), task, 0, 20);
    }
    public void beginGame()
    {
        for(var modifiers : getPlayer().getAttribute(Attribute.MAX_HEALTH).getModifiers())
        {
            getPlayer().getAttribute(Attribute.MAX_HEALTH).removeModifier(modifiers);
        }
        getPlayer().setHealth(getPlayer().getAttribute(Attribute.MAX_HEALTH).getValue());
        getPlayer().setFireTicks(0);
        getPlayer().setFoodLevel(20);
        getPlayer().setSaturation(5);
        getPlayer().clearActivePotionEffects();
        switch (spawnLocation)
        {
            case NETHER -> {
                getPlayer().teleportAsync(game.wm.getCageCenter()).thenAccept(tpd -> {
                    getPlayer().getInventory().clear();
                    giveStartingGear(spawnLocation);
                });
            }
            case OVERWORLD -> {
                getPlayer().teleportAsync(game.gameCenter).thenAccept(tpd -> {
                    getPlayer().getInventory().clear();
                    giveStartingGear(spawnLocation);
                });
            }
        }
        getPlayer().setGameMode(GameMode.SURVIVAL);
    }
    protected void giveStartingGear(SpawnLocation location)
    {
        if(location == SpawnLocation.NETHER) {
            ItemStack startingPickaxe = new ItemStack(Material.COPPER_PICKAXE);
            startingPickaxe.addEnchantment(Enchantment.UNBREAKING, 1);
            getPlayer().getInventory().addItem(startingPickaxe);
            getPlayer().getInventory().addItem(ItemStack.of(Material.GOLDEN_CARROT, 4));
            getPlayer().getInventory().addItem(ItemStack.of(Material.NETHERRACK, 32));
            getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0));
        }
        ItemUtil.giveNethersideCompass(this);
    }

    public Player getPlayer()
    {
        return Bukkit.getPlayer(playerID);
    }

    /**
     * Called when a ParticipantTask has is cancelled. Cleans up the task from the participant.
     * @param task The task that has been cancelled.
     */
    public void taskConcluded(ParticipantTask task)
    {
        tasks.remove(task);
    }
    public void cancelAllTasks()
    {
        for (ParticipantTask task : new ArrayList<>(tasks))
        {
            task.cancel();
        }
        tasks.clear();
    }
    public EliminatedParticipant getEliminatedParticipant()
    {
        if(!eliminated)
            return null;
        return tasks.stream()
                .filter(task -> task instanceof EliminatedParticipant)
                .map(task -> (EliminatedParticipant) task)
                .findFirst()
                .orElse(null);
    }
    public boolean isEliminated() {return eliminated;}
    public UUID getID() { return playerID;}
    public Game getGame() {return game;}

}
