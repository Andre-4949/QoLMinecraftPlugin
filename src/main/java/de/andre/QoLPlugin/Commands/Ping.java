package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Ping implements CommandExecutor, TabCompleter {
    private final PluginController controller;

    public Ping(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return true;
        p.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "ping: " + p.getPing() + "ms");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
