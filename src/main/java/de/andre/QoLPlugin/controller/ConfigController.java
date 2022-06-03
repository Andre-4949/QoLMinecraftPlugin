package de.andre.QoLPlugin.controller;

import de.andre.QoLPlugin.AdditionalClasses.AdminModeData;
import de.andre.QoLPlugin.Commands.AdminMode;
import de.andre.QoLPlugin.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigController {

    private final PluginController controller;
    private MessageController messageController;
    public static final int SECONDSTOTICKS = 20;

    //----------------------------------------------------------//
    //Loaded from Config (with defaults if nothing is defined)
    private boolean simpleHarvestEnabled = false;
    private ArrayList<Material> simpleHarvestMaterials = new ArrayList<>();

    private boolean advancedCompostEnabled = false;
    private ArrayList<Material> advancedCompostMaterials = new ArrayList<>();

    private boolean adminModeEnabled = false;
    private HashMap<Player, AdminModeData> adminmodeHashmap = new HashMap<>();
    private boolean sendMessageOnGamemodeChange = false; // sends a message whenever a player changes gamemode from surivial to creative and doesnt use /adminmode


    private boolean mutedPlayersEnabled = false;
    private ArrayList<Player> mutedPlayers = new ArrayList<>();

    private boolean adminsWhichEnabledPrivateMessageViewingActivated = false; //if the admins should see /msg messages if they enabled it
    private ArrayList<Player> adminsWhichEnabledPrivateMessageViewing = new ArrayList<>();

    private HashMap<Player, Location> deadPlayersHashMap = new HashMap<>();

    private boolean unlimitedCost = false;

    private boolean sendCoordsOfDeathOnRespawn = false;

    private boolean villagerSelectEnabled = true;
    private int villagerSelectCooldown = 1;
    private int villagerSelectMaxRetries = 700;

    private boolean ctrlQCraftEnabled = false;

    private boolean fastLeafDecayEnabled = false;
    private boolean fastLeafDecayIgnoreLeafType = false;

    private boolean toolBreakPreventionEnabled = false;
    private String toolBreakPreventionDetectString = "almost unbreakable";

    private boolean fastLadderClimbingEnabled = false;

    private boolean vineMinerEnabled = false;
    private int vineMinerMaxBlocks = 512;
    //----------------------------------------------------------//
    private final File config;
    private final YamlConfiguration ymlConfig;

    public ConfigController(PluginController controller) {
        this.controller = controller;

        config = new File(controller.getMain().getDataFolder(), ConfigPaths.configFileName);
        ymlConfig = YamlConfiguration.loadConfiguration(config);

    }

    public void onEnable() {
        this.messageController = new MessageController();
        load(ymlConfig);
        saveConfig();
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
        loadAdminMode(config);
        loadUnlimitedAnvil(config);
        loadVillagerSelect(config);
        loadCtrlQCraft(config);
        loadFastLeafDecayEnabled(config);
        loadToolBreakPrevention(config);
        loadFastLadderClimbing(config);
        loadVineMiner(config);
    }

    private void loadVineMiner(YamlConfiguration config) {
        this.vineMinerEnabled = config.getBoolean(ConfigPaths.vineMinerActivated,vineMinerEnabled);
        this.vineMinerMaxBlocks = config.getInt(ConfigPaths.vineMinerMaxBlocks, vineMinerMaxBlocks);
    }

    private void loadFastLadderClimbing(YamlConfiguration config) {
        this.fastLadderClimbingEnabled = config.getBoolean(ConfigPaths.fastLadderClimbingActivated, fastLadderClimbingEnabled);
    }

    private void loadToolBreakPrevention(YamlConfiguration config) {
        this.toolBreakPreventionEnabled = config.getBoolean(ConfigPaths.toolBreakPreventionActivated, toolBreakPreventionEnabled);
        this.toolBreakPreventionDetectString = config.getString(ConfigPaths.toolBreakPreventionDetectString, toolBreakPreventionDetectString);
    }

    private void loadFastLeafDecayEnabled(YamlConfiguration config) {
        this.fastLeafDecayEnabled = config.getBoolean(ConfigPaths.fastLeafDecayActivated, fastLeafDecayEnabled);
        this.fastLeafDecayIgnoreLeafType = config.getBoolean(ConfigPaths.fastLeafDecayIgnoreLeafType, fastLeafDecayIgnoreLeafType);
    }

    private void loadCtrlQCraft(YamlConfiguration config) {
        this.ctrlQCraftEnabled = config.getBoolean(ConfigPaths.ctrlQCraftActivated, ctrlQCraftEnabled);
    }

    private void loadVillagerSelect(YamlConfiguration config) {
        this.villagerSelectEnabled = config.getBoolean(ConfigPaths.villagerSelectActivated, villagerSelectEnabled);
        this.villagerSelectCooldown = config.getInt(ConfigPaths.villagerSelectCooldown, villagerSelectCooldown);
        this.villagerSelectMaxRetries = config.getInt(ConfigPaths.villagerSelectMaxRetries, villagerSelectMaxRetries);
    }

    private void loadUnlimitedAnvil(YamlConfiguration config) {
        this.unlimitedCost = config.getBoolean(ConfigPaths.unlimitedAnvilActivated, unlimitedCost);
    }

    private void loadAdminMode(YamlConfiguration config) {
        this.adminModeEnabled = config.getBoolean(ConfigPaths.adminModeActivated, adminModeEnabled);
        this.sendMessageOnGamemodeChange = config.getBoolean(ConfigPaths.sendCoordsOfDeathOnRespawn, sendMessageOnGamemodeChange);
    }

    private void loadSimpleHarvestMaterials(YamlConfiguration config) {
        this.simpleHarvestEnabled = config.getBoolean(ConfigPaths.simpleHarvestActivated, simpleHarvestEnabled);
        if (simpleHarvestEnabled)
            this.simpleHarvestMaterials = (ArrayList<Material>) config.getStringList(ConfigPaths.simpleHarvestMaterials).stream().map(x -> Material.getMaterial(x.toUpperCase())).collect(Collectors.toList());

    }

    private void loadAdvancedCompostMaterials(YamlConfiguration config) {
        this.advancedCompostEnabled = config.getBoolean(ConfigPaths.advancedCompostActivated, advancedCompostEnabled);
        if (advancedCompostEnabled)
            this.advancedCompostMaterials = (ArrayList<Material>) config.getStringList(ConfigPaths.advancedCompostMaterials).stream().map(x -> Material.getMaterial(x.toUpperCase())).collect(Collectors.toList());
    }

    private void loadMutedPlayers(YamlConfiguration config) {
        this.mutedPlayersEnabled = config.getBoolean(ConfigPaths.mutedPlayersActivated, mutedPlayersEnabled);
        if (mutedPlayersEnabled)
            this.mutedPlayers = (ArrayList<Player>) config.getStringList(ConfigPaths.mutedPlayersPlayers).stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    private void loadAdminsWhichEnabledPrivateMessageViewing(YamlConfiguration config) {
        this.adminsWhichEnabledPrivateMessageViewingActivated = config.getBoolean(ConfigPaths.privateMessageViewingActivated, adminsWhichEnabledPrivateMessageViewingActivated);
        if (adminsWhichEnabledPrivateMessageViewingActivated)
            this.adminsWhichEnabledPrivateMessageViewing = (ArrayList<Player>) config.getStringList(ConfigPaths.privateMessageViewingPlayers).stream().map(Bukkit::getPlayer).filter(Objects::nonNull).filter(ServerOperator::isOp).collect(Collectors.toList());
    }

    private void loadSendCoordsOfDeathOnRespawn(YamlConfiguration config) {
        this.sendCoordsOfDeathOnRespawn = config.getBoolean(ConfigPaths.sendCoordsOfDeathOnRespawn, sendCoordsOfDeathOnRespawn);
    }


    /*
     save Config
     */

    public void saveConfig() {

        AdminMode.resetAllAdmins(controller, adminmodeHashmap.keySet());

        saveSimpleHarvestMaterials(ymlConfig);
        saveAdvancedCompostMaterials(ymlConfig);
        saveMutedPlayers(ymlConfig);
        saveAdminsWhichEnabledPrivateMessageViewing(ymlConfig);
        saveSendCoordsOfDeathOnRespawn(ymlConfig);
        saveSendMessageOnGamemodeChange(ymlConfig);
        saveUnlimitedAnvil(ymlConfig);
        saveVillagerSelect(ymlConfig);
        saveCtrlQCraft(ymlConfig);
        saveFastLeafDecay(ymlConfig);
        saveToolBreakPrevention(ymlConfig);
        saveFastLadderClimbing(ymlConfig);
        saveVineMiner(ymlConfig);
        try {
            ymlConfig.save(this.config.getAbsoluteFile());
            Util.sendInfoLogMessage(controller, this.config.getAbsolutePath());
            Util.sendInfoLogMessage(controller, "Config saved!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveVineMiner(YamlConfiguration config) {
        config.set(ConfigPaths.vineMinerActivated, vineMinerEnabled);
        config.set(ConfigPaths.vineMinerMaxBlocks, vineMinerMaxBlocks);
    }

    private void saveFastLadderClimbing(YamlConfiguration config) {
        config.set(ConfigPaths.fastLadderClimbingActivated, fastLadderClimbingEnabled);
    }

    private void saveToolBreakPrevention(YamlConfiguration config) {
        config.set(ConfigPaths.toolBreakPreventionActivated, toolBreakPreventionEnabled);
        config.set(ConfigPaths.toolBreakPreventionDetectString, toolBreakPreventionDetectString);
    }

    private void saveFastLeafDecay(YamlConfiguration config) {
        config.set(ConfigPaths.fastLeafDecayActivated, fastLeafDecayEnabled);
        config.set(ConfigPaths.fastLeafDecayIgnoreLeafType, fastLeafDecayIgnoreLeafType);
    }

    private void saveCtrlQCraft(YamlConfiguration config) {
        config.set(ConfigPaths.ctrlQCraftActivated, ctrlQCraftEnabled);
    }

    private void saveVillagerSelect(YamlConfiguration config) {
        config.set(ConfigPaths.villagerSelectActivated, villagerSelectEnabled);
        config.set(ConfigPaths.villagerSelectCooldown, villagerSelectCooldown);
        config.set(ConfigPaths.villagerSelectMaxRetries, villagerSelectMaxRetries);
    }

    private void saveUnlimitedAnvil(YamlConfiguration config) {
        config.set(ConfigPaths.unlimitedAnvilActivated, unlimitedCost);
    }

    private void saveSendMessageOnGamemodeChange(YamlConfiguration config) {
        config.set(ConfigPaths.adminModeSendMessageOnGamemodeChange, sendMessageOnGamemodeChange);
        config.set(ConfigPaths.adminModeActivated, adminModeEnabled);
    }

    private void saveSendCoordsOfDeathOnRespawn(YamlConfiguration config) {
        config.set(ConfigPaths.sendCoordsOfDeathOnRespawn, sendCoordsOfDeathOnRespawn);
    }

    private void saveSimpleHarvestMaterials(YamlConfiguration config) {
        List<String> simpleHarvestMaterialsStringList = new ArrayList<>();
        simpleHarvestMaterials.forEach(x -> simpleHarvestMaterialsStringList.add(x.toString()));
        config.set(ConfigPaths.simpleHarvestMaterials, simpleHarvestMaterialsStringList);
        config.set(ConfigPaths.simpleHarvestActivated, simpleHarvestEnabled);
    }

    private void saveAdvancedCompostMaterials(YamlConfiguration config) {
        List<String> advancedCompostMaterialsStringList = new ArrayList<>();
        advancedCompostMaterials.forEach(x -> advancedCompostMaterialsStringList.add(x.toString()));
        config.set(ConfigPaths.advancedCompostMaterials, advancedCompostMaterialsStringList);
        config.set(ConfigPaths.advancedCompostActivated, advancedCompostEnabled);
    }

    private void saveMutedPlayers(YamlConfiguration config) {
        config.set(ConfigPaths.mutedPlayersPlayers, mutedPlayers.stream().map(HumanEntity::getName).collect(Collectors.toList()));
        config.set(ConfigPaths.mutedPlayersActivated, mutedPlayersEnabled);
    }

    private void saveAdminsWhichEnabledPrivateMessageViewing(YamlConfiguration config) {
        config.set(ConfigPaths.privateMessageViewingPlayers, adminsWhichEnabledPrivateMessageViewing.stream().map(HumanEntity::getName).collect(Collectors.toList()));
        config.set(ConfigPaths.privateMessageViewingActivated, adminsWhichEnabledPrivateMessageViewingActivated);
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
    public void addPlayerToDeadPlayers(Player p, Location l) {
        this.deadPlayersHashMap.put(p, l);
    }

    public boolean isNotVineMinerEnabled() {
        return !vineMinerEnabled;
    }

    public int getVineMinerMaxBlocks() {
        return vineMinerMaxBlocks;
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

    public void removePlayerFromDeadPlayers(Player p) {
        this.deadPlayersHashMap.remove(p);
    }

    /*
    * Getter
    */

    public MessageController getMessageController() {
        return messageController;
    }

    public boolean isSimpleHarvestEnabled() {
        return simpleHarvestEnabled;
    }

    public ArrayList<Material> getSimpleHarvestMaterials() {
        return simpleHarvestMaterials;
    }

    public boolean isNotAdvancedCompostEnabled() {
        return !advancedCompostEnabled;
    }

    public ArrayList<Material> getAdvancedCompostMaterials() {
        return advancedCompostMaterials;
    }

    public HashMap<Player, AdminModeData> getAdminmodeHashmap() {
        return adminmodeHashmap;
    }

    public boolean isSendMessageOnGamemodeChange() {
        return sendMessageOnGamemodeChange;
    }

    public boolean isAdminModeEnabled() {
        return adminModeEnabled;
    }

    public boolean isMutedPlayersEnabled() {
        return mutedPlayersEnabled;
    }

    public ArrayList<Player> getMutedPlayers() {
        return mutedPlayers;
    }

    public boolean isAdminsWhichEnabledPrivateMessageViewingActivated() {
        return adminsWhichEnabledPrivateMessageViewingActivated;
    }

    public ArrayList<Player> getAdminsWhichEnabledPrivateMessageViewing() {
        return adminsWhichEnabledPrivateMessageViewing;
    }

    public HashMap<Player, Location> getDeadPlayersHashMap() {
        return deadPlayersHashMap;
    }

    public boolean isUnlimitedCost() {
        return unlimitedCost;
    }

    public boolean isSendCoordsOfDeathOnRespawn() {
        return sendCoordsOfDeathOnRespawn;
    }

    public boolean isVillagerSelectEnabled() {
        return villagerSelectEnabled;
    }

    public int getVillagerSelectCooldown() {
        return villagerSelectCooldown;
    }

    public int getVillagerSelectMaxRetries() {
        return villagerSelectMaxRetries;
    }

    public boolean isCtrlQCraftEnabled() {
        return ctrlQCraftEnabled;
    }

    public boolean isFastLeafDecayEnabled() {
        return fastLeafDecayEnabled;
    }

    public boolean isFastLeafDecayIgnoreLeafType() {
        return fastLeafDecayIgnoreLeafType;
    }

    public boolean isToolBreakPreventionEnabled() {
        return toolBreakPreventionEnabled;
    }

    public String getToolBreakPreventionDetectString() {
        return toolBreakPreventionDetectString;
    }

    public boolean isFastLadderClimbingEnabled() {
        return fastLadderClimbingEnabled;
    }

    /*
     * Setter
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

    public void setMutedPlayers(ArrayList<Player> mutedPlayers) {
        this.mutedPlayers = mutedPlayers;
    }

    public void setAdminsWhichEnabledPrivateMessageViewing(ArrayList<Player> adminsWhichEnabledPrivateMessageViewing) {
        this.adminsWhichEnabledPrivateMessageViewing = adminsWhichEnabledPrivateMessageViewing;
    }

    public void setDeadPlayersHashMap(HashMap<Player, Location> deadPlayersHashMap) {
        this.deadPlayersHashMap = deadPlayersHashMap;
    }

    public void setUnlimitedCost(boolean unlimitedCost) {
        this.unlimitedCost = unlimitedCost;
    }

    public void setSendCoordsOfDeathOnRespawn(boolean sendCoordsOfDeathOnRespawn) {
        this.sendCoordsOfDeathOnRespawn = sendCoordsOfDeathOnRespawn;
    }
}