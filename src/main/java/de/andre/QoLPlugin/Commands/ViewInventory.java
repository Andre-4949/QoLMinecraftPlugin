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

public class ViewInventory implements CommandExecutor, TabCompleter {
    private PluginController controller;
    public ViewInventory(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p) || !sender.isOp() || args.length > 1) return true;

        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null) return true;

        Inventory targetPlayerInventory = targetPlayer.getInventory();
        p.openInventory(targetPlayerInventory);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        if(args.length==1)Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).forEach(completions::add);
        return completions;
    }
}
