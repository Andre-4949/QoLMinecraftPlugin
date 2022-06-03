package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BetterCommands implements CommandExecutor, TabCompleter {
    private PluginController controller;

    public BetterCommands(PluginController controller) {
        this.controller = controller;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.isOp())return true;
        try {
            ArrayList<Integer> seperatorindices = new ArrayList<>();

            List<String> argsList = Arrays.stream(args).collect(Collectors.toList());

            argsList.forEach(x->{
                if(x.equalsIgnoreCase("|"))seperatorindices.add(argsList.indexOf(x));
            });
            StringBuilder cmd = new StringBuilder();

            for(int i=0;i< seperatorindices.get(0);i++){
                cmd.append(args[i]).append(" ");
            }

            ArrayList<String> parameters = new ArrayList<>(Arrays.asList(args).subList(seperatorindices.get(0)+1, args.length));

            String finalCmd = cmd.toString();
            parameters.forEach(x -> {
                try {
                    Bukkit.dispatchCommand(sender, finalCmd + x);
                } catch (CommandException e){
                    sender.sendMessage(String.format("Parameter: %s | Exception: %s",x,e.getMessage()));
                }
            });

        } catch (Exception e) {
            sender.sendMessage(controller.getConfig().getMessageController().getGENERALERROR());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("enter Command or seperator ('|')");
        }else{
            completions.add("enter paramters");
        }

        return completions;
    }
}
