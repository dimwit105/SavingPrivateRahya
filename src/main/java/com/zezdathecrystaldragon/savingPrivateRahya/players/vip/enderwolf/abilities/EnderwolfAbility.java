package com.zezdathecrystaldragon.savingPrivateRahya.players.vip.enderwolf.abilities;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public interface EnderwolfAbility extends Listener
{
    default void remove()
    {
        HandlerList.unregisterAll(this);
    }
}
