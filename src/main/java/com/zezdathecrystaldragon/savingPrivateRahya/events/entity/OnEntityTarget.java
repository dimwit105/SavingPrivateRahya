package com.zezdathecrystaldragon.savingPrivateRahya.events.entity;

import com.zezdathecrystaldragon.savingPrivateRahya.SavingPrivateRahya;
import com.zezdathecrystaldragon.savingPrivateRahya.game.Game;
import com.zezdathecrystaldragon.savingPrivateRahya.players.vip.VeryImportantParticipant;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class OnEntityTarget implements Listener {
    public void onEntityTarget(EntityTargetEvent event) {
        SavingPrivateRahya.PLUGIN.getGame().getVip()
                .filter(vip -> event.getEntity() instanceof Wolf wolf
                        && wolf.getPersistentDataContainer().has(SavingPrivateRahya.PLUGIN.VIP_MOB))
                .flatMap(VeryImportantParticipant::getEnderwolf)
                .ifPresent(enderwolf -> enderwolf.onWolfTarget(event));
    }
}

