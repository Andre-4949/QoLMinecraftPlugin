package de.andre.QoLPlugin;

import de.andre.QoLPlugin.controller.PluginController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.ServerOperator;

import java.util.ArrayList;
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

    public static ItemStack[] removeAmount(ItemStack[] i, Material m, int amount) {
        for (ItemStack content : i) {
            if (content != null && content.getType().equals(m)) {
                int contentAmount = content.getAmount();
                if (contentAmount>amount){
                    content.setAmount(contentAmount-amount);
                    amount = 0;
                }else{
                    content.setType(Material.AIR);
                    amount-=contentAmount;
                }
                if (amount==0)return i;
            }
        }
        return i;
    }

    public static int countItems(ItemStack[] i, Material m){
        int amount = 0;
        for (ItemStack itemStack : i) {
            if(itemStack!=null&&itemStack.getType().equals(m)){
                amount+=itemStack.getAmount();
            }
        }
        return amount;
    }

    public static ArrayList<Block> getNeighbouringBlocks(Block b){
        ArrayList<Integer> integers = new ArrayList<Integer>(){{add(1);add(-1);add(0);}};
        return new ArrayList<Block>(){{//iterate through all possibilities of -1; 0; 1 -> 3*3*3 -> 27 blocks (diagonal block also count)
            for (Integer x : integers) {
                for (Integer y : integers) {
                    for (Integer z : integers) {
                        add(b.getRelative(x,y,z));
                    }
                }
            }
        }};
    }

    public static String componentToString(Component component){
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static void executeCommand(String format) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),format);
    }
}