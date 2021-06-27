package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.controller.ConfigController;
import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QolDefaultCommand implements CommandExecutor, TabCompleter {
    private final PluginController controller;

    private final String RELOADCONFIG = "reloadconfig";
    private final String ADVANCEDCOMPOST = "advancedCompost";
    private final String SIMPLEHARVEST = "simpleHarvest";
    private final String ADD = "add";
    private final String REMOVE = "remove";
    private final String LIST = "list";

    public QolDefaultCommand(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return false;
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase(RELOADCONFIG)) {
                    reloadConfig();
                }
                break;
            case 2:
                if(args[0].equalsIgnoreCase(SIMPLEHARVEST) && args[1].equalsIgnoreCase(LIST)){
                    sender.sendMessage(controller.getConfig().getSimpleHarvestMaterials().stream().map(Enum::toString).collect(Collectors.toList()).toString());
                }
            case 3:
                if (args[0].equalsIgnoreCase(SIMPLEHARVEST) && args[1].equalsIgnoreCase(ADD)) {
                    if (addMaterialToSimpleHarvest(sender, args) //This returns a boolean if it was successful or not.
                    ) {
                        sender.sendMessage(controller.getConfig().getMessages().getServerPrefix() + "Material added successfully.");
                    } else {
                        sender.sendMessage(controller.getConfig().getMessages().getErrorAddMaterial());
                    }
                } else if (args[0].equalsIgnoreCase(SIMPLEHARVEST) && args[1].equalsIgnoreCase(REMOVE)) {
                    if (removeMaterialToSimpleHarvest(sender, args) //This returns a boolean if it was successful or not.
                    ) {
                        sender.sendMessage(controller.getConfig().getMessages().getServerPrefix() + "Material removed successfully.");
                    } else {
                        sender.sendMessage(controller.getConfig().getMessages().getErrorAddMaterial());
                    }
                }
                break;
            default:
                return false;
        }
        return false;
    }


    public void reloadConfig() {
        ConfigController newConfigController = new ConfigController(controller);
        controller.setConfig(newConfigController);
        newConfigController.onEnable();
    }

    public boolean addMaterialToSimpleHarvest(CommandSender p, String[] args) {
        /*
            Adds a Material to the SimpleHarvest-Material-List and returns a boolean if the operation was successful
            or not.
         */
        try {
            List<Material> simpleHarvestMaterials = controller.getConfig().getSimpleHarvestMaterials();
            simpleHarvestMaterials.add(Material.getMaterial(args[2].toUpperCase()));
            controller.getConfig().setSimpleHarvestMaterials(simpleHarvestMaterials);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean removeMaterialToSimpleHarvest(CommandSender p, String[] args) {
        /*
            Adds a Material to the SimpleHarvest-Material-List and returns a boolean if the operation was successful
            or not.
         */
        try {
            List<Material> simpleHarvestMaterials = controller.getConfig().getSimpleHarvestMaterials();
            simpleHarvestMaterials.remove(Material.getMaterial(args[2].toUpperCase()));
            controller.getConfig().setSimpleHarvestMaterials(simpleHarvestMaterials);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        switch (args.length) {
            case 0:
                return onTabCompleteArgsLength0(sender, command, alias, args);
            case 1:
                return onTabCompleteArgsLength1(sender, command, alias, args);
        }
        return completions;
    }

    public ArrayList<String> onTabCompleteArgsLength0(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        completions.add(RELOADCONFIG);
        return completions;
    }

    public ArrayList<String> onTabCompleteArgsLength1(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();

        return completions;
    }
}
