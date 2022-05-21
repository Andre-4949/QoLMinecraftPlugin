package de.andre.QoLPlugin;

import de.andre.QoLPlugin.Commands.*;
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
        controller.onEnable();
        controller.getConfig().onEnable();
        registerCommands();
    }

    private void registerCommands(){
        registerCommand("qol", new QolDefaultCommand(controller));
        registerCommand("adminmode", new AdminMode(controller));
        registerCommand("viewInventory", new ViewInventory(controller));
        registerCommand("togglemute", new MutePlayer(controller));
        registerCommand("collectiveCommands", new BetterCommands(controller));
        registerCommand("UnlimitedAnvil", new UnlimitedAnvil(controller));
        registerCommand("SendCoordsOfDeathOnRespawn", new SendCoordsOfDeathOnRespawn(controller));
        registerCommand("ping", new Ping(controller));
        registerCommand("breakprevention", new ToolBreakPreventionCommand(controller));
        registerCommand("bp", new ToolBreakPreventionCommand(controller));
        registerCommand("vineMiner", new VineMinerCommand(controller));
        registerCommand("vm", new VineMinerCommand(controller));
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
