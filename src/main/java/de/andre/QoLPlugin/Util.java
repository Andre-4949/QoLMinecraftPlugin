package de.andre.QoLPlugin;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.permissions.ServerOperator;

public class Util {
    Util(){}

    public static void sendMessageToAdmins(Object o){
        Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(x->x.sendMessage(o.toString()));
    }

    public static void sendInfoLogMessage(PluginController controller, Object o){
        Bukkit.getLogger().info(controller.getConfig().getMessages().getServerPrefix()+o.toString());
    }
}
