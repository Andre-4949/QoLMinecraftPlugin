package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class VineMiner implements QoLListener {
    private final PluginController controller;
    public static int MAXVINEBLOCKS = 512;

    public VineMiner(PluginController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onMine(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player p = event.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();
        Block targetBlock = event.getBlock();
        if(targetBlock.getType()!= Material.SPRUCE_LOG)return;
        event.setCancelled(true);
        Bukkit.getScheduler().runTask(controller.getMain(), ()-> {
            getConnectingBlocks(targetBlock).forEach(x -> x.breakNaturally(i, false));//no effect for better performances
        });
    }

    public static ArrayList<Block> getNeighbouringBlocks(Block b){
        ArrayList<Integer> integers = new ArrayList<Integer>(){{add(1);add(-1);add(0);}};
        return new ArrayList<Block>(){{//iterate through all possibilities of -1; 0; 1 -> 3*3*3 -> 27 blocks (diagonal block also count)
            for (Integer x : integers) {
                for (Integer y : integers) {
                    for (Integer z : integers) {
                        add(b.getRelative(x,y,z));
                    }
                }
            }
        }};

    }

    public ArrayList<Block> getConnectingBlocks(Block b){
        ArrayList<Block> vineBlocks = new ArrayList<Block>(){{add(b);}};
        int lastAmount;

        do {
            HashSet<Block> newVineBlocks = new HashSet<>();
            lastAmount = vineBlocks.size();
            for (Block vineBlock : vineBlocks) {
                newVineBlocks.addAll(getNeighbouringBlocks(vineBlock).stream().filter(x -> x.getType().equals(b.getType())).collect(Collectors.toList()));
            }
            vineBlocks.addAll(newVineBlocks);
            Set<Block> vineSet = new HashSet<>(vineBlocks);
            vineBlocks.clear();
            vineBlocks.addAll(vineSet);
        }while((vineBlocks.size()<MAXVINEBLOCKS && vineBlocks.size()!=lastAmount));//when the same size, we don't make progress so let's just stop gathering blocks
        return vineBlocks;
    }
}
