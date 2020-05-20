package net.skyemc.skyestatus.utils;

import net.md_5.bungee.api.ChatColor;

public class Utils {
    public static enum Status{
        ONLINE,
        OFFLINE,
        RESTARTING
    }

    public static String formatter(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);// Replacing common color coded character with bungees
    }
}
