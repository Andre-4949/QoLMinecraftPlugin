package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.Commands.AdminMode;
import de.andre.QoLPlugin.Util;
import de.andre.QoLPlugin.controller.PluginController;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.permissions.ServerOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
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
        Block locBlock = loc.getWorld().getBlockAt(loc);
        locBlock.setType(Material.CHEST);
        Chest chest = (Chest) locBlock.getState();
        chest.getBlockInventory().setContents(event.getDrops().toArray(new ItemStack[0]));
        event.getDrops().forEach(x -> x.setType(Material.AIR));
        Player p = event.getEntity();
        controller.getConfig().addPlayerToDeadPlayers(p, loc);
    }

    @EventHandler
    public void equipElytra(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        if (Stream.of(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE, Material.ELYTRA).noneMatch(x -> event.getItem().getType().equals(x))) {
            return;
        }
        Player p = event.getPlayer();

        if (p.getInventory().getChestplate() == null) return;
        ItemStack currentChestPiece = p.getInventory().getChestplate();

        ItemStack newChestPiece = p.getInventory().getItemInMainHand();
        p.getInventory().setChestplate(newChestPiece.clone());
        p.getInventory().setItemInMainHand(currentChestPiece.clone());
        p.updateInventory();
        p.sendMessage(controller.getConfig().getMessages().getSERVERPREFIX() + String.format("You have now equipped your %s.", newChestPiece.getType().toString().toLowerCase()));
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

    @EventHandler
    public void onTotemActivation(EntityResurrectEvent event) {
        if (!(event.getEntityType().equals(EntityType.PLAYER)) || event.isCancelled()) return;

        Player p = (Player) event.getEntity();
        ItemStack item = p.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) ? p.getInventory().getItemInMainHand() : p.getInventory().getItemInOffHand();

        if (p.getInventory().contains(Material.TOTEM_OF_UNDYING) && Util.countItems(p.getInventory().getContents(), Material.TOTEM_OF_UNDYING) >= 2) {
            ItemStack[] contents = p.getInventory().getContents();
            p.getInventory().setContents(Util.removeAmount(contents, Material.TOTEM_OF_UNDYING, 2));
            p.updateInventory();
            if (p.getInventory().getItemInOffHand().equals(item)) {
                p.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
            } else {
                p.getInventory().setItemInMainHand(new ItemStack(Material.TOTEM_OF_UNDYING));
            }
            p.sendMessage("A new Totem from your Inventory was placed in your hand.");
            return;
        }

        ArrayList<Material> shulkerBoxTypes = new ArrayList<>(
                Arrays.asList(
                        Material.SHULKER_BOX,
                        Material.WHITE_SHULKER_BOX,
                        Material.ORANGE_SHULKER_BOX,
                        Material.MAGENTA_SHULKER_BOX,
                        Material.LIGHT_BLUE_SHULKER_BOX,
                        Material.YELLOW_SHULKER_BOX,
                        Material.LIME_SHULKER_BOX,
                        Material.PINK_SHULKER_BOX,
                        Material.GRAY_SHULKER_BOX,
                        Material.LIGHT_GRAY_SHULKER_BOX,
                        Material.CYAN_SHULKER_BOX,
                        Material.PURPLE_SHULKER_BOX,
                        Material.BLUE_SHULKER_BOX,
                        Material.BROWN_SHULKER_BOX,
                        Material.GREEN_SHULKER_BOX,
                        Material.RED_SHULKER_BOX
                )
        );

        /*
         * shulkerbox checking
         * */
        Stream<ItemStack> invStream = Arrays.stream(p.getInventory().getContents()).filter(Objects::nonNull);
        if (invStream.anyMatch(x -> shulkerBoxTypes.contains(x.getType()))) {
            ArrayList<ItemStack> shulkerboxes = Arrays.stream(p.getInventory().getContents())
                    .filter(Objects::nonNull)
                    .filter(x -> shulkerBoxTypes.contains(x.getType()))
                    .collect(Collectors.toCollection(ArrayList::new));


            //checking the enderchest but adding them after the inventory shulkerboxes, because the shulkerboxes in the inventory should be prioritized
            Stream<ItemStack> enderchestStream = Arrays.stream(p.getEnderChest().getContents()).filter(Objects::nonNull);
            if (enderchestStream.anyMatch(x -> shulkerBoxTypes.contains(x.getType()))) {
                shulkerboxes.addAll(Arrays.stream(p.getEnderChest().getContents())
                        .filter(Objects::nonNull)
                        .filter(x -> shulkerBoxTypes.contains(x.getType()))
                        .collect(Collectors.toCollection(ArrayList::new)));
            }

            for (ItemStack shulkerbox : shulkerboxes) {

                BlockStateMeta shulkerboxMeta = (BlockStateMeta) shulkerbox.getItemMeta();
                ShulkerBox shulkerBox = (ShulkerBox) shulkerboxMeta.getBlockState();

                if (shulkerBox.getInventory().contains(Material.TOTEM_OF_UNDYING)) {

                    shulkerBox.getInventory().setContents(Util.removeAmount(shulkerBox.getInventory().getContents(), Material.TOTEM_OF_UNDYING, 1));

                    shulkerboxMeta.setBlockState(shulkerBox);
                    shulkerbox.setItemMeta(shulkerboxMeta);

                    Bukkit.getLogger().info(Arrays.toString(shulkerBox.getInventory().getContents()));

                    if (p.getInventory().getItemInOffHand().equals(item))
                        p.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
                    else
                        p.getInventory().setItemInMainHand(new ItemStack(Material.TOTEM_OF_UNDYING));
                    break;
                }
            }
        }


    }
}