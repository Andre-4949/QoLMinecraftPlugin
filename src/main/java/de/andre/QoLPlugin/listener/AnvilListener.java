package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;

public class AnvilListener implements QoLListener{
    private final PluginController controller;

    public AnvilListener(PluginController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void placeItemInAnvil(PrepareAnvilEvent event){
        if(controller.getConfig().getUnlimitedCost())
            event.getInventory().setMaximumRepairCost(Integer.MAX_VALUE);
            event.getInventory().setRepairCost(80);
    }
}
