package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.controller.PluginController;

public class PlayerListener implements QoLListener {
    private PluginController controller;

    public PlayerListener(PluginController controller) {
        this.controller = controller;
    }

}
