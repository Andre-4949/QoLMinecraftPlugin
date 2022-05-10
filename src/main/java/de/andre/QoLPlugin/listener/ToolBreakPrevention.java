package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.controller.PluginController;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Objects;

public class ToolBreakPrevention implements QoLListener {
    private final PluginController controller;
    public static final String DETECTSTRING = "almost unbreakable";

    public ToolBreakPrevention(PluginController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onItemUse(PlayerItemDamageEvent event) {
        //return when the code below shouldn't be executed
        if (event.getItem().lore() == null || !event.getItem().getItemMeta().hasEnchant(Enchantment.MENDING) || Objects.requireNonNull(event.getItem().lore()).stream().noneMatch(x -> PlainTextComponentSerializer.plainText().serialize(x).equalsIgnoreCase(DETECTSTRING)))
            return;
        int itemDurability = durabilityLeft(event.getItem());

        Bukkit.getLogger().info(String.valueOf(itemDurability));

        if (itemDurability == 10) {//warnings are never that bad
            event.getPlayer().sendMessage(controller.getConfig().getMessages().getSERVERPREFIX() + String.format("Your %s has low durability.", event.getItem().getType().toString().toLowerCase()));
        }
        if (!event.getItem().getType().equals(Material.ELYTRA) && !(itemDurability <= 1)) {
            return;//for "normal" Items
        } else {
            if (!(itemDurability <= 2)) {//Elytra as special case, at one durability it is that other form, when you have to repair it with leather
                return;
            }
        }
        int playerXp = getPlayerExp(event.getPlayer()); //check out credit below

        if (playerXp <= 0) {//if he doesn't have xp to repair the items why bother with this event
            return;
        }


        Damageable damageable = ((Damageable) event.getItem().getItemMeta());

        int diff = damageable.getDamage();

        damageable.setDamage(0);//after that the item is basically as new || full durability
        event.getItem().setItemMeta(damageable);//overwrite old itemmeta


        //correct xp
        event.getPlayer().setExp(0f);
        event.getPlayer().setLevel(0);
        changePlayerExp(event.getPlayer(), playerXp - diff); // give back the xp minus the difference

        //let's inform the player he almost broke his stuff
        event.getPlayer().sendMessage(controller.getConfig().getMessages().getSERVERPREFIX() + String.format("Your %s almost broke. We tried to use your experience to repair it.", event.getItem().getType().toString().toLowerCase()));

        if (event.getItem().getType().equals(Material.ELYTRA)) {//and let's just for convenience open the elytra
            event.getPlayer().setGliding(true);
        }

    }


    public static int durabilityLeft(ItemStack i) {
        Damageable damageable = ((Damageable) i.getItemMeta());
        return i.getType().getMaxDurability() - damageable.getDamage();
    }

    /*
     * All the code below is from this Discussion: https://www.spigotmc.org/threads/how-to-get-players-exp-points.239171/
     * Credit: DOGC_Kyle
     * website: https://www.akenland.com/  <- website belongs to his RPG-Inspired, Community-Focused Survival Server
     * GitHub: https://github.com/KyleNanakdewa
     * Discord: Kade#1805
     */

    // Calculate amount of EXP needed to level up
    public static int getExpToLevelUp(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

    // Calculate total experience up to a level
    public static int getExpAtLevel(int level) {
        if (level <= 16) {
            return (int) (Math.pow(level, 2) + 6 * level);
        } else if (level <= 31) {
            return (int) (2.5 * Math.pow(level, 2) - 40.5 * level + 360.0);
        } else {
            return (int) (4.5 * Math.pow(level, 2) - 162.5 * level + 2220.0);
        }
    }

    // Calculate player's current EXP amount
    public static int getPlayerExp(Player player) {
        int exp = 0;
        int level = player.getLevel();

        // Get the amount of XP in past levels
        exp += getExpAtLevel(level);

        // Get amount of XP towards next level
        exp += Math.round(getExpToLevelUp(level) * player.getExp());

        return exp;
    }

    // Give or take EXP
    public static int changePlayerExp(Player player, int exp) {
        // Get player's current exp
        int currentExp = getPlayerExp(player);

        // Reset player's current exp to 0
        player.setExp(0);
        player.setLevel(0);

        // Give the player their exp back, with the difference
        int newExp = currentExp + exp;
        player.giveExp(newExp);

        // Return the player's new exp amount
        return newExp;
    }

}
