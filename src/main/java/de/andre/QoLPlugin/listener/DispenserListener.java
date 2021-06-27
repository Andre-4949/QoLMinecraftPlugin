package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;

public class DispenserListener implements QoLListener{
    private final PluginController controller;

    public DispenserListener(PluginController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event){

        //TODO make this shit work

        if(event.getBlock().getType()!=Material.DISPENSER)return;


        Location targetBlockLocation = event.getBlock().getLocation();
        Block dispenser = event.getBlock();
        Dispenser dispenser1 = (Dispenser) dispenser.getState();

        Directional dispenser_ = (Directional)dispenser.getBlockData();

        switch (dispenser_.getFacing()){
            case NORTH:
                targetBlockLocation.setZ(targetBlockLocation.getZ()-1);
                break;
            case EAST:
                targetBlockLocation.setX(targetBlockLocation.getX()+1);
                break;
            case SOUTH:
                targetBlockLocation.setZ(targetBlockLocation.getZ()+1);
                break;
            case WEST:
                targetBlockLocation.setX(targetBlockLocation.getX()-1);
                break;
            default:
                return;
        }
        Block targetBlock = targetBlockLocation.getBlock();

        if(     event.isCancelled() ||
                targetBlock.getType() != Material.LAVA_CAULDRON ||
                event.getItem().getType() != Material.BUCKET ){
            return;
        }

        ItemStack bucket = event.getItem();
        targetBlock.setType(Material.CAULDRON);


        if(dispenser1.getInventory().containsAtLeast(bucket,1)){
            dispenser1.getInventory().remove(bucket);
        }

        bucket.setAmount(1);
        bucket.setType(Material.LAVA_BUCKET);
        event.setItem(bucket);
        
        event.getBlock().setType(Material.DISPENSER,false);
        event.getBlock().setBlockData(dispenser1.getBlockData());
    }
}