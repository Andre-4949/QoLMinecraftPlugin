package de.andre.QoLPlugin.controller;

import de.andre.QoLPlugin.listener.*;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class ListenerController {
    private final PluginController controller;

    private final ArrayList<QoLListener> listeners = new ArrayList<>();

    public ListenerController(PluginController controller) {
        this.controller = controller;
        listeners.add(new FarmingListener(controller));
        listeners.add(new DispenserListener(controller));
        listeners.add(new PlayerListener(controller));
        listeners.add(new AnvilListener(controller));
        registerListener();
    }

    private void registerListener(){
        HandlerList.unregisterAll(controller.getMain());
        listeners.forEach(listener->controller.getMain().getServer().getPluginManager().registerEvents(listener, controller.getMain()));
    }

}