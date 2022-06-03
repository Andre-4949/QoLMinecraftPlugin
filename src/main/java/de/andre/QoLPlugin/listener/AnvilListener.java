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
        if (controller.getConfig().isUnlimitedCost()) {
            event.getInventory().setMaximumRepairCost(Integer.MAX_VALUE);
            if (event.getInventory().getRepairCost() > 39) {
                event.getInventory().setRepairCost(80);
                event.getViewers().forEach(x -> x.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "The repair cost is set to 80 lvls, because the repair cost exceeded the vanilla maximum."));
            }
        }
    }
}
