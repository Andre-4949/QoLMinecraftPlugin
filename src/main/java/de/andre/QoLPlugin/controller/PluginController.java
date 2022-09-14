package de.andre.QoLPlugin.controller;

import de.andre.QoLPlugin.Main;

public class PluginController {
    private final Main main;
    private ConfigController config;
    private ListenerController listenerController;
    public PluginController(Main main) {
        this.main = main;
    }

    public Main getMain() {
        return main;
    }

    public ConfigController getConfig() {
        return this.config;
    }

    public void setConfig(ConfigController config){
        this.config = config;
        config.onEnable();
        this.listenerController = new ListenerController(this);
        listenerController.onEnable();
        EnchantmentController.registerLocalEnchantments(listenerController);
    }

    public ListenerController getListenerController() {
        return listenerController;
    }
}
