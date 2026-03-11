package com.zezdathecrystaldragon.savingPrivateRahya.util;

import com.zezdathecrystaldragon.savingPrivateRahya.players.Participant;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.List;

public class ItemUtil
{
    public static void giveNethersideCompass(Participant participant)
    {
        ItemStack nethersideCompass = ItemStack.of(Material.COMPASS);
        CompassMeta meta = (CompassMeta) nethersideCompass.getItemMeta();
        meta.setLodestoneTracked(false);
        Location netherSidePortal = participant.getGame().wm.getNethersidePortal();
        List<Component> loreToAdd = List.of(Component.text("Points to the portal generated on the nether side."), Component.text(String.format("Nether Coordinates: %d, %d, %d",
                netherSidePortal.getBlockX(),
                netherSidePortal.getBlockY(),
                netherSidePortal.getBlockZ())));
        meta.setLodestone(netherSidePortal);
        meta.customName(Component.text(String.format("%s's way out", participant.getGame().getVip().getPlayer().getName())));
        meta.lore(loreToAdd);

        meta.setRarity(ItemRarity.RARE);
        nethersideCompass.setItemMeta(meta);
        participant.getPlayer().getInventory().addItem(nethersideCompass);

    }
}
