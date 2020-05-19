package net.skyemc.skyestatus;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Map;
import java.util.Timer;

public class SkyeServer {

    String name; // Server name
    ServerInfo info; // Server info (duh)
    SkyeStatus plugin; // Plugin object
    String friendlyName; // Friendly server name, may be null

    public boolean getsPinged = true;
    public boolean getsNotified = false;
    public boolean isOnline = false;

    private final Timer pingTimer = new Timer(true); // This servers timer object

    public SkyeServer(Map.Entry<String, ServerInfo> entry, SkyeStatus plugin) {
        this.name = entry.getKey();
        this.info = entry.getValue();
        this.plugin = plugin;
    }

    public void startPinging(int interval){
        if(this.getsPinged) // Checking if this server is supposed to be pinged
            pingTimer.schedule(new PingHandler(plugin, this), 500, interval * 1000); // Scheduling the timer for this server
    }

    public void statusChange(boolean val){
        if (isOnline == val){ // Checking if Status changed
            return;
        }
        if(val){ // when server went online
            plugin.getLogger().info("Server " + this.name + "(" + this.tryFriendlyName() + ") is back online");
            plugin.notifyServers(this);
        } else { // when server went offline
          plugin.getLogger().warning("Server " + this.name + "(" + this.tryFriendlyName() + ") has gone offline");
        }
        this.isOnline = val; // Updating the online value
    }

    public void sendMessage(TextComponent msg){
        info.getPlayers().forEach(proxiedPlayer -> { // Looping through all players on this server
            proxiedPlayer.sendMessage(ChatMessageType.CHAT, msg);
        });
    }

    public String tryFriendlyName(){
        return friendlyName == null ? name : friendlyName;
    }

    public void stopPinging(){
        pingTimer.cancel(); // Stopping the timer
    }

    public ServerInfo getInfo(){
        return info;
    }
}
