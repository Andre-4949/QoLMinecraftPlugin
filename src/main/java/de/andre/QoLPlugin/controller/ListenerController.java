package de.andre.QoLPlugin.controller;

import de.andre.QoLPlugin.listener.*;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class ListenerController {
    private final PluginController controller;

    private final ArrayList<QoLListener> listeners = new ArrayList<>();

    public ListenerController(PluginController controller) {
        this.controller = controller;
    }

    public void onEnable(){
        listeners.add(new FarmingListener(controller));
        listeners.add(new DispenserListener(controller));
        listeners.add(new PlayerListener(controller));
        if (controller.getConfig().isUnlimitedCost())
            listeners.add(new AnvilListener(controller));
        listeners.add(new VineMiner(controller));
        if (controller.getConfig().isFastLeafDecayEnabled())
            listeners.add(new FastLeafDecay(controller));
        if (controller.getConfig().isToolBreakPreventionEnabled())
            listeners.add(new ToolBreakPrevention(controller));
        registerListener();
    }

    private void registerListener(){
        HandlerList.unregisterAll(controller.getMain());
        listeners.forEach(listener->controller.getMain().getServer().getPluginManager().registerEvents(listener, controller.getMain()));
    }

    public QoLListener getListener(Class<? extends QoLListener> c){
        for (QoLListener listener : listeners) {
            if (listener.getClass().equals(c))return listener;
        }
        return null;
    }

}