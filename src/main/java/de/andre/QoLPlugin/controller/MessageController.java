package de.andre.QoLPlugin.controller;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class MessageController {
    private String PLUGINPREFIX = "[QoLPlugin] ";
    private String SERVERPREFIX = ChatColor.RED + "[QoLPlugin]" + ChatColor.RESET + " ";
    private String ERRORADDMATERIAL = SERVERPREFIX + "There was an Error. Either the Material doesn't exist or it was spelled wrong.";
    private String MATERIALADDEDSUCCESS = SERVERPREFIX + " Material added successfully.";
    private String MATERIALREMOVEDSUCCESS = SERVERPREFIX + " Material removed successfully.";
    private String SAVECONFIGCONSOLE = PLUGINPREFIX + " The Config was saved.";
    private String SAVECONFIGINGAME = SERVERPREFIX + " The Config was saved.";
    private String GENERALERRORMESSAGE = SERVERPREFIX + "What the fork happened here? O.o";
    private String PRIVATEMESSAGEFROMONEPLAYERTOANOTHER = SERVERPREFIX+ "%s texted %s: %s";
    /*
     Setter
     */

    public void setServerPrefix(String s){SERVERPREFIX=s;}

    public void setPLUGINPREFIX(String PLUGINPREFIX) {
        this.PLUGINPREFIX = PLUGINPREFIX;
    }

    public void setSERVERPREFIX(String SERVERPREFIX) {
        this.SERVERPREFIX = SERVERPREFIX;
    }

    public void setERRORADDMATERIAL(String ERRORADDMATERIAL) {
        this.ERRORADDMATERIAL = ERRORADDMATERIAL;
    }

    public void setMATERIALADDEDSUCCESS(String MATERIALADDEDSUCCESS) {
        this.MATERIALADDEDSUCCESS = MATERIALADDEDSUCCESS;
    }

    public void setMATERIALREMOVEDSUCCESS(String MATERIALREMOVEDSUCCESS) {
        this.MATERIALREMOVEDSUCCESS = MATERIALREMOVEDSUCCESS;
    }

    public void setSAVECONFIGCONSOLE(String SAVECONFIGCONSOLE) {
        this.SAVECONFIGCONSOLE = SAVECONFIGCONSOLE;
    }

    public void setSAVECONFIGINGAME(String SAVECONFIGINGAME) {
        this.SAVECONFIGINGAME = SAVECONFIGINGAME;
    }

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
