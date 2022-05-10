package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;

public class DispenserListener implements QoLListener {
    private final PluginController controller;

    public DispenserListener(PluginController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {

        if (event.getBlock().getType() != Material.DISPENSER) return;

        Location targetBlockLocation = event.getBlock().getLocation();
        Block dispenser = event.getBlock();
        Dispenser dispenser1 = (Dispenser) dispenser.getState();

        Directional dispenser_ = (Directional) dispenser.getBlockData();
        targetBlockLocation.add(dispenser_.getFacing().getDirection());

        Block targetBlock = targetBlockLocation.getBlock();

        if (event.isCancelled() ||
                targetBlock.getType() != Material.LAVA_CAULDRON ||
                event.getItem().getType() != Material.BUCKET) {
            return;
        }

        ItemStack bucket = event.getItem();
        ItemStack bucket_ = bucket.clone();


        bucket.setAmount(1);
        bucket.setType(Material.LAVA_BUCKET);
        event.setItem(bucket);
        targetBlock.setType(Material.CAULDRON);
        Bukkit.getScheduler().runTaskLater(controller.getMain(),()->{
            for (ItemStack itemStack : dispenser1.getInventory()) {
                if (itemStack != null && itemStack.getType().equals(bucket_.getType())) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    break;
                }
            }
            dispenser.setBlockData(dispenser1.getBlockData(), true);
        },1L);
    }
}