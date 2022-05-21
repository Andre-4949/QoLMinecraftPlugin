package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.Util;
import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FastLeafDecay implements QoLListener {
    private final PluginController controller;
    private static ArrayList<Block> leaves = new ArrayList<>();

    public FastLeafDecay(PluginController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        leaves.add(event.getBlock());
        leavesRemove(event.getBlock().getLocation(), event.getBlock().getType());
        controller.getMain().getServer().getScheduler().runTaskLater(controller.getMain(), (@NotNull Runnable) event.getBlock()::breakNaturally, 1);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event){
        if (!event.isCancelled() && event.getBlock().getBlockData() instanceof Leaves) leavesRemove(event.getBlock().getLocation(),event.getBlock().getType());
    }

    public void onLeavesDecay(List<Block> l) {
        leaves.addAll(l);
        l.forEach(x->leavesRemove(x.getLocation(),x.getType()));
        l.forEach(block -> block.breakNaturally(false));
    }

    private void leavesRemove(Location l, Material m) {
        onLeavesDecay(
                Util.getNeighbouringBlocks(l.getBlock()).stream()
                        .filter(x -> x.getType().equals(m))
                        .filter(x -> !leaves.contains(x))
                        .filter(x -> {
                            if (!(x.getBlockData() instanceof Leaves)) return false;
                            Leaves leaves = ((Leaves) x.getBlockData());
                            return !leaves.isPersistent() && leaves.getDistance() >= 7;
                        }).collect(Collectors.toList()));
    }
}
