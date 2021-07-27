package de.andre.QoLPlugin.AdditionalClasses;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class AdminModeData {
    private ItemStack[] inv;
    private Location loc;
    public AdminModeData(ItemStack[] inv, Location loc) {
        this.inv = inv.clone();
        this.loc = loc.clone();
    }

    public ItemStack[] getInv() {
        return inv;
    }

    public Location getLoc() {
        return loc;
    }
}
