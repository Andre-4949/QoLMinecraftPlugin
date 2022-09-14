package de.andre.QoLPlugin.listener;

import de.andre.QoLPlugin.Commands.AdminMode;
import de.andre.QoLPlugin.Util;
import de.andre.QoLPlugin.controller.ConfigController;
import de.andre.QoLPlugin.controller.PluginController;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerListener implements QoLListener {
    private final PluginController controller;
    private static final ArrayList<String> messageCommands = new ArrayList<>() {{
        add("msg");
        add("w");
        add("tell");
    }};

    public PlayerListener(PluginController controller) {
        this.controller = controller;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        if (controller.getConfig().getMutedPlayers().contains(event.getPlayer()) && event.viewers().containsAll(Bukkit.getOnlinePlayers()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().split(" ")[0].replace("/", "");
        if (!messageCommands.contains(cmd)) return;
        String[] commandSections = event.getMessage().split(" ", 3);
        if (commandSections.length > 2)
            Bukkit.getOnlinePlayers().stream()
                    .filter(ServerOperator::isOp)
                    .filter(x -> controller.getConfig().getAdminsWhichEnabledPrivateMessageViewing().contains(x))
                    .forEach(x -> x.sendMessage(controller.getConfig().getMessageController().getPRIVATEMESSAGEFROMONEPLAYERTOANOTHER(
                            event.getPlayer(),
                            event.getMessage().split(" ")[1],
                            commandSections[2])));
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        if (controller.getConfig().isSendMessageOnGamemodeChange() && !event.getCause().equals(PlayerGameModeChangeEvent.Cause.PLUGIN) && event.getNewGameMode().equals(GameMode.CREATIVE) && !controller.getConfig().getAdminmodeHashmap().containsKey(event.getPlayer()))
            event.getPlayer().sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "Please think about using the /adminmode command");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (!event.getPlayer().isOp() && !controller.getConfig().getAdminmodeHashmap().containsKey(event.getPlayer()))
            return;
        AdminMode.resetOneAdmin(controller, event.getPlayer());
    }

    @EventHandler
    public void onPlayerDied(PlayerDeathEvent event) {
        Location loc = event.getEntity().getWorld().getHighestBlockAt(event.getEntity().getLocation()).getLocation();
        if (event.getEntity().getWorld().getEnvironment().equals(World.Environment.NETHER) && loc.getY() > 128) {
            for (int i = 128; i > 0; i--) {
                loc.setY(i);
                if (loc.getBlock().isSolid()) {
                    loc.add(0, 1, 0);
                    break;
                }
            }
        }
        ArrayList<ItemStack> remainingItems = new ArrayList<>();

        do {
            Block locBlock = loc.add(0, 1, 0).getBlock();
            locBlock.setType(Material.CHEST);
            Chest chest = (Chest) locBlock.getState();
            ArrayList<ItemStack> drops = new ArrayList<>(event.getDrops());
            for (ItemStack drop : drops) {
                remainingItems.addAll(chest.getBlockInventory().addItem(drop).values());
            }
        } while (remainingItems.size()>0);

        event.getDrops().forEach(x -> x.setType(Material.AIR));
        Player p = event.getPlayer();
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
        p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + String.format("You have now equipped your %s.", newChestPiece.getType().toString().toLowerCase()));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (controller.getConfig().isSendCoordsOfDeathOnRespawn() && controller.getConfig().getDeadPlayersHashMap().containsKey(event.getPlayer())) {
            Location l = controller.getConfig().getDeadPlayersHashMap().get(event.getPlayer());
            event.getPlayer().sendMessage(String.format("You died in world: %s | x: %s | y: %s | z: %s",
                    l.getWorld().getName(), Math.round(l.getX()), Math.round(l.getY()), Math.round(l.getZ())));
            controller.getConfig().removePlayerFromDeadPlayers(event.getPlayer());
        }
    }

    @EventHandler
    public void onCtrlQCraft(CraftItemEvent event) {
        if (!event.getClick().equals(ClickType.CONTROL_DROP) || event.getInventory().getResult() == null || !controller.getConfig().isCtrlQCraftEnabled())
            return;

        //initialize some important variables
        Player p = (Player) event.getWhoClicked();
        Recipe r = event.getInventory().getRecipe();
        ArrayList<ItemStack> correctedIngredientList = new ArrayList<>();
        ArrayList<ItemStack> ingredientList;
        int choiceLength;

        // set the values based on what recipe we deal with
        if (r instanceof ShapelessRecipe shapelessRecipe) {
            ingredientList = new ArrayList<>(shapelessRecipe.getIngredientList());
            choiceLength = shapelessRecipe.getChoiceList().size();
        } else if (r instanceof ShapedRecipe shapedRecipe) {
            ingredientList = new ArrayList<>(shapedRecipe.getIngredientMap().values());
            choiceLength = shapedRecipe.getChoiceMap().values().size();
        } else {
            p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "oops something went wrong");
            return;//if some other recipe event has been triggered; I haven't tested it but maybe in the future they might trigger this event
        }

        if (choiceLength != 1) //This was a pain in the a..
            /*
             * let's start by just trying to craft sticks, the ingredientList will contain oak planks even
             * if you try to craft the sticks with spruce planks, that's why the ingredientList has to be corrected
             * to contain the same material as the crafting matrix.
             *
             * I implemented a method called iterateParallel to iterate through two iterator objects at the same time,
             * because the slots mustn't get messed up. I tried with brewing stands and cobblestone, blackstone, and it
             * didn't work like I intended it to work
             * */
            Util.iterateParallel(
                    Arrays.stream(event.getInventory().getMatrix()).iterator(),
                    ingredientList.iterator(),
                    (matrixEntry, ingredient) -> {
                        if (matrixEntry != null) {
                            if (matrixEntry.getType() != ingredient.getType()) {
                                ingredient.setType(matrixEntry.getType());
                            }
                            correctedIngredientList.add(ingredient);
                        }
                    }
            );

        if (correctedIngredientList.size() == 0)
            correctedIngredientList.addAll(ingredientList);// if there is just one choice lets fill the list with the ingredientList
        ArrayList<ItemStack> ingredientRatio = Util.simplifyItemStackList(correctedIngredientList);
        p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "CtrlQCrafting...");


        int totalAmountOfItemsCrafted = 0;
        int amountOfResults;

        //as long as the player has enough items let's craft
        while (ingredientRatio.stream().allMatch(ingredient -> p.getInventory().containsAtLeast(ingredient, ingredient.getAmount()))) {

            /*
             * for params:
             *   1. reset the amountOfResults to 0 - should be clear
             *   2. check if ...
             *       2.1 we still got space until we hit the MaxStackSize#
             *       2.2 the inventory still has enough items for another crafting cycle
             * */
            for (amountOfResults = 0;
                 (
                         (amountOfResults + 1) * r.getResult().getAmount()) < r.getResult().getMaxStackSize() &&
                         ingredientRatio.stream().allMatch(ingredient -> p.getInventory().containsAtLeast(ingredient, ingredient.getAmount()));
                 amountOfResults++) {
                ItemStack[] contents = Util.removeItems(p.getInventory().getContents(), ingredientRatio);

                if (p.getInventory().getContents() == contents) { //this should never happen but let's be safe
                    break;
                }
                p.getInventory().setContents(contents);
                p.updateInventory();
            }

            ItemStack result = r.getResult().clone();
            amountOfResults *= r.getResult().getAmount();

            result.setAmount(amountOfResults);//example: *4 | planks or *16 | ironbars

            totalAmountOfItemsCrafted += amountOfResults;

            Block targetBlock = p.getTargetBlock(5);
            Vector itemVector = targetBlock != null ? targetBlock.getLocation().toVector().add(p.getEyeLocation().add(0, -0.1, 0).toVector().multiply(-1)) : new Vector(0, 0, 0);
            itemVector.normalize();

            p.getWorld().dropItem(p.getEyeLocation(), result, (f) -> {
                f.setVelocity(itemVector.multiply(0.5));
                f.setPickupDelay(ConfigController.SECONDSTOTICKS*3);
            });
//            p.getWorld().dropItem(p.getLocation().add(p.getFacing().getDirection()),result);
            p.updateInventory();
        }

        p.setStatistic(Statistic.CRAFT_ITEM, r.getResult().getType(),
                p.getStatistic(Statistic.CRAFT_ITEM, r.getResult().getType()) + totalAmountOfItemsCrafted);
    }

    @EventHandler
    public void onTotemActivation(EntityResurrectEvent event) {
        if (!(event.getEntityType().equals(EntityType.PLAYER)) || event.isCancelled()) return;

        Player p = (Player) event.getEntity();
        ItemStack item = p.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) ? p.getInventory().getItemInMainHand() : p.getInventory().getItemInOffHand();

        if (p.getInventory().contains(Material.TOTEM_OF_UNDYING) && p.getInventory().containsAtLeast(new ItemStack(Material.TOTEM_OF_UNDYING), 2)) {
            ItemStack[] contents = p.getInventory().getContents();
            p.getInventory().setContents(Util.removeAmount(contents, Material.TOTEM_OF_UNDYING, 2));
            p.updateInventory();
            if (p.getInventory().getItemInOffHand().equals(item)) {
                p.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
            } else {
                p.getInventory().setItemInMainHand(new ItemStack(Material.TOTEM_OF_UNDYING));
            }
            p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "A new Totem from your Inventory was placed in your hand.");
            return;
        }

        if (p.getEnderChest().contains(Material.TOTEM_OF_UNDYING) && p.getEnderChest().containsAtLeast(new ItemStack(Material.TOTEM_OF_UNDYING), 2)) {
            ItemStack[] contents = p.getEnderChest().getContents();
            p.getEnderChest().setContents(Util.removeAmount(contents, Material.TOTEM_OF_UNDYING, 2));
            p.updateInventory();
            if (p.getInventory().getItemInOffHand().equals(item)) {
                p.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
            } else {
                p.getInventory().setItemInMainHand(new ItemStack(Material.TOTEM_OF_UNDYING));
            }
            p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "A new Totem from your Enderchest was placed in your hand.");
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
                        Material.RED_SHULKER_BOX,
                        Material.BLACK_SHULKER_BOX
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
                    p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "A Totem from one of your shulkerboxes was placed in your hand, because you used the one you hand.");
                    if (p.getInventory().getItemInOffHand().equals(item))
                        p.getInventory().setItemInOffHand(new ItemStack(Material.TOTEM_OF_UNDYING));
                    else
                        p.getInventory().setItemInMainHand(new ItemStack(Material.TOTEM_OF_UNDYING));
                    break;
                }
            }
        }


    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) &&
                e.getClickedBlock() != null &&
                e.getClickedBlock().getType() == Material.LADDER &&
                p.isSneaking() && controller.getConfig().isFastLadderClimbingEnabled()) {


            Location l = e.getClickedBlock().getLocation();
            if (!Util.getNeighbouringBlocks(p.getLocation().getBlock()).contains(l.getBlock())) return;

            boolean down = false;

            do {
                if (p.getLocation().getPitch() < 0)
                    l.setY(l.getY() + 1D);
                else {
                    l.setY(l.getY() - 1D);
                    down = true;
                }
            } while (l.getBlock().getType() == Material.LADDER);

            if (down) l.setY(l.getY() + 1);

            Location teleportLocation = l.clone();
            teleportLocation.add(0.5D, 0.1D, 0.5D);
            teleportLocation.setPitch(p.getLocation().getPitch());
            teleportLocation.setYaw(p.getLocation().getYaw());
            p.teleport(teleportLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        Inventory inv = p.getInventory();
        if (event.getFoodLevel() > 18) return;
        ItemStack[] content = inv.getContents();

        boolean eaten = false;

        for (int i = 0; i < content.length; i++) {
            ItemStack itemStack = content[i];
            if (itemStack == null) continue;
            if (itemStack.getType().isEdible() && p.getFoodLevel() < 20) {
                switch (itemStack.getType()) {
                    case PUFFERFISH, TROPICAL_FISH -> {
                        p.setFoodLevel(p.getFoodLevel() + 1);
                        p.setSaturation(p.getSaturation() + 0.2f);
                    }
                    case COOKIE, GLOW_BERRIES, COD, SALMON, SWEET_BERRIES -> {
                        p.setFoodLevel(p.getFoodLevel() + 2);
                        p.setSaturation(p.getSaturation() + 0.4f);
                    }
                    case DRIED_KELP, POTATO -> {
                        p.setFoodLevel(p.getFoodLevel() + 1);
                        p.setSaturation(p.getSaturation() + 0.6f);
                    }
                    case ROTTEN_FLESH -> {
                        p.setFoodLevel(p.getFoodLevel() + 4);
                        p.setSaturation(p.getSaturation() + 0.8f);
                    }
                    case BEETROOT -> {
                        p.setFoodLevel(p.getFoodLevel() + 1);
                        p.setSaturation(p.getSaturation() + 1.2f);
                    }
                    case HONEY_BOTTLE -> {
                        p.setFoodLevel(p.getFoodLevel() + 6);
                        p.setSaturation(p.getSaturation() + 1.2f);
                    }
                    case MELON_SLICE, POISONOUS_POTATO, CHICKEN, MUTTON -> {
                        p.setFoodLevel(p.getFoodLevel() + 2);
                        p.setSaturation(p.getSaturation() + 1.2f);
                    }
                    case BEEF, PORKCHOP, RABBIT -> {
                        p.setFoodLevel(p.getFoodLevel() + 3);
                        p.setSaturation(p.getSaturation() + 1.8f);
                    }
                    case APPLE, CHORUS_FRUIT -> {
                        p.setFoodLevel(p.getFoodLevel() + 4);
                        p.setSaturation(p.getSaturation() + 2.4f);
                    }
                    case SPIDER_EYE -> {
                        p.setFoodLevel(p.getFoodLevel() + 2);
                        p.setSaturation(p.getSaturation() + 3.2f);
                    }
                    case CARROT -> {
                        p.setFoodLevel(p.getFoodLevel() + 3);
                        p.setSaturation(p.getSaturation() + 3.6f);
                    }
                    case PUMPKIN_PIE -> {
                        p.setFoodLevel(p.getFoodLevel() + 8);
                        p.setSaturation(p.getSaturation() + 4.8f);
                    }
                    case BAKED_POTATO, BREAD, COOKED_COD, COOKED_RABBIT -> {
                        p.setFoodLevel(p.getFoodLevel() + 5);
                        p.setSaturation(p.getSaturation() + 6f);
                    }
                    case BEETROOT_SOUP, COOKED_CHICKEN, MUSHROOM_STEW, SUSPICIOUS_STEW -> {
                        p.setFoodLevel(p.getFoodLevel() + 6);
                        p.setSaturation(p.getSaturation() + 7.2f);
                    }
                    case COOKED_MUTTON, COOKED_SALMON -> {
                        p.setFoodLevel(p.getFoodLevel() + 6);
                        p.setSaturation(p.getSaturation() + 9.6f);
                    }
                    case GOLDEN_APPLE -> {
                        p.setFoodLevel(p.getFoodLevel() + 4);
                        p.setSaturation(p.getSaturation() + 9.6f);
                    }
                    case RABBIT_STEW -> {
                        p.setFoodLevel(p.getFoodLevel() + 10);
                        p.setSaturation(p.getSaturation() + 12f);
                    }
                    case COOKED_PORKCHOP, COOKED_BEEF -> {
                        p.setFoodLevel(p.getFoodLevel() + 8);
                        p.setSaturation(p.getSaturation() + 12.8f);
                    }
                    case GOLDEN_CARROT -> {
                        p.setFoodLevel(p.getFoodLevel() + 6);
                        p.setSaturation(p.getSaturation() + 14.4f);
                    }
                }
                p.sendHealthUpdate();
                p.updateCommands();

                p.getInventory().setContents(Util.removeAmount(content, itemStack.getType(), 1));
                p.playSound(p, Sound.ENTITY_GENERIC_EAT, 100, 1);
                p.setStatistic(Statistic.USE_ITEM, itemStack.getType(), p.getStatistic(Statistic.USE_ITEM, itemStack.getType()) + 1);

                p.updateInventory();
                event.setCancelled(true);
                i = 0;
                eaten = true;
            }
        }
        if (eaten){
            p.setSaturatedRegenRate(5);
            Bukkit.getScheduler().runTaskLater(controller.getMain(),()-> p.setSaturatedRegenRate(10),3*ConfigController.SECONDSTOTICKS);
        }
    }
}