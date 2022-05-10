package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.LeavesDecayEvent;

import java.util.ArrayList;
import java.util.Random;

public class FastLeafDecay implements QoLListener {
    private final PluginController controller;
    private ArrayList<Block> leaves = new ArrayList<>();

    public FastLeafDecay(PluginController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        leaves.add(event.getBlock());
        leavesRemove(event.getBlock());
        controller.getMain().getServer().getScheduler().runTaskLater(controller.getMain(), () -> event.getBlock().breakNaturally(), new Random().nextInt(400) + 1);
        leaves.remove(event.getBlock());
    }

    private void leavesRemove(Block b) {
        VineMiner.getNeighbouringBlocks(b).stream()
                .filter(x -> x.getType().equals(b.getType()))
                .filter(x -> {
                    Leaves leaves = ((Leaves) x.getBlockData());
                    return !leaves.isPersistent() && leaves.getDistance() > 7;
                })
                .filter(x -> !leaves.contains(x))
                .forEach(x -> controller.getMain().getServer().getPluginManager().callEvent(new LeavesDecayEvent(x)));
    }

}
