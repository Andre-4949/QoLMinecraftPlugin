package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.controller.PluginController;
import de.andre.QoLPlugin.listener.VineMiner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VineMinerCommand implements CommandExecutor, TabCompleter {
    private final PluginController controller;
    private static final ArrayList<String> OPTIONS = new ArrayList<>(Arrays.asList("on", "off"));

    public VineMinerCommand(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))return true;
        Player p = (Player) sender;
        VineMiner vm = (VineMiner) controller.getListenerController().getListener(VineMiner.class);
        if (args[0].equals("on")){
            vm.addPlayerToActiveVineMiner(p);
            return true;
        }
        vm.removePlayerFromActiveVineMiner(p);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length==1) return OPTIONS;
        return new ArrayList<>();
    }
}
