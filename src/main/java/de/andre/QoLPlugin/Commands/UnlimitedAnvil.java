package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.Util;
import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnlimitedAnvil implements TabCompleter, CommandExecutor {
    private final PluginController controller;
    private final String TRUE = "true";
    private final String FALSE = "false";
    private final String HELP = "help";

    public UnlimitedAnvil(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.isOp())return true;
        if(args.length<1){
            Util.smartSendMessage(controller, sender, "The value is now set to: " + controller.getConfig().getUnlimitedCost());
            return true;
        }
        switch (args[0]){
            case TRUE:
                controller.getConfig().setUnlimitedCost(true);
                Util.smartSendMessage(controller, sender, "The value is now set to: " + true);
                break;
            case FALSE:
                controller.getConfig().setUnlimitedCost(false);
                Util.smartSendMessage(controller, sender, "The value is now set to: " + false);
                break;
            case HELP:
                Util.smartSendMessage(controller,sender,"This command enables/disables the 'too expensive'-function while combining items which have more anvil uses. (the text in the anvil still remains)");
                break;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>(Arrays.asList(TRUE,FALSE,HELP));
    }
}
