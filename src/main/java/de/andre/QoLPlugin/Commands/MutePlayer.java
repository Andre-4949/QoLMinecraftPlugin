package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MutePlayer implements TabCompleter, CommandExecutor {
    private PluginController controller;
    private final String LIST = "list";

    public MutePlayer(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;
        if (!controller.getConfig().isMutedPlayersEnabled())return true;
        if (sender instanceof Player p && args.length == 1) {

            if (args[0].equalsIgnoreCase(LIST)) {
                sender.sendMessage(controller.getConfig().getMutedPlayers().toString());
                return true;
            }

            Player targetPlayer;

            try {
                targetPlayer = Bukkit.getPlayer(args[0]);
            } catch (Exception e) {
                sender.sendMessage(controller.getConfig().getMessageController().getGENERALERROR());
                return true;
            }
            assert targetPlayer != null;
            if (targetPlayer.isOp()) {
                sender.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "You can't mute someone with equal rights as you.");
                return true;
            } else if (targetPlayer.equals(p)) {
                sender.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "You can't mute yourself.");
            }

            if (!controller.getConfig().getMutedPlayers().contains(targetPlayer)) {
                controller.getConfig().getMutedPlayers().add(targetPlayer);
                sender.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "This player is now muted until you unmute him.");
            } else {
                controller.getConfig().getMutedPlayers().remove(targetPlayer);
                sender.sendMessage(controller.getConfig().getMessageController().getSERVERPREFIX() + "This player is now unmuted until you mute him.");
            }


        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> completions = new ArrayList<>() {{
            add(LIST);
        }};
        if (args.length == 1) {
            Bukkit.getOnlinePlayers().forEach(x -> completions.add(x.getName()));
        }
        return completions;
    }
}
