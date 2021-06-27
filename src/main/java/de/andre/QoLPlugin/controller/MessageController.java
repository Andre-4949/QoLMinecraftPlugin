package de.andre.QoLPlugin.controller;

import org.bukkit.ChatColor;

public class MessageController {
    //TODO add some more Messages so it doesn't look so depressing
    private String SERVERPREFIX = ChatColor.RED + "[QoLPlugin]" + ChatColor.RESET + " ";
    private String ERRORADDMATERIAL = SERVERPREFIX+"There was an Error. Either the Material doesn't exist or it was spelled wrong.";

    /*
     Setter
     */

    public void setServerPrefix(String s){SERVERPREFIX=s;}


    /*
     Getter
     */
    public String getServerPrefix(){return SERVERPREFIX;}

    public String getErrorAddMaterial() {
        return ERRORADDMATERIAL;
    }
}
