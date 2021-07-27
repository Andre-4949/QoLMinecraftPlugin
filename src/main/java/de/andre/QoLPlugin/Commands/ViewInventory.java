package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewInventory implements CommandExecutor, TabCompleter {
    private PluginController controller;
    public ViewInventory(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)||!sender.isOp()||args.length>1)return true;
        Player p = (Player) sender;
        Player targetPlayer;
        if(Bukkit.getPlayer(args[0])!=null)targetPlayer = Bukkit.getPlayer(args[0]);
        else{return true;}
        assert targetPlayer != null;
        Inventory targetPlayerInventory = targetPlayer.getInventory();
        p.openInventory(targetPlayerInventory);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        if(args.length==0)completions.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList()));
        return completions;
    }
}
