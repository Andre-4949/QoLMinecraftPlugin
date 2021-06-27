package de.andre.QoLPlugin.controller;

import de.andre.QoLPlugin.listener.DispenserListener;
import de.andre.QoLPlugin.listener.FarmingListener;
import de.andre.QoLPlugin.listener.QoLListener;

import java.util.ArrayList;

public class ListenerController {
    private final PluginController controller;

    private final ArrayList<QoLListener> listeners = new ArrayList<>();

    public ListenerController(PluginController controller) {
        this.controller = controller;
        listeners.add(new FarmingListener(controller));
        listeners.add(new DispenserListener(controller));
        //listeners.add(new PlayerListener(controller));
        registerListener();
    }

    private void registerListener(){
        listeners.forEach(listener->controller.getMain().getServer().getPluginManager().registerEvents(listener, controller.getMain()));
    }

}