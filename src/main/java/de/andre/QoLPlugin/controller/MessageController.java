package de.andre.QoLPlugin.controller;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MessageController {
    private final String PLUGINPREFIX = "[QoLPlugin] ";
    private final String SERVERPREFIX = ChatColor.RED + "[QoLPlugin]" + ChatColor.RESET + " ";
    private final String ERRORADDMATERIAL = SERVERPREFIX + "There was an Error. Either the Material doesn't exist or it was spelled wrong.";
    private final String MATERIALADDEDSUCCESS = SERVERPREFIX + "Material added successfully.";
    private final String MATERIALREMOVEDSUCCESS = SERVERPREFIX + "Material removed successfully.";
    private final String SAVECONFIGCONSOLE = PLUGINPREFIX + "The Config was saved.";
    private final String SAVECONFIGINGAME = SERVERPREFIX + "The Config was saved.";
    private final String GENERALERRORMESSAGE = SERVERPREFIX + "What the fork happened here? O.o";
    private final String PRIVATEMESSAGEFROMONEPLAYERTOANOTHER = SERVERPREFIX + "%s texted %s: %s";
    /*
     Getter
     */

    public String getSERVERPREFIX() {
        return SERVERPREFIX;
    }

    public String getERRORADDMATERIAL() {
        return ERRORADDMATERIAL;
    }

    public String getMATERIALADDEDSUCCESS() {
        return MATERIALADDEDSUCCESS;
    }

    public String getMATERIALREMOVEDSUCCESS() {
        return MATERIALREMOVEDSUCCESS;
    }

    public String getPLUGINPREFIX() {
        return PLUGINPREFIX;
    }

    public String getSAVECONFIGCONSOLE() {
        return SAVECONFIGCONSOLE;
    }

    public String getSAVECONFIGINGAME() {
        return SAVECONFIGINGAME;
    }

    public String getGENERALERROR(){return GENERALERRORMESSAGE;}

    public String getPRIVATEMESSAGEFROMONEPLAYERTOANOTHER(Player sender, ArrayList<Player> reciever, String msg){
        return String.format(PRIVATEMESSAGEFROMONEPLAYERTOANOTHER, sender.getName(),reciever.toString().replace("[","").replace("]","").replace("  "," "),msg);
    }
}
