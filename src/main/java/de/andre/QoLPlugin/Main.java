package de.andre.QoLPlugin;

import de.andre.QoLPlugin.Commands.QolDefaultCommand;
import de.andre.QoLPlugin.controller.ConfigController;
import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {

    private final PluginController controller = new PluginController(this);

    @Override
    public void onEnable() {
        controller.setConfig(new ConfigController(controller));
        registerCommands();
        controller.onEnable();
        controller.getConfig().onEnable();
    }

    private void registerCommands(){
        registerCommand("qol", new QolDefaultCommand(controller));
    }

    private void registerCommand(String s, Object e){
        PluginCommand command = getCommand(s);

        if(Objects.equals(command, null))throw new RuntimeException("Command is not defined in plugin.yml");

        if(e instanceof CommandExecutor)command.setExecutor((CommandExecutor) e);

        if (e instanceof TabCompleter)command.setTabCompleter((TabCompleter) e);
    }

    @Override
    public void onDisable() {
        controller.getConfig().saveConfig();
        Bukkit.getWorlds().forEach(World::save);
    }

}
