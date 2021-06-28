package de.andre.QoLPlugin.controller;

import de.andre.QoLPlugin.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigController {

    private final PluginController controller;
    private MessageController messageController;

    private List<Material> simpleHarvestMaterials = Arrays.asList(Material.BEETROOTS, Material.WHEAT, Material.CARROTS, Material.POTATOES);
    private List<Material> advancedCompostMaterials = Arrays.asList(Material.ROTTEN_FLESH);
    private final File config;
    private final YamlConfiguration ymlConfig;
    private World overworld;

    public ConfigController(PluginController controller) {
        this.controller = controller;
        this.overworld = Bukkit.getWorld("world");

        config = new File(controller.getMain().getDataFolder(), "config.yml");
        ymlConfig = YamlConfiguration.loadConfiguration(config);

    }

    public void onEnable(){
        this.messageController = new MessageController();
        load(ymlConfig);
    }

    /*
     load Config
     */

    public void load(YamlConfiguration config) {
        loadSimpleHarvestMaterials(config);
        loadAdvancedCompostMaterials(config);
    }

    private void loadSimpleHarvestMaterials(YamlConfiguration config) {
        if (config.contains("simpleHarvest.materials")) {
            this.simpleHarvestMaterials = config.getStringList("simpleHarvest.materials").stream().map(x->Material.getMaterial(x.toUpperCase())).collect(Collectors.toList());
        }
    }

    private void loadAdvancedCompostMaterials(YamlConfiguration config) {
        if (config.contains("advancedCompost.materials")) {
            this.advancedCompostMaterials = config.getStringList("advancedCompost.materials").stream().map(x->Material.getMaterial(x.toUpperCase())).collect(Collectors.toList());
        }
    }


    /*
     save Config
     */

    public void saveConfig() {
        //TODO make this shit also work

        saveSimpleHarvestMaterials(ymlConfig, simpleHarvestMaterials);
        saveAdvancedCompostMaterials(ymlConfig, advancedCompostMaterials);

        try {
            ymlConfig.save(this.config.getAbsoluteFile());
            Util.sendInfoLogMessage(controller,this.config.getAbsolutePath());
            Util.sendInfoLogMessage(controller, "Config saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSimpleHarvestMaterials(YamlConfiguration config, List<Material> simpleHarvestMaterials) {
        List<String> simpleHarvestMaterialsStringList = new ArrayList<>();
        simpleHarvestMaterials.forEach(x->simpleHarvestMaterialsStringList.add(x.toString()));
        config.set("simpleHarvest.materials", simpleHarvestMaterialsStringList);
    }

    private void saveAdvancedCompostMaterials(YamlConfiguration config, List<Material> advancedCompostMaterials) {
        List<String> advancedCompostMaterialsStringList = new ArrayList<>();
        advancedCompostMaterials.forEach(x->advancedCompostMaterialsStringList.add(x.toString()));
        config.set("advancedCompost.materials", advancedCompostMaterialsStringList);
    }


    /*
     Getter
    */

    public List<Material> getSimpleHarvestMaterials() {
        return simpleHarvestMaterials;
    }

    public MessageController getMessages() {
        return this.messageController;
    }

    public List<Material> getAdvancedCompostMaterials() {
        return advancedCompostMaterials;
    }

    /*
     Setter
     */

    public void setSimpleHarvestMaterials(List<Material> simpleHarvestMaterials) {
        this.simpleHarvestMaterials = simpleHarvestMaterials;
    }

    public void setAdvancedCompostMaterials(List<Material> advancedCompostMaterials) {
        this.advancedCompostMaterials = advancedCompostMaterials;
    }
}