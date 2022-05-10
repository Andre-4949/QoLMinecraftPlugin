package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.Commands.AdminMode;
import de.andre.QoLPlugin.controller.PluginController;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.ServerOperator;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerListener implements QoLListener {
    private final PluginController controller;

    public PlayerListener(PluginController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {


        if (controller.getConfig().getMutedPlayers().contains(event.getPlayer()) && event.viewers().containsAll(Bukkit.getOnlinePlayers()))
            event.setCancelled(true);

        if (event.viewers().size() < Bukkit.getOnlinePlayers().size()) {
            Bukkit.getOnlinePlayers().stream().filter(ServerOperator::isOp).filter(x -> controller.getConfig().getAdminsWhichEnabledPrivateMessageViewing().contains(x)).forEach(x -> x.sendMessage(controller.getConfig().getMessages().getPRIVATEMESSAGEFROMONEPLAYERTOANOTHER(
                    event.getPlayer(),
                    new ArrayList<>(event.viewers().stream().map(y -> (Player) y).collect(Collectors.toList())),
                    event.message().toString())));
        }
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        if (controller.getConfig().getSendMessageOnGamemodeChange() && !event.getCause().equals(PlayerGameModeChangeEvent.Cause.PLUGIN) && event.getNewGameMode().equals(GameMode.CREATIVE) && !controller.getConfig().getAdminmodeHashmap().containsKey(event.getPlayer()))
            event.getPlayer().sendMessage(controller.getConfig().getMessages().getSERVERPREFIX() + "Please think about using the /adminmode command");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (!event.getPlayer().isOp() && !controller.getConfig().getAdminmodeHashmap().containsKey(event.getPlayer()))
            return;
        AdminMode.resetOneAdmin(controller, event.getPlayer());
    }

    @EventHandler
    public void onPlayerDied(PlayerDeathEvent event) {
        Location loc = event.getEntity().getLocation();
        Player p = event.getEntity();
        controller.getConfig().addPlayerToDeadPlayers(p, loc);
    }


    @EventHandler
    public void equipElytra(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getPlayer().isSneaking()) {
            if(Stream.of(Material.LEATHER_CHESTPLATE,Material.CHAINMAIL_CHESTPLATE,Material.IRON_CHESTPLATE,Material.GOLDEN_CHESTPLATE,Material.DIAMOND_CHESTPLATE,Material.NETHERITE_CHESTPLATE).noneMatch(x->event.getItem().getType().equals(x))){
                return;
            }
            Player p = event.getPlayer();

            if (p.getInventory().getChestplate() == null) return;
            ItemStack elytra = p.getInventory().getChestplate();

            if(!elytra.getType().equals(Material.ELYTRA))return;

            ItemStack chestplate = p.getInventory().getItemInMainHand();
            p.getInventory().setChestplate(chestplate.clone());
            p.getInventory().setItemInMainHand(elytra.clone());
            p.updateInventory();
            p.sendMessage(controller.getConfig().getMessages().getSERVERPREFIX() + String.format("You have now equipped your %s.",chestplate.getType().getKey()));

        } else {
            if (!event.getItem().getType().equals(Material.ELYTRA)) return;

            Player p = event.getPlayer();

            if (p.getInventory().getChestplate() == null) return;

            ItemStack chestplate = p.getInventory().getChestplate();
            ItemStack elytra = p.getInventory().getItemInMainHand();
            p.getInventory().setChestplate(elytra);
            p.getInventory().setItemInMainHand(chestplate);
            p.updateInventory();
            p.sendMessage(controller.getConfig().getMessages().getSERVERPREFIX() + "You have now equipped your Elytra.");
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (controller.getConfig().getSendCoordsOfDeathOnRespawn() && controller.getConfig().getDeadPlayers().containsKey(event.getPlayer())) {
            Location l = controller.getConfig().getDeadPlayers().get(event.getPlayer());
            event.getPlayer().sendMessage(String.format("You died in world: %s | x: %s | y: %s | z: %s",
                    l.getWorld().getName(), Math.round(l.getX()), Math.round(l.getY()), Math.round(l.getZ())));
            controller.getConfig().removePlayerFromDeadPlayers(event.getPlayer());
        }
    }
    @EventHandler
    public void onCtrlQCraft(CraftItemEvent event){
        if(!event.getClick().equals(ClickType.CONTROL_DROP)||event.getInventory().getResult()==null)return;
        Player p = (Player) event.getWhoClicked();
        CraftingInventory inv = event.getInventory();
        ItemStack[] matrix = event.getInventory().getMatrix().clone();



//
//        OptionalInt minCrafts_ = Arrays.stream(matrix).filter(Objects::nonNull).map(ItemStack::getAmount).collect(Collectors.toList()).stream().mapToInt(x-> x).min();
//        int minCrafts = minCrafts_.isPresent() ? minCrafts_.getAsInt() : 1;
//
//        ItemStack result = inv.getResult().clone();
//        result.setAmount(result.getAmount()*minCrafts);
//        inv.setResult(result);
//        p.getWorld().dropItem(p.getLocation().add(new Vector(1,1,1)),result);
//
//
//        ArrayList<ItemStack> newMatrix = (ArrayList<ItemStack>) Arrays.stream(matrix).map(x->{
//            if(x==null)return null;
//            ItemStack temp = x.clone();
//            temp.setAmount(temp.getAmount()-minCrafts* Util.countTimesItemStackTypeAppearsInIterable(Arrays.stream(matrix).iterator(),x));
//            return temp;
//        }).collect(Collectors.toList());
//        p.sendMessage(newMatrix.toString());
//        newMatrix.forEach(x->{matrix[newMatrix.indexOf(x)]=x;});
//        ItemStack[] emptyMatrix = new ItemStack[matrix.length];
//        event.getInventory().setMatrix(emptyMatrix);
//        Bukkit.getScheduler().runTaskLater(controller.getMain(),()->{
//            event.getInventory().setMatrix(matrix);
//        },1L);

    }
}