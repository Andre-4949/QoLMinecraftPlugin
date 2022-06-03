package de.andre.QoLPlugin;

import de.andre.QoLPlugin.controller.PluginController;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.logging.log4j.util.BiConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.ServerOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

public class Util {
    Util(){}

    public static void sendMessageToAdmins(PluginController controller,Object o){
        Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).forEach(x->x.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX()+o.toString()));
    }
    public static void sendInfoLogMessage(PluginController controller, Object o){
        Bukkit.getLogger().info(controller.getConfig().getMessageController().getSERVERPREFIX()+o.toString());
    }
    public static void sendWarnLogMessage(PluginController controller, Object o){
        Bukkit.getLogger().warning(controller.getConfig().getMessageController().getSERVERPREFIX()+o.toString());
    }
    public static void sendSevereLogMessage(PluginController controller, Object o){
        Bukkit.getLogger().log(Level.SEVERE,controller.getConfig().getMessageController().getSERVERPREFIX()+o.toString());
    }

    public static void smartSendMessage(PluginController controller, Object p, String msg) {
        if(p instanceof Player){
            ((Player) p).sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX()+ msg);
        } else if(p instanceof CommandSender){
            ((CommandSender) p).sendMessage(controller.getConfig().getMessageController().getPLUGINPREFIX()+ msg);
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

    public static ArrayList<Block> getCubeOfBlocksAroundBlock(Block b){
        ArrayList<Integer> integers = new ArrayList<>() {{
            add(1);
            add(-1);
            add(0);
        }};
        return new ArrayList<>() {{//iterate through all possibilities of -1; 0; 1 -> 3*3*3 -> 27 blocks (diagonal block also count)
            for (Integer x : integers) {
                for (Integer y : integers) {
                    for (Integer z : integers) {
                        add(b.getRelative(x, y, z));
                    }
                }
            }
        }};
    }

    public static ArrayList<Block> getNeighbouringBlocks(Block b){
        return new ArrayList<>(){{
            add(b.getRelative(BlockFace.UP));
            add(b.getRelative(BlockFace.DOWN));
            add(b.getRelative(BlockFace.NORTH));
            add(b.getRelative(BlockFace.EAST));
            add(b.getRelative(BlockFace.SOUTH));
            add(b.getRelative(BlockFace.WEST));
        }};
    }

    public static String componentToString(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static void executeCommand(String format) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), format);
    }

    public static ArrayList<ItemStack> simplifyItemStackList(ArrayList<ItemStack> ingredientRatio) {
        /*
        * This simplifies a list of many ItemStacks with the same material to one ItemStack with the combined amount
        * */
        HashMap<Material, Integer> ingredientHashMap = new HashMap<>();
        ingredientRatio.forEach(ingredient -> {
            if (ingredient != null)
                ingredientHashMap.put(ingredient.getType(), ingredientHashMap.getOrDefault(ingredient.getType(), 0) + ingredient.getAmount());
        });
        return new ArrayList<>() {{
            ingredientHashMap.forEach(((material, amount) -> add(new ItemStack(material, amount))));
        }};
    }

    public static <T1, T2> void iterateParallel(Iterator<T1> c1, Iterator<T2> c2, BiConsumer<T1, T2> consumer) {
        while (c1.hasNext() && c2.hasNext()) {
            consumer.accept(c1.next(), c2.next());
        }
    }

    public static ItemStack[] removeItems(ItemStack[] inv,ArrayList<ItemStack> itemStacks) {
        itemStacks.forEach(ingredient -> {
            int itemsToRemove = ingredient.getAmount();
            for (ItemStack content : inv) {
                if (itemsToRemove <= 0) break;
                if (content != null && content.getType() != Material.AIR && content.getType() == ingredient.getType()) {
                    int diff = Math.min(itemsToRemove, content.getAmount());
                    itemsToRemove -= diff;
                    content.setAmount(content.getAmount() - diff);
                }
            }
        });
        return inv;
    }
}