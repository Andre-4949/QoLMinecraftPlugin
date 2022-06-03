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
        if(!controller.getConfig().isFastLeafDecayEnabled())return;
        leaves.add(event.getBlock());
        leavesRemove(event.getBlock().getLocation(), event.getBlock().getType());
        controller.getMain().getServer().getScheduler().runTaskLater(controller.getMain(), ()-> leaves = new ArrayList<>(), 1);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event){
        if(!controller.getConfig().isFastLeafDecayEnabled())return;
        if (!event.isCancelled() && event.getBlock().getBlockData() instanceof Leaves) leavesRemove(event.getBlock().getLocation(),event.getBlock().getType());
    }

    public void onLeavesDecay(List<Block> l) {
        leaves.addAll(l);
        l.forEach(x->leavesRemove(x.getLocation(),x.getType()));
        l.forEach(block -> block.breakNaturally(false));
    }

    private void leavesRemove(Location l, Material m) {
        onLeavesDecay(
                Util.getCubeOfBlocksAroundBlock(l.getBlock()).stream()
                        .filter(x -> x.getType().equals(m) || controller.getConfig().isFastLeafDecayIgnoreLeafType())
                        .filter(x -> !leaves.contains(x))
                        .filter(x -> {
                            if (!(x.getBlockData() instanceof Leaves leaves)) return false;
                            return !leaves.isPersistent() && leaves.getDistance() >= 7;
                        }).collect(Collectors.toList()));
    }
}
