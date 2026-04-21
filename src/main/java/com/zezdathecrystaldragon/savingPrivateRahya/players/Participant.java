package com.zezdathecrystaldragon.savingPrivateRahya.players;

import com.zezdathecrystaldragon.savingPrivateRahya.players.tasks.EliminatedParticipant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.players.util.ParticipantTask;
import com.zezdathecrystaldragon.savingPrivateRahya.players.tasks.RespawningParticipant;
import com.zezdathecrystaldragon.savingPrivateRahya.util.ItemUtil;
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
import org.bukkit.scoreboard.Scoreboard;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Optional;
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
    private boolean restored = false;

    public VeryImportantParticipant toVIP()
    {
        return new VeryImportantParticipant(this);
    }
    public Participant(UUID playerID, Game game)
    {
        this.playerID = playerID;
        this.game = game;
        spawnLocation = SpawnLocation.OVERWORLD;
        handleTeam();
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
        handleTeam();
    }

    /**
     * Set where this participant should spawn. NETHER spawns with the VIP, OVERWORLD spawns with everyone else.
     * @param location Either Nether for VIP spawning, or Overworld for regular spawning.
     * @return Whether setting was successful. VIPs can only spawn in the nether, and can not have an overworld spawn.
     */
    public boolean electSpawn(SpawnLocation location)
    {
        spawnLocation = location;
        handleTeam();
        return true;
    }

    /**
     * Marks the player as eliminated, and will respawn them on their elimination location when they respawn. Note, this does not kill the player itself,
     * it merely marks them as out of the game, and spectators them when they respawn.
     */
    public void eliminate()
    {
        eliminated = true;
        getPlayer().ifPresent(player -> eliminationLocation = player.getLocation());
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
        if(getPlayer().isEmpty())
            return;
        var player = getPlayer().get();
        if(game.isPreGame() || game.getVip().isEmpty() || game.getVip().get().getPlayer().isEmpty())
        {
            event.setRespawnLocation(game.gameCenter);
            return;
        }
        if(respawning)
        {
            respawning = false;
            player.setGameMode(GameMode.SPECTATOR);
            event.setRespawnLocation(game.getVip().get().getPlayer().get().getLocation());
            addTask(new RespawningParticipant(this));
        }
        if(eliminated)
        {
            player.setGameMode(GameMode.SPECTATOR);
            event.setRespawnLocation(eliminationLocation);
        }
    }
    public void onDisconnect(PlayerQuitEvent event)
    {
        cleanupParticipant();
    }

    /**
     * Adds a ParticipantTask to the participant. This will manage them in a list and stop them when needed.
     * All ParticipantTask's added this way will run once every 20 ticks, starting immediately.
     * @param task the task to add.
     */
    public void addTask(ParticipantTask task)
    {
        tasks.add(task);
        getPlayer().ifPresent(player -> {
            SavingPrivateRahya.PLUGIN.getFoliaLib().getScheduler().runAtEntityTimer(player, task, 0, 20);

        });
    }
    public void beginGame()
    {
        if(getPlayer().isEmpty())
            return;

        var player = getPlayer().get();
        player.getAttribute(Attribute.MAX_HEALTH).removeModifier(SavingPrivateRahya.REVIVED_MISSING_HEARTS);
        player.setHealth(player.getAttribute(Attribute.MAX_HEALTH).getValue());
        player.setFireTicks(0);
        player.setFoodLevel(20);
        player.setSaturation(5);
        player.clearActivePotionEffects();
        player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(300,0));
        switch (spawnLocation)
        {
            case NETHER -> {
                player.teleportAsync(game.wm.getCageCenter()).thenAccept(tpd -> {
                    player.getInventory().clear();
                    giveStartingGear(spawnLocation);
                });
            }
            case OVERWORLD -> {
                player.teleportAsync(game.gameCenter).thenAccept(tpd -> {
                    player.getInventory().clear();
                    giveStartingGear(spawnLocation);
                });
            }
        }
        player.setGameMode(GameMode.SURVIVAL);
    }
    protected void giveStartingGear(SpawnLocation location)
    {
        if (getPlayer().isEmpty())
            return;
        var player = getPlayer().get();
        if(location == SpawnLocation.NETHER) {
            ItemStack startingPickaxe = new ItemStack(Material.COPPER_PICKAXE);
            startingPickaxe.addEnchantment(Enchantment.UNBREAKING, 1);
            player.getInventory().addItem(startingPickaxe);
            player.getInventory().addItem(ItemStack.of(Material.GOLDEN_CARROT, 4));
            player.getInventory().addItem(ItemStack.of(Material.BLACKSTONE, 32));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 0));
        }
        ItemUtil.giveNethersideCompass(this);
    }

    public void handleTeam()
    {
        Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
        switch (spawnLocation)
        {
            case NETHER -> board.getTeam(Game.NETHER_TEAM_NAME).addPlayer(Bukkit.getOfflinePlayer(getID()));
            case OVERWORLD -> board.getTeam(Game.OVERWORLD_TEAM_NAME).addPlayer(Bukkit.getOfflinePlayer(getID()));
        }
    }

    public Optional<Player> getPlayer()
    {
        return Optional.ofNullable(Bukkit.getPlayer(playerID));
    }

    /**
     * Called when a ParticipantTask has is cancelled. Cleans up the task from the participant.
     * @param task The task that has been cancelled.
     */
    public void taskConcluded(ParticipantTask task)
    {
        tasks.remove(task);
    }
    public void cleanupParticipant()
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
    public SpawnLocation getSpawnLocation() {return spawnLocation;}
    public boolean getRestored() {return restored;}
    public void setRestoredTrue()
    {
        if(!game.isPreGame())
            restored = true;
    }

}
