package de.andre.QoLPlugin.controller;

public class ConfigPaths {
    //Class for easier management of paths in the config file


    public final static String configFileName = "config.yml";

    /*
    * Is Enabled? -> Path to booleans that describe whether a certain feature is enabled or not
    * */
    public final static String fastLadderClimbingActivated = "fastLadderClimbing.activated";

    public final static String toolBreakPreventionActivated = "toolBreakPrevention.activated";

    public final static String fastLeafDecayActivated = "fastLeafDecay.activated";

    public final static String ctrlQCraftActivated = "ctrlQCraft.activated";

    public final static String simpleHarvestActivated = "simpleHarvest.activated";

    public final static String villagerSelectActivated = "villagerSelect.activated";

    public final static String unlimitedAnvilActivated = "unlimitedAnvil.activated";

    public final static String advancedCompostActivated = "advancedCompost.activated";

    public final static String privateMessageViewingActivated = "privateMessageViewing.activated";

    public final static String mutedPlayersActivated = "mutedPlayers.activated";

    public final static String adminModeActivated = "adminmode.activated";

    public final static String vineMinerActivated = "vineMiner.activated";

    /*
    * Paths to other config params (Lists, Strings, Integers)
    * */

    public final static String toolBreakPreventionDetectString = "toolBreakPrevention.detectString";

    public final static String fastLeafDecayIgnoreLeafType = "fastLeafDecay.ignoreLeafType";

    public final static String villagerSelectCooldown = "villagerSelect.cooldown";

    public final static String villagerSelectMaxRetries = "villagerSelect.maxRetries";

    public final static String adminModeSendMessageOnGamemodeChange = "adminmode.sendMessageOnGamemodeChange";

    public final static String simpleHarvestMaterials = "simpleHarvest.materials";

    public final static String advancedCompostMaterials = "advancedCompost.materials";

    public final static String mutedPlayersPlayers = "mutedPlayers.players";

    public final static String privateMessageViewingPlayers = "privateMessageViewing.players";

    public final static String sendCoordsOfDeathOnRespawn = "SendCoordsOfDeathOnRespawn";

    public final static String vineMinerMaxBlocks = "vineMiner.maxBlocks";
}
