package de.andre.QoLPlugin.CustomEnchantments;

import de.andre.QoLPlugin.listener.QoLListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

abstract class CustomEnchantments extends Enchantment implements QoLListener {
    public CustomEnchantments(@NotNull NamespacedKey key) {
        super(key);
    }

    public void addEnchant(ItemStack i, int lvl) {
        ArrayList<Component> lore = new ArrayList<>();
        if (i.lore() != null) {
            lore.addAll(i.lore());
        }
        removeEnchant(i);
        lore.add(this.displayName(lvl));
        i.lore(lore);
    }

    @Override
    public @NotNull Component displayName(int level) {
        return Component.text((ChatColor.BLUE + StringUtils.capitalise(this.getKey().getKey()) + " " + "I".repeat(Math.max(0, level)).replace("IIIII", "V")
                .replace("IIII", "IV")
                .replace("VV", "X")
                .replace("VIV", "IX")
                .replace("XXXXX", "L")
                .replace("XXXX", "XL")
                .replace("LL", "C")
                .replace("LXL", "XC")
                .replace("CCCCC", "D")
                .replace("CCCC", "CD")
                .replace("DD", "M")
                .replace("DCD", "CM") + ChatColor.WHITE).replace(" I",""));
    }

    public void removeEnchant(ItemStack i) {
        ArrayList<Component> lore = new ArrayList<>();
        if (i.lore() != null) {
            lore.addAll(i.lore());
        }
        lore.removeIf(x -> PlainTextComponentSerializer.plainText().serialize(x).startsWith(this.getKey().toString()));
        i.lore(lore);
    }
}
