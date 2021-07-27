package de.andre.QoLPlugin.controller;

import de.andre.QoLPlugin.AdditionalClasses.AdminModeData;
import de.andre.QoLPlugin.Commands.AdminMode;
import de.andre.QoLPlugin.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigController {

    private final PluginController controller;
    private MessageController messageController;

    //----------------------------------------------------------//
    //Loaded from Config (with defaults if nothing is defined)
    private boolean simpleHarvestActivated = true;
    private ArrayList<Material> simpleHarvestMaterials = new ArrayList<>(Arrays.asList(Material.BEETROOTS, Material.WHEAT, Material.CARROTS, Material.POTATOES));

    private boolean advancedCompostActivated = true;
    private ArrayList<Material> advancedCompostMaterials = new ArrayList<>(Arrays.asList(Material.ROTTEN_FLESH));

    private HashMap<Player, AdminModeData> adminmodeHashmap = new HashMap<>();

    private boolean mutedPlayersActivated = true;
    private ArrayList<Player> mutedPlayers = new ArrayList<>();

    private boolean adminsWhichEnabledPrivateMessageViewingActivated = true; //if the admins should see /msg messages if they enabled it
    private ArrayList<Player> adminsWhichEnabledPrivateMessageViewing = new ArrayList<>();

    private HashMap<Player, Location> deadPlayersHashMap = new HashMap<>();

    private boolean unlimitedCost = true;

    private boolean sendCoordsOfDeathOnRespawn = false;
    //----------------------------------------------------------//
    private final File config;
    private final YamlConfiguration ymlConfig;
    private World overworld;

    public ConfigController(PluginController controller) {
        this.controller = controller;
        this.overworld = Bukkit.getWorld("world");

        config = new File(controller.getMain().getDataFolder(), "config.yml");
        ymlConfig = YamlConfiguration.loadConfiguration(config);

    }

    public void onEnable() {
        this.messageController = new MessageController();
        load(ymlConfig);
    }

    /*
     load Config
     */

    public void load(YamlConfiguration config) {
        loadSimpleHarvestMaterials(config);
        loadAdvancedCompostMaterials(config);
        loadMutedPlayers(config);
        loadAdminsWhichEnabledPrivateMessageViewing(config);
        loadSendCoordsOfDeathOnRespawn(config);
    }

    private void loadSimpleHarvestMaterials(YamlConfiguration config) {
        if (config.contains("simpleHarvest.materials") && config.contains("simpleHarvest.activated")) {
            simpleHarvestActivated = config.getBoolean("simpleHarvest.activated");
            if (simpleHarvestActivated) {
                this.simpleHarvestMaterials = (ArrayList<Material>) config.getStringList("simpleHarvest.materials").stream().map(x -> Material.getMaterial(x.toUpperCase())).collect(Collectors.toList());
            }
        }
    }

    private void loadAdvancedCompostMaterials(YamlConfiguration config) {
        if (config.contains("advancedCompost.materials") && config.contains("advancedCompost.activated")) {
            advancedCompostActivated = config.getBoolean("advancedCompost.activated");
            if (advancedCompostActivated) {
                this.advancedCompostMaterials = (ArrayList<Material>) config.getStringList("advancedCompost.materials").stream().map(x -> Material.getMaterial(x.toUpperCase())).collect(Collectors.toList());
            }
        }
    }

    private void loadMutedPlayers(YamlConfiguration config) {
        if (config.contains("mutedPlayers.players") && config.contains("mutedPlayers.activated")) {
            mutedPlayersActivated = config.getBoolean("mutedPlayers.activated");
            if (mutedPlayersActivated) {
                this.mutedPlayers = (ArrayList<Player>) config.getStringList("mutedPlayers.players").stream().map(Bukkit::getPlayer).collect(Collectors.toList());
            }
        }
    }

    private void loadAdminsWhichEnabledPrivateMessageViewing(YamlConfiguration config) {
        if (config.contains("privateMessageViewing.admins") && config.contains("privateMessageViewing.activated")) {
            adminsWhichEnabledPrivateMessageViewingActivated = config.getBoolean("privateMessageViewing.activated");
            if (adminsWhichEnabledPrivateMessageViewingActivated) {
                this.adminsWhichEnabledPrivateMessageViewing = (ArrayList<Player>) config.getStringList("privateMessageViewing.players").stream().map(Bukkit::getPlayer).filter(Objects::nonNull).filter(ServerOperator::isOp).collect(Collectors.toList());
            }
        }
    }

    private void loadSendCoordsOfDeathOnRespawn(YamlConfiguration config){
        if(config.contains("SendCoordsOfDeathOnRespawn")){
            this.sendCoordsOfDeathOnRespawn = config.getBoolean("SendCoordsOfDeathOnRespawn");
        }
    }


    /*
     save Config
     */

    public void saveConfig() {

        AdminMode.resetAllAdmins(controller, adminmodeHashmap.keySet());

        saveSimpleHarvestMaterials(ymlConfig, simpleHarvestMaterials, simpleHarvestActivated);
        saveAdvancedCompostMaterials(ymlConfig, advancedCompostMaterials, advancedCompostActivated);
        saveMutedPlayers(ymlConfig, mutedPlayers, mutedPlayersActivated);
        saveAdminsWhichEnabledPrivateMessageViewing(ymlConfig, adminsWhichEnabledPrivateMessageViewing, adminsWhichEnabledPrivateMessageViewingActivated);
        saveSendCoordsOfDeathOnRespawn(ymlConfig,sendCoordsOfDeathOnRespawn);

        try {
            ymlConfig.save(this.config.getAbsoluteFile());
            Util.sendInfoLogMessage(controller, this.config.getAbsolutePath());
            Util.sendInfoLogMessage(controller, "Config saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSendCoordsOfDeathOnRespawn(YamlConfiguration config, boolean sendCoordsOfDeathOnRespawn) {
        config.set("SendCoordsOfDeathOnRespawn",sendCoordsOfDeathOnRespawn);
    }

    private void saveSimpleHarvestMaterials(YamlConfiguration config, List<Material> simpleHarvestMaterials, boolean simpleHarvestActivated) {
        List<String> simpleHarvestMaterialsStringList = new ArrayList<>();
        simpleHarvestMaterials.forEach(x -> simpleHarvestMaterialsStringList.add(x.toString()));
        config.set("simpleHarvest.materials", simpleHarvestMaterialsStringList);
        config.set("simpleHarvest.activated", simpleHarvestActivated);
    }

    private void saveAdvancedCompostMaterials(YamlConfiguration config, List<Material> advancedCompostMaterials, boolean advancedCompostActivated) {
        List<String> advancedCompostMaterialsStringList = new ArrayList<>();
        advancedCompostMaterials.forEach(x -> advancedCompostMaterialsStringList.add(x.toString()));
        config.set("advancedCompost.materials", advancedCompostMaterialsStringList);
        config.set("advancedCompost.activated", advancedCompostActivated);
    }

    private void saveMutedPlayers(YamlConfiguration config, ArrayList<Player> mutedPlayers, boolean mutedPlayersActivated) {
        config.set("mutedPlayers.players",mutedPlayers.stream().map(HumanEntity::getName).collect(Collectors.toList()));
        config.set("mutedPlayers.activated",mutedPlayersActivated);
    }

    private void saveAdminsWhichEnabledPrivateMessageViewing(YamlConfiguration config, ArrayList<Player> adminsWhichEnabledPrivateMessageViewing, boolean adminsWhichEnabledPrivateMessageViewingActivated) {
        config.set("privateMessageViewing.admins",adminsWhichEnabledPrivateMessageViewing.stream().map(HumanEntity::getName).collect(Collectors.toList()));
        config.set("privateMessageViewing.activated",adminsWhichEnabledPrivateMessageViewingActivated);
    }

    /*
     Getter
    */

    public ArrayList<Material> getSimpleHarvestMaterials() {
        return simpleHarvestMaterials;
    }

    public MessageController getMessages() {
        return this.messageController;
    }

    public ArrayList<Material> getAdvancedCompostMaterials() {
        return advancedCompostMaterials;
    }

    public HashMap<Player, AdminModeData> getAdminmodeHashmap() {
        return adminmodeHashmap;
    }

    public ArrayList<Player> getMutedPlayers() {
        return this.mutedPlayers;
    }

    public ArrayList<Player> getAdminsWhichEnabledPrivateMessageViewing() {
        return this.adminsWhichEnabledPrivateMessageViewing;
    }

    public HashMap<Player, Location> getDeadPlayers() {
        return deadPlayersHashMap;
    }

    public boolean getUnlimitedCost() {
        return this.unlimitedCost;
    }

    public boolean getSendCoordsOfDeathOnRespawn() {
        return this.sendCoordsOfDeathOnRespawn;
    }

    /*
     Setter
     */

    public void setSimpleHarvestMaterials(ArrayList<Material> simpleHarvestMaterials) {
        this.simpleHarvestMaterials = simpleHarvestMaterials;
    }

    public void setAdvancedCompostMaterials(ArrayList<Material> advancedCompostMaterials) {
        this.advancedCompostMaterials = advancedCompostMaterials;
    }

    public void setAdminmodeHashmap(HashMap<Player, AdminModeData> adminmodeHashmap) {
        this.adminmodeHashmap = adminmodeHashmap;
    }

    public void setUnlimitedCost(boolean unlimitedCost) {
        this.unlimitedCost = unlimitedCost;
    }

    public void setSendCoordsOfDeathOnRespawn(boolean sendCoordsOfDeathOnRespawn) {
        this.sendCoordsOfDeathOnRespawn = sendCoordsOfDeathOnRespawn;
    }

    /*
     "Adder"
     */

    public void addSimpleHarvestMaterials(Material simpleHarvestMaterials) {
        this.simpleHarvestMaterials.add(simpleHarvestMaterials);
    }

    public void addAdvancedCompostMaterials(Material advancedCompostMaterials) {
        this.advancedCompostMaterials.add(advancedCompostMaterials);
    }

    public void addAdminModePlayer(Player p, AdminModeData arrayList) {
        this.adminmodeHashmap.put(p, arrayList);
    }

    public void addAdminsWhichEnabledPrivateMessageViewing(Player player) {
        this.adminsWhichEnabledPrivateMessageViewing.add(player);
    }

    public void addPlayerToDeadPlayers(Player p,Location l){
        this.deadPlayersHashMap.put(p,l);
    }
    /*
     Remove
     */

    public void removeAdminsWhichEnabledPrivateMessageViewing(Player player) {
        this.adminsWhichEnabledPrivateMessageViewing.remove(player);
    }

    public void removeSimpleHarvestMaterials(Material simpleHarvestMaterials) {
        this.simpleHarvestMaterials.remove(simpleHarvestMaterials);
    }

    public void removeAdvancedCompostMaterials(Material advancedCompostMaterials) {
        this.advancedCompostMaterials.remove(advancedCompostMaterials);
    }

    public void removePlayerFromDeadPlayers(Player p){
        this.deadPlayersHashMap.remove(p);
    }
}