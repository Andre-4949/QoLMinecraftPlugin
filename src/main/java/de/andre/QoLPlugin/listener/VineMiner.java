package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.Util;
import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class VineMiner implements QoLListener {
    private final PluginController controller;
    public static ArrayList<Player> activeVineMiner = new ArrayList<>();

    public VineMiner(PluginController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onMine(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        if (controller.getConfig().isNotVineMinerEnabled())return;

        Player p = event.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();
        ItemStack tool = p.getInventory().getItemInMainHand().clone();
        Block targetBlock = event.getBlock();
        if (!(p.isSneaking() && activeVineMiner.contains(p))) return;


        //The event is cancelled so all drops go to the player when the block is later broken
        event.setCancelled(true);


        //just for performance
        HashMap<Material, Integer> totaldrops = new HashMap<>();
        AtomicInteger xp = new AtomicInteger();

        Bukkit.getScheduler().runTask(controller.getMain(), () -> {

            //get connecting Blocks
            ArrayList<Block> blocks = getConnectingBlocks(targetBlock);

            //sort by distance to player
            blocks.sort((x, y) -> {
                double xDist = x.getLocation().distance(p.getLocation());
                double yDist = y.getLocation().distance(p.getLocation());
                return (-1) * Double.compare(yDist, xDist);
            });
            org.bukkit.inventory.meta.Damageable damageable = (Damageable) i.getItemMeta();

            int durability = -1;
            if (damageable != null) durability = i.getType().getMaxDurability() - damageable.getDamage();

            if (durability>0) {//only applies if player item with tool

                int maxMineableBlocks = ToolBreakPrevention.isProtected(i, controller) ? durability - 2 : durability;
                //limit the blocks, so we don't get negative durability later on
                if (maxMineableBlocks <= 0) return;
                blocks = blocks.stream().limit(controller.getConfig().getVineMinerMaxBlocks()).limit(maxMineableBlocks).collect(Collectors.toCollection(ArrayList::new));
                //call event so ToolBreakPrevention is called
//                    controller.getMain().getServer().getPluginManager().callEvent(new PlayerItemDamageEvent(p, i, 1, 1));


                float unbreakingFactor = 1f / (1 + i.getEnchantmentLevel(Enchantment.DURABILITY));//from minecraft wiki
                damageable.setDamage(damageable.getDamage() + Math.max((int) (blocks.size() * unbreakingFactor), 1));
                i.setItemMeta(damageable);
                durability = i.getType().getMaxDurability() - damageable.getDamage();
                if (durability <= 0) {
                    i.setType(Material.AIR);
                }
                p.getInventory().setItemInMainHand(i);
                p.updateInventory();

            }

            blocks.forEach(x -> {
                x.getDrops(tool, p).forEach(y ->
                        totaldrops.put(y.getType(), totaldrops.getOrDefault(y.getType(), 0) + y.getAmount())
                );
                xp.addAndGet(new BlockBreakEvent(x, p).getExpToDrop());//as long as I don't call this event I'm fine :)... hopefully
                x.setType(Material.AIR);
            });
            totaldrops.forEach((material, value) -> p.getInventory().addItem(new ItemStack(material, value)).forEach((k, v) -> {
                if ((!v.getType().equals(Material.AIR)))
                    p.getWorld().dropItem(p.getLocation(), v);
            }));
            p.updateInventory();
            p.giveExp(p.applyMending(xp.get()));
        });
    }


    public ArrayList<Block> getConnectingBlocks(Block b){
        ArrayList<Block> vineBlocks = new ArrayList<>() {{
            add(b);
        }};
        int lastAmount;

        do {
            HashSet<Block> newVineBlocks = new HashSet<>();
            lastAmount = vineBlocks.size();
            for (Block vineBlock : vineBlocks) {
                newVineBlocks.addAll(Util.getCubeOfBlocksAroundBlock(vineBlock).stream().filter(x -> x.getType().equals(b.getType())).collect(Collectors.toList()));
            }
            vineBlocks.addAll(newVineBlocks);
            Set<Block> vineSet = new HashSet<>(vineBlocks);
            vineBlocks.clear();
            vineBlocks.addAll(vineSet);
        } while ((vineBlocks.size() < controller.getConfig().getVineMinerMaxBlocks() && vineBlocks.size() != lastAmount));//when the same size, we don't make progress so let's just stop gathering blocks
        return vineBlocks;
    }

    public void addPlayerToActiveVineMiner(Player p) {
        activeVineMiner.add(p);
    }

    public void removePlayerFromActiveVineMiner(Player p) {
        activeVineMiner.remove(p);
    }
}
