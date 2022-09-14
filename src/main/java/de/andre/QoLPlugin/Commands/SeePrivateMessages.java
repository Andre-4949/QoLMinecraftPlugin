package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.controller.PluginController;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SeePrivateMessages implements CommandExecutor, TabCompleter {
    private final PluginController controller;

    public SeePrivateMessages(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p))return true;
        if (!p.isOp())return true;
        if (controller.getConfig().getAdminsWhichEnabledPrivateMessageViewing().contains(p)){
            controller.getConfig().removeAdminsWhichEnabledPrivateMessageViewing(p);
            p.sendMessage(Component.text(controller.getConfig().getMessageController().getSERVERPREFIX() + "Private message viewing disabled"));
        } else {
            controller.getConfig().addAdminsWhichEnabledPrivateMessageViewing(p);
            p.sendMessage(Component.text(controller.getConfig().getMessageController().getSERVERPREFIX() + "Private message viewing enabled"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
