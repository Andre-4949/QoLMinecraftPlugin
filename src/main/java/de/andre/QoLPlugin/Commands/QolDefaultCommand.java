package de.andre.QoLPlugin.Commands;

import de.andre.QoLPlugin.Util;
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
import java.util.Objects;
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
    private final String HELP = "help";
    private final String MODIFYCONFIG = "modifyConfig";
    private final String AMBIENTSPAWNLIMIT = "ambientSpawnLimit";
    private final String ANIMALSPAWNLIMIT = "animalSpawnLimit";
    private final String MONSTERSPAWNLIMIT = "monsterSpawnLimit";
    private final String WATERAMBIENTSPAWNLIMIT = "waterAmbientSpawnLimit";
    private final String WATERANIMALSPAWNLIMIT = "waterAnimalSpawnLimit";
    private final String VIEWDISTANCE = "viewDistance";

    public QolDefaultCommand(PluginController controller) {
        this.controller = controller;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) return true;
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase(RELOADCONFIG)) reloadConfig();
                else if (args[0].equalsIgnoreCase(SAVECONFIG)) saveConfig(sender);
                else if (args[0].equalsIgnoreCase(HELP)) sendHelpMessage(sender);
                break;
            case 2:
                if (args[1].equalsIgnoreCase(LIST)) {
                    if (args[0].equalsIgnoreCase(SIMPLEHARVEST)) listSimpleHarvest(sender);
                    else if (args[0].equalsIgnoreCase(ADVANCEDCOMPOST)) listAdvancedCompost(sender);
                }
                break;
            case 3:
                if (args[0].equalsIgnoreCase(SIMPLEHARVEST)) simpleHarvest(sender, args);
                else if (args[0].equalsIgnoreCase(ADVANCEDCOMPOST)) advancedCompost(sender, args);
                else if (args[0].equalsIgnoreCase(MODIFYCONFIG)) modifyConfig(sender, args);
                break;
            default:
                return false;
        }
        return false;
    }

    private void modifyConfig(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;
        Player p = (Player) sender;
        try {
            if (args[1].equalsIgnoreCase(       AMBIENTSPAWNLIMIT))p.getWorld().setAmbientSpawnLimit(Integer.parseInt(args[2]));
            else if(args[1].equalsIgnoreCase(   ANIMALSPAWNLIMIT))p.getWorld().setAnimalSpawnLimit(Integer.parseInt(args[2]));
            else if(args[1].equalsIgnoreCase(   MONSTERSPAWNLIMIT))p.getWorld().setMonsterSpawnLimit(Integer.parseInt(args[2]));
            else if(args[1].equalsIgnoreCase(   WATERAMBIENTSPAWNLIMIT))p.getWorld().setWaterAmbientSpawnLimit(Integer.parseInt(args[2]));
            else if(args[1].equalsIgnoreCase(   WATERANIMALSPAWNLIMIT))p.getWorld().setWaterAnimalSpawnLimit(Integer.parseInt(args[2]));
            else if(args[1].equalsIgnoreCase(   VIEWDISTANCE))p.getWorld().setViewDistance(Integer.parseInt(args[2]));
        } catch (Exception e) {
            sender.sendMessage(controller.getConfig().getMessages().getGENERALERROR());
            Util.sendWarnLogMessage(controller,e.toString());
        }
    }

    private void saveConfig(CommandSender sender) {
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
            if (removeMaterialFromAdvancedCompost(sender, args) //This returns a boolean if it was successful or not.
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
            controller.getConfig().addAdvancedCompostMaterials(Material.getMaterial(args[2].toUpperCase()));
            return true;
        } catch (Exception e) {
            sender.sendMessage(e.toString());
            return false;
        }
    }

    public void reloadConfig() {
        controller.setConfig(new ConfigController(controller));
    }

    public boolean addMaterialToSimpleHarvest(CommandSender p, String[] args) {
        /*
            Adds a Material to the SimpleHarvest-Material-List and returns a boolean if the operation was successful
            or not.
         */
        try {
            controller.getConfig().addSimpleHarvestMaterials(Material.getMaterial(args[2].toUpperCase()));
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
            controller.getConfig().removeSimpleHarvestMaterials(Material.getMaterial(args[2].toUpperCase()));
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
            controller.getConfig().removeAdvancedCompostMaterials(Material.getMaterial(args[2].toUpperCase()));
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
        completions.add(MODIFYCONFIG);
        return completions;
    }

    public ArrayList<String> onTabCompleteArgsLength2(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        if (args[0].equalsIgnoreCase(SIMPLEHARVEST)) completions.addAll(Arrays.asList(LIST, ADD, REMOVE));
        if (args[0].equalsIgnoreCase(ADVANCEDCOMPOST)) completions.addAll(Arrays.asList(LIST, ADD, REMOVE));
        if (args[0].equalsIgnoreCase(MODIFYCONFIG))completions.addAll(Arrays.asList(AMBIENTSPAWNLIMIT,ANIMALSPAWNLIMIT,MONSTERSPAWNLIMIT,WATERAMBIENTSPAWNLIMIT,WATERANIMALSPAWNLIMIT,VIEWDISTANCE));
        return completions;
    }

    public ArrayList<String> onTabCompleteArgsLength3(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();

        if ((args[0].equalsIgnoreCase(SIMPLEHARVEST) || args[0].equalsIgnoreCase(ADVANCEDCOMPOST)) && args[1].equalsIgnoreCase(ADD) && sender instanceof Player) {
            Player p = (Player) sender;
            completions.add(p.getInventory().getItemInMainHand().getType().toString());
            if (p.getTargetBlock(10) != null) completions.add(Objects.requireNonNull(p.getTargetBlock(10)).getType().toString());
        } else if (args[0].equalsIgnoreCase(SIMPLEHARVEST) && args[1].equalsIgnoreCase(REMOVE))
            controller.getConfig().getSimpleHarvestMaterials().forEach(x -> completions.add(x.toString()));
        else if (args[0].equalsIgnoreCase(ADVANCEDCOMPOST) && args[1].equalsIgnoreCase(REMOVE))
            controller.getConfig().getAdvancedCompostMaterials().forEach(x -> completions.add(x.toString()));

        return completions;
    }

    public void sendHelpMessage(CommandSender sender) {
        String helpMessage =
                "qol: \n" +
                        "    - advancedCompost:\n" +
                        "        - add [MaterialName(Suggestions is the Block you are looking at and the item you are holding)]\n" +
                        "        - list\n" +
                        "        - remove [MaterialName(Suggestions is the Block you are looking at and the item you are holding)]\n" +
                        "    - simpleHarvest:" +
                        "        - add [MaterialName(Suggestions is the Block you are looking at and the item you are holding)]\n" +
                        "        - list\n" +
                        "        - remove [MaterialName(Suggestions is the Block you are looking at and the item you are holding)]\n" +
                        "    - saveConfig: saves the config\n" +
                        "    - reloadConfig: reloads the Config from the config.yml file in the plugins/QoLPlugin folder";
        sender.sendMessage(helpMessage);
    }
}