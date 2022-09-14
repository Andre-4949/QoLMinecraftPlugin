package de.andre.QoLPlugin.controller;

import de.andre.QoLPlugin.CustomEnchantments.Telekinesis;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;

public class EnchantmentController {
    public static final Telekinesis TELEKINESIS = new Telekinesis(NamespacedKey.minecraft("telekinesis"));

    public static void registerLocalEnchantments(ListenerController listenerController){
        if (!Enchantment.isAcceptingRegistrations()) {
            try {
                Field accemptingNew = Enchantment.class.getDeclaredField("acceptingNew");
                accemptingNew.setAccessible(true);
                accemptingNew.set(null,true);
                accemptingNew.setAccessible(false);

                Enchantment.registerEnchantment(TELEKINESIS);
                listenerController.addListener(TELEKINESIS);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Enchantment.stopAcceptingRegistrations();
    }
}
