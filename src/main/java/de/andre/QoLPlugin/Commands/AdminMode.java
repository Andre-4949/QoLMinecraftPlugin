package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.AdditionalClasses.AdminModeData;
import de.andre.QoLPlugin.Util;
import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.*;

public class AdminMode implements CommandExecutor, TabCompleter {
    private PluginController controller;
    public static int SECONDSTOTICKS = 20;

    public AdminMode(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (!p.isOp()) return true;
        try {
            if (controller.getConfig().getAdminmodeHashmap().containsKey(p)) {
                Location playerLoc = controller.getConfig().getAdminmodeHashmap().get(p).getLoc();
                ItemStack[] playerInv = controller.getConfig().getAdminmodeHashmap().get(p).getInv();

                p.getInventory().setContents(playerInv);
                p.getInventory().setStorageContents(p.getInventory().getStorageContents());
                p.teleport(playerLoc);
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 15 * SECONDSTOTICKS, 5, true));
                p.setVelocity(new Vector());
                p.setGameMode(GameMode.SURVIVAL);
                HashMap<Player, AdminModeData> newAdminModeHashmap = controller.getConfig().getAdminmodeHashmap();
                newAdminModeHashmap.remove(p);
                controller.getConfig().setAdminmodeHashmap(newAdminModeHashmap);
            } else {
                Location playerLoc = p.getLocation();
                ItemStack[] playerInv = p.getInventory().getContents();

                for (int i = 0; i < playerInv.length; i++) {
                    if (playerInv[i] != null) playerInv[i] = playerInv[i].clone();
                }

                AdminModeData playerData = new AdminModeData(playerInv, playerLoc);

                //ArrayList<Object> playerData = new ArrayList<>(Arrays.asList(playerLoc, playerInv));

                controller.getConfig().addAdminModePlayer(p, playerData);

                p.setGameMode(GameMode.CREATIVE);

                p.getInventory().clear();
            }
        } catch (Exception e) {
            HashMap<Player, AdminModeData> adminModeHashmap = controller.getConfig().getAdminmodeHashmap();
            String hashmapEntry = "nothing";

            try {
                if (adminModeHashmap.get(p).getInv() != null &&
                        adminModeHashmap.get(p).getLoc() != null) {
                    hashmapEntry = Arrays.toString(adminModeHashmap.get(p).getInv()) + " " +
                            (adminModeHashmap.get(p).getLoc()).toString();
                }
            } catch (Exception ignored) {
            }

            String information = String.format("Exception: %s | Name: %s | Time: %s | HashmapEntry or null: %s",
                    e.getMessage(), p.getName(), LocalDate.now(), hashmapEntry);

            Util.sendSevereLogMessage(controller, information);
            Util.sendMessageToAdmins(controller, "There was an Error while performing the /adminmode command. Please contact the developers immediately!");

            p.setGameMode(GameMode.CREATIVE);
            p.sendMessage(controller.getConfig().getMessages().getSERVERPREFIX() + "You are now in the creative mode due to an Error. sorry :|");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }

    public static void resetAllAdmins(PluginController controller, Set<Player> players) {
        for (Player p : players) {
            resetOneAdmin(controller, p);
        }
    }

    public static void resetOneAdmin(PluginController controller, Player p) {
        Location playerLoc;
        ItemStack[] playerInv;
        try {
            playerLoc = controller.getConfig().getAdminmodeHashmap().get(p).getLoc();
            playerInv = controller.getConfig().getAdminmodeHashmap().get(p).getInv();
        }catch (NullPointerException e){
            return;
        }
        if (playerInv != null) {
            p.getInventory().setContents(playerInv);
        }
        p.teleport(playerLoc);
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 15, 5, true));
        p.setGameMode(GameMode.SURVIVAL);

        HashMap<Player, AdminModeData> newAdminModeHashmap = controller.getConfig().getAdminmodeHashmap();
        newAdminModeHashmap.remove(p);
        controller.getConfig().setAdminmodeHashmap(newAdminModeHashmap);
    }
}
