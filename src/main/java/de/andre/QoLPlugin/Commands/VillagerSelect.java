package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.controller.PluginController;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VillagerSelect implements CommandExecutor, TabCompleter {
    private final PluginController controller;

    public VillagerSelect(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p) || !controller.getConfig().isVillagerSelectEnabled()) return true;

        if (args.length < 2) return true;

        ItemStack item;
        Enchantment enchantment = enchantmentByString(args[1]);
        if (enchantment != null) {
            item = new ItemStack(Material.ENCHANTED_BOOK, 1);
            ItemMeta itemMeta = item.getItemMeta();
            int level = 1;
            if (args.length == 3) {
                try {
                    level = Math.min(Math.max(Integer.parseInt(args[2]), 1), enchantment.getMaxLevel());
                } catch (NumberFormatException e){
                    p.sendMessage("There was an error parsing the number of the level");
                    return true;
                }
            }
            if (enchantment.equals(Enchantment.SOUL_SPEED)) return true;
            itemMeta.addEnchant(enchantment, level, false);
            item.setItemMeta(itemMeta);
        } else {
            item = new ItemStack(Material.valueOf(args[1].replace("minecraft:", "").toUpperCase()), 1);
        }

        UUID uuid = UUID.fromString(args[0]);

        List<Entity> entities = p.getNearbyEntities(5, 5, 5).stream().filter(x -> x.getUniqueId().equals(uuid)).collect(Collectors.toList());
        if (entities.size() == 0) {
            p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "Entity not found, please be near your desired entity.");
            return true;
        }
        Entity entity = entities.get(0);
        if (entity.getType() != EntityType.VILLAGER) {
            p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "Your Entity is not a Villager, please target a villager");
            return true;
        }
        Villager v = (Villager) entity;

        if (v.getProfession().equals(Villager.Profession.NONE) || v.getProfession().equals(Villager.Profession.NITWIT)) {
            p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "The villager needs to have a profession");
        }
        if (v.getVillagerExperience() != 0) {
            p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "The villager is not allowed to have any experience in his profession.");
        }
        p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "Modifying started");
        modifyVillager(v, item, controller.getConfig().getVillagerSelectMaxRetries());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return new ArrayList<>();
        Villager.Profession v = Villager.Profession.NONE;
        if (args.length > 1) {
            UUID uuid = UUID.fromString(args[0]);
            List<Entity> entities = p.getNearbyEntities(5, 5, 5).stream().filter(x -> x.getUniqueId().equals(uuid)).collect(Collectors.toList());
            if (entities.size() == 0) {
                p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "Entity not found, please be near your desired entity.");
                return new ArrayList<>();
            }
            Entity entity = entities.get(0);
            if (entity.getType() != EntityType.VILLAGER) {
                p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "Your Entity is not a Villager, please target a villager");
                return new ArrayList<>();
            }
            if (args.length == 2)
                ((Villager) entity).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 3 * 20, 1));
            v = ((Villager) entity).getProfession();
        }
        switch (args.length) {
            case 1:
                Entity entity = p.getTargetEntity(5, false);
                return entity == null ? new ArrayList<>() : new ArrayList<>(Collections.singletonList(entity.getUniqueId().toString()));
            case 2:
                return itemTabComplete(p, v);
            case 3:
                Enchantment e = enchantmentByString(args[1]);
                if (e == null) {
                    return new ArrayList<>();
                }
                return new ArrayList<>() {{
                    for (int i = 1; i < e.getMaxLevel() + 1; i++) {
                        add(String.valueOf(i));
                    }
                }};
            default:
                return new ArrayList<>();
        }
    }

    private ArrayList<String> itemTabComplete(Player p, Villager.Profession v) {
        return switch (v) {
            case NONE, NITWIT -> new ArrayList<>();
            case MASON -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.BRICK.toString());
            }};
            case CLERIC -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.REDSTONE.toString());
            }};
            case FARMER -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.BREAD.toString());
            }};
            case ARMORER -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.IRON_BOOTS.toString());
                add(Material.IRON_LEGGINGS.toString());
                add(Material.IRON_CHESTPLATE.toString());
                add(Material.IRON_HELMET.toString());
            }};
            case BUTCHER -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.RABBIT_STEW.toString());
            }};
            case FLETCHER -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.ARROW.toString());
            }};
            case SHEPHERD -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.SHEARS.toString());
            }};
            case FISHERMAN -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.COD_BUCKET.toString());
            }};
            case LIBRARIAN -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.BOOKSHELF.toString());
                for (Enchantment value : Enchantment.values()) {
                    if (!value.equals(Enchantment.SOUL_SPEED))//Soul speed is not obtainable from villagers
                        add(value.key().toString());
                }
            }};
            case TOOLSMITH -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.STONE_AXE.toString());
                add(Material.STONE_SHOVEL.toString());
                add(Material.STONE_PICKAXE.toString());
                add(Material.STONE_HOE.toString());
            }};
            case WEAPONSMITH -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.IRON_AXE.toString());
                add(Material.IRON_SWORD.toString());
            }};
            case CARTOGRAPHER -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.MAP.toString());
            }};
            case LEATHERWORKER -> new ArrayList<>() {{
                add(Material.EMERALD.toString());
                add(Material.LEATHER_CHESTPLATE.toString());
                add(Material.LEATHER_LEGGINGS.toString());
            }};
        };
    }

    private Enchantment enchantmentByString(String s) {
        for (Enchantment value : Enchantment.values()) {
            if (value.key().toString().equalsIgnoreCase(s) || value.key().toString().toLowerCase().replace("minecraft:", "").equals(s.toLowerCase())) {
                return value;
            }
        }
        return null;
    }

    private void modifyVillager(Villager v, ItemStack result, int i) {
        if (!v.getLocation().getChunk().isLoaded()) {
            v.getLocation().getChunk().load();
        }
        if (i <= 0) {
            v.customName(null);
            v.setCustomNameVisible(false);
            return;
        }
        // block start
        // this block has originally been below the for-loop, but it didn't work like intended, that's why I pasted it up here, so it works as a do-while loop (thats the best i can explain it)
        v.customName(Component.text(i / 20 + "s"));
        v.setCustomNameVisible(true);
        i--;
        v.resetOffers();
        // block end


        for (MerchantRecipe recipe : v.getRecipes()) {
            Material recipeType = recipe.getResult().getType();
            if (
                    (
                            recipeType.equals(result.getType()) &&
                                    !recipeType.equals(Material.ENCHANTED_BOOK) &&
                                    !result.getType().equals(Material.ENCHANTED_BOOK)
                    ) || (
                            recipeType.equals(Material.ENCHANTED_BOOK) &&
                                    result.getType().equals(Material.ENCHANTED_BOOK) &&
                                    ((EnchantmentStorageMeta) recipe.getResult().getItemMeta()).getStoredEnchants().equals(result.getEnchantments())
                    )
            ) {
                v.getLocation().getWorld().spawnParticle(Particle.SMALL_FLAME, v.getLocation().add(0, 2, 0), 50);
                Bukkit.getScheduler().runTaskLater(controller.getMain(), () -> {
                    v.customName(null);
                    v.setCustomNameVisible(false);
                }, 100);
                v.customName(Component.text("Done!"));
                return;
            }
        }
        int finalI = i;
        Bukkit.getScheduler().runTaskLater(controller.getMain(), () -> modifyVillager(v, result, finalI), controller.getConfig().getVillagerSelectCooldown());
    }
}
