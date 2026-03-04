package com.zezdathecrystaldragon.savingPrivateRahya.players;

import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.players.tasks.ParticipantTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.tasks.RespawningParticipant;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class Participant
{
    protected Game game;
    final UUID playerID;
    ArrayList<ParticipantTask> tasks = new ArrayList<ParticipantTask>();
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
            return;
        if(respawning)
        {
            respawning = false;
            getPlayer().setGameMode(GameMode.SPECTATOR);
            getPlayer().teleportAsync(game.getVip().getPlayer().getLocation());
            addTask(new RespawningParticipant(this));
        }
        if(eliminated)
        {
            getPlayer().setGameMode(GameMode.SPECTATOR);
            getPlayer().teleportAsync(eliminationLocation);
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
        getPlayer().setHealth(getPlayer().getAttribute(Attribute.MAX_HEALTH).getValue());
        getPlayer().clearActivePotionEffects();
        switch (spawnLocation)
        {
            case NETHER -> {
                getPlayer().teleportAsync(game.wm.getCageCenter()).thenAccept(tpd -> {
                    if(tpd)
                    {
                        getPlayer().getInventory().clear();
                        giveStartingGear();
                    }
                });

            }
            case OVERWORLD -> {
                getPlayer().teleportAsync(game.overworld.getSpawnLocation()).thenAccept(tpd -> {
                    getPlayer().getInventory().clear();
                });
            }
        }
    }
    protected void giveStartingGear()
    {
        ItemStack startingPickaxe = new ItemStack(Material.COPPER_PICKAXE);
        startingPickaxe.addEnchantment(Enchantment.UNBREAKING, 1);
        getPlayer().getInventory().addItem(startingPickaxe);
        getPlayer().getInventory().addItem(ItemStack.of(Material.GOLDEN_CARROT, 4));
        getPlayer().getInventory().addItem(ItemStack.of(Material.NETHERRACK, 32));
        getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0));
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
        Iterator<ParticipantTask> iter = tasks.iterator();
        while(iter.hasNext())
        {
            ParticipantTask task = iter.next();
            task.cancel();
        }
    }
    public boolean isEliminated() {return eliminated;}
    public UUID getID() { return playerID;}

}
