package de.andre.QoLPlugin;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.util.logging.Level;

public class Util {
    Util(){}

    public static void sendMessageToAdmins(PluginController controller,Object o){
        Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(x->x.sendMessage(controller.getConfig().getMessages().getSERVERPREFIX()+o.toString()));
    }
    public static void sendInfoLogMessage(PluginController controller, Object o){
        Bukkit.getLogger().info(controller.getConfig().getMessages().getSERVERPREFIX()+o.toString());
    }
    public static void sendWarnLogMessage(PluginController controller, Object o){
        Bukkit.getLogger().warning(controller.getConfig().getMessages().getSERVERPREFIX()+o.toString());
    }
    public static void sendSevereLogMessage(PluginController controller, Object o){
        Bukkit.getLogger().log(Level.SEVERE,controller.getConfig().getMessages().getSERVERPREFIX()+o.toString());
    }

    public static void smartSendMessage(PluginController controller, Object p, String msg) {
        if(p instanceof Player){
            ((Player) p).sendMessage(controller.getConfig().getMessages().getSERVERPREFIX()+ msg);
        } else if(p instanceof CommandSender){
            ((CommandSender) p).sendMessage(controller.getConfig().getMessages().getPLUGINPREFIX()+ msg);
        } else{
            sendWarnLogMessage(controller,"Plugin failed to send message | content: " + msg);
        }
    }
}