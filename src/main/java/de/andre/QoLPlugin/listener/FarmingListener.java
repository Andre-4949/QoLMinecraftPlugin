package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class FarmingListener implements QoLListener {
    private final PluginController controller;

    public FarmingListener(PluginController controller) {
        this.controller = controller;
    }


    @EventHandler
    public void onHarvest(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getPlayer().isSneaking() || !controller.getConfig().isSimpleHarvestEnabled()) return;

        List<Material> seeds = controller.getConfig().getSimpleHarvestMaterials();

        Block block = event.getClickedBlock();

        Player p = event.getPlayer();

        if (seeds.stream().anyMatch(x -> x.equals(Objects.requireNonNull(block).getType()))) {

            Ageable age = (Ageable) block.getBlockData();

            if (age.getAge() == age.getMaximumAge()) {
                block.getDrops(
                                p.getInventory().getItemInMainHand(), p).
                        forEach(item ->
//                                p.getWorld().dropItemNaturally(block.getLocation(), item)
                                        p.getInventory().addItem(item).forEach((k,value)-> {
                                            if ((!value.getType().equals(Material.AIR)))
                                                p.getWorld().dropItem(p.getLocation(), value);
                                        })
                        );

                block.setType(block.getType());
            }

        }
    }

    @EventHandler
    public void onRightClickOnComposter(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || Objects.requireNonNull(event.getClickedBlock()).getType() != Material.COMPOSTER || controller.getConfig().isNotAdvancedCompostEnabled())
            return;

        Block block = event.getClickedBlock();
        Player p = event.getPlayer();
        Levelled composter = (Levelled) block.getBlockData();

        if (controller.getConfig().getAdvancedCompostMaterials().contains(p.getInventory().getItemInMainHand().getType())) {

            if (!p.isSneaking() && composter.getLevel() <= composter.getMaximumLevel()) {
                try {
                    composter.setLevel(composter.getLevel() + 1);
                    block.setBlockData(composter, false);
                    ItemStack playerMainHand = p.getInventory().getItemInMainHand();
                    playerMainHand.setAmount(playerMainHand.getAmount() - 1);
                    p.getInventory().setItemInMainHand(playerMainHand);
                    event.setCancelled(true);
                } catch (Exception ignored) {
                }


            } else {
                ItemStack playerMainHand = p.getInventory().getItemInMainHand();

                Location dropLocation = event.getClickedBlock().getLocation();
                dropLocation.setY(dropLocation.getY() + 1);

                ItemStack boneMeal = new ItemStack(Material.BONE_MEAL);
                boneMeal.setAmount((playerMainHand.getAmount() - playerMainHand.getAmount() % 8) / 8);
                if (boneMeal.getType() != Material.AIR && boneMeal.getAmount() > 0)
                    p.getWorld().dropItemNaturally(dropLocation, boneMeal);
                playerMainHand.setAmount(playerMainHand.getAmount() % 8);
                p.getInventory().setItemInMainHand(playerMainHand);
            }
        }
    }

    @EventHandler
    public void onHopperEvent(InventoryMoveItemEvent event) {
        if (!event.getDestination().getType().equals(InventoryType.COMPOSTER) ||
                !event.getSource().getType().equals(InventoryType.HOPPER) ||
                controller.getConfig().isNotAdvancedCompostEnabled() ||
                (event.getDestination().getLocation() != null &&
                        event.getSource().getLocation() != null) ||
                !controller.getConfig().getAdvancedCompostMaterials().contains(event.getItem().getType())
        ) return;

        Block destination = Objects.requireNonNull(event.getSource().getLocation()).add(0.0, -1.0, 0.0).getBlock();
        Levelled composter = (Levelled) destination.getBlockData();

        if (composter.getLevel() == composter.getMaximumLevel()) event.setCancelled(true);

        try {

            composter.setLevel(composter.getLevel() + 1);
            destination.setBlockData(composter, true);

            ItemStack removeItem = event.getItem();
            removeItem.setAmount(1);

            for (ItemStack itemStack : event.getSource()) {
                if (itemStack != null && itemStack.getType().equals(removeItem.getType())) {
                    itemStack.setAmount(itemStack.getAmount() - 1);
                    return;
                }
            }

        } catch (IllegalArgumentException | NullPointerException ignored) {
        }
    }
}