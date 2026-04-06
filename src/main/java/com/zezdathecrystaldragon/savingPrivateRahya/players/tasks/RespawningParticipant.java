package com.zezdathecrystaldragon.savingPrivateRahya.players.tasks;

import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import com.zezdathecrystaldragon.savingPrivateRahya.players.util.ParticipantTask;
import com.zezdathecrystaldragon.savingPrivateRahya.util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.List;

public class RespawningParticipant extends ParticipantTask
{
    Player player;
    int respawnTime = 16;
    public RespawningParticipant(Participant participant)
    {
        super(participant);
        this.player = participant.getPlayer();
    }

    @Override
    public void cancel()
    {
        player.setGameMode(GameMode.SURVIVAL);
        super.cancel();
    }
    @Override
    public void run()
    {
        sendRespawnMessage();
        if(respawnTime == 0)
        {
            player.setGameMode(GameMode.SURVIVAL);
            player.setSaturation(20F);
            player.setFoodLevel(20);
            ItemUtil.giveNethersideCompass(participant);
            this.cancel();
        }
        else
        {
            respawnTime--;
        }
    }
    void sendRespawnMessage()
    {
        if(respawnTime > 0)
            player.sendActionBar(Component.text(String.format("Respawning in %s seconds!", respawnTime)));
        else
            player.sendActionBar(Component.text("Respawning!"));
    }
}
