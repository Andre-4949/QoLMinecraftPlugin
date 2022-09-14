package de.andre.QoLPlugin.CustomEnchantments;

import de.andre.QoLPlugin.controller.EnchantmentController;
import io.papermc.paper.enchantments.EnchantmentRarity;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Telekinesis extends CustomEnchantments {
    private final String name;

    public Telekinesis(@NotNull NamespacedKey key) {
        super(key);
        this.name = StringUtils.capitalise(key.toString().split(":")[1]);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player p = event.getPlayer();
        ItemStack mainHand = p.getInventory().getItemInMainHand();
        if (mainHand.getType() != Material.AIR && mainHand.containsEnchantment(EnchantmentController.TELEKINESIS)){
            event.setDropItems(false);
            event.getBlock().getDrops(mainHand,p).forEach(item->
                    p.getInventory().addItem(item).forEach((k,value)->{
                        if (!value.getType().equals(Material.AIR))
                            p.getWorld().dropItem(p.getLocation(),value);
                    }));
        }
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return super.getKey();
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public @NotNull EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }


    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack item) {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return EnchantmentRarity.COMMON;
    }

    @Override
    public float getDamageIncrease(int level, @NotNull EntityCategory entityCategory) {
        return 0;
    }

    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        return Set.of(EquipmentSlot.HAND,EquipmentSlot.OFF_HAND);
    }

    @Override
    public @NotNull String translationKey() {
        return "telekinesis";
    }

}
