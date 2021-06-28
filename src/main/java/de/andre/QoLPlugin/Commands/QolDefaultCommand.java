package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.controller.ConfigController;
import de.andre.QoLPlugin.controller.PluginController;
import org.bukkit.Material;
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
import java.util.stream.Collectors;

public class QolDefaultCommand implements CommandExecutor, TabCompleter {
    private final PluginController controller;

    private final String RELOADCONFIG = "reloadconfig";
    private final String ADVANCEDCOMPOST = "advancedCompost";
    private final String SIMPLEHARVEST = "simpleHarvest";
    private final String ADD = "add";
    private final String REMOVE = "remove";
    private final String LIST = "list";
    private final String SAVECONFIG = "saveConfig";

    public QolDefaultCommand(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return false;
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase(RELOADCONFIG)) reloadConfig();
                if (args[0].equalsIgnoreCase(SAVECONFIG)) saveConfig(sender, args);
                break;
            case 2:
                if (args[1].equalsIgnoreCase(LIST)) {
                    if (args[0].equalsIgnoreCase(SIMPLEHARVEST)) listSimpleHarvest(sender);
                    if (args[0].equalsIgnoreCase(ADVANCEDCOMPOST)) listAdvancedCompost(sender);
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase(SIMPLEHARVEST)) simpleHarvest(sender, args);
                if (args[0].equalsIgnoreCase(ADVANCEDCOMPOST)) advancedCompost(sender, args);
                break;
            default:
                return false;
        }
        return false;
    }

    private void saveConfig(CommandSender sender, String args[]) {
        controller.getConfig().saveConfig();
        if (sender instanceof Player) sender.sendMessage(controller.getConfig().getMessages().getSAVECONFIGINGAME());
        else sender.sendMessage(controller.getConfig().getMessages().getSAVECONFIGCONSOLE());
    }


    private void listSimpleHarvest(CommandSender sender) {
        sender.sendMessage(controller.getConfig().getSimpleHarvestMaterials().stream().map(Enum::toString).collect(Collectors.toList()).toString());
    }

    private void listAdvancedCompost(CommandSender sender) {
        sender.sendMessage(controller.getConfig().getAdvancedCompostMaterials().stream().map(Enum::toString).collect(Collectors.toList()).toString());
    }

    private void simpleHarvest(CommandSender sender, String[] args) {
        if (args[1].equalsIgnoreCase(ADD)) {
            if (addMaterialToSimpleHarvest(sender, args) //This returns a boolean if it was successful or not.
            ) {
                sender.sendMessage(controller.getConfig().getMessages().getMATERIALADDEDSUCCESS());
            } else {
                sender.sendMessage(controller.getConfig().getMessages().getERRORADDMATERIAL());
            }
        } else if (args[1].equalsIgnoreCase(REMOVE)) {
            if (removeMaterialFromSimpleHarvest(sender, args) //This returns a boolean if it was successful or not.
            ) {
                sender.sendMessage(controller.getConfig().getMessages().getMATERIALREMOVEDSUCCESS());
            } else {
                sender.sendMessage(controller.getConfig().getMessages().getERRORADDMATERIAL());
            }
        }
    }

    private void advancedCompost(CommandSender sender, String[] args) {
        if (args[1].equalsIgnoreCase(ADD)) {
            if (addMaterialToAdvancedCompost(sender, args) //This returns a boolean if it was successful or not.
            ) {
                sender.sendMessage(controller.getConfig().getMessages().getMATERIALADDEDSUCCESS());
            } else {
                sender.sendMessage(controller.getConfig().getMessages().getERRORADDMATERIAL());
            }
        } else if (args[1].equalsIgnoreCase(REMOVE)) {
            if (removeMaterialFromSimpleHarvest(sender, args) //This returns a boolean if it was successful or not.
            ) {
                sender.sendMessage(controller.getConfig().getMessages().getMATERIALREMOVEDSUCCESS());
            } else {
                sender.sendMessage(controller.getConfig().getMessages().getERRORADDMATERIAL());
            }
        }
    }

    private boolean addMaterialToAdvancedCompost(CommandSender sender, String[] args) {
        /*
            Adds a Material to the AdvancedCompost-Material-List and returns a boolean if the operation was successful
            or not.
         */
            try {
                List<Material> advancedCompost = controller.getConfig().getAdvancedCompostMaterials();
                advancedCompost.add(Material.getMaterial(args[2].toUpperCase()));
                controller.getConfig().setAdvancedCompostMaterials(advancedCompost);
                return true;
            } catch (Exception e) {
                return false;
            }

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

    public boolean removeMaterialFromSimpleHarvest(CommandSender p, String[] args) {
        /*
            Removes a Material from the SimpleHarvest-Material-List and returns a boolean if the operation was successful
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

    public boolean removeMaterialFromAdvancedCompost(CommandSender p, String[] args) {
        /*
            Removes a Material from the AdvancedCompost-Material-List and returns a boolean if the operation was successful
            or not.
         */
        try {
            List<Material> advancedCompost = controller.getConfig().getAdvancedCompostMaterials();
            advancedCompost.remove(Material.getMaterial(args[2].toUpperCase()));
            controller.getConfig().setAdvancedCompostMaterials(advancedCompost);
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
            case 1:
                return onTabCompleteArgsLength1(sender, command, alias, args);
            case 2:
                return onTabCompleteArgsLength2(sender, command, alias, args);
            case 3:
                return onTabCompleteArgsLength3(sender, command, alias, args);
        }
        return completions;
    }

    public ArrayList<String> onTabCompleteArgsLength1(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        completions.add(RELOADCONFIG);
        completions.add(SAVECONFIG);
        completions.add(SIMPLEHARVEST);
        completions.add(ADVANCEDCOMPOST);
        return completions;
    }

    public ArrayList<String> onTabCompleteArgsLength2(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        if (args[0].equalsIgnoreCase(SIMPLEHARVEST)) completions.addAll(Arrays.asList(LIST, ADD, REMOVE));
        if (args[0].equalsIgnoreCase(ADVANCEDCOMPOST)) completions.addAll(Arrays.asList(LIST, ADD, REMOVE));
        return completions;
    }

    public ArrayList<String> onTabCompleteArgsLength3(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        if (args[0].equalsIgnoreCase(SIMPLEHARVEST) && args[1].equalsIgnoreCase(REMOVE))
            controller.getConfig().getSimpleHarvestMaterials().forEach(x -> completions.add(x.toString()));
        if (args[0].equalsIgnoreCase(ADVANCEDCOMPOST) && args[1].equalsIgnoreCase(REMOVE))
            controller.getConfig().getAdvancedCompostMaterials().forEach(x -> completions.add(x.toString()));

        return completions;
    }
}
