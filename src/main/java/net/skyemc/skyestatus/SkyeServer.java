package net.skyemc.skyestatus;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.skyemc.skyestatus.utils.Utils;

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
    public boolean isRestarting = false;

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

    public Utils.Status getStatus(){
        if(this.isRestarting)
            return Utils.Status.RESTARTING;
        if(this.isOnline){
            return Utils.Status.ONLINE;
        }
        return Utils.Status.OFFLINE;
    }

    public TextComponent getFancyStatus(boolean isCurrent){
        Configuration config = plugin.getConfig();
        TextComponent message = new TextComponent(); // Final TextComponent which will be returned in the end#

        Utils.Status state = getStatus();
        String serverStatus;
        switch (state){
            case ONLINE:
                serverStatus = config.getString("servers_status_online");
                break;
            case OFFLINE:
                serverStatus = config.getString("servers_status_offline");
                break;
            case RESTARTING:
                serverStatus = config.getString("servers_status_restarting");
                break;
            default:
                serverStatus = "&4Unknown";
        }

        String join_here = isCurrent ? config.getString("servers_here") : ""; // Adding "You are here" message in case its true
        if(state == Utils.Status.ONLINE && !isCurrent){
            join_here = config.getString("servers_click_join"); // Setting this to the click to join message
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + this.name)); // Adding click event to join server
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(config.getString("hover_text")).create())); // Adding hover event for text
        }

        String finalMessage = config.getString("servers_entry_format", "Config error"); // Fetching the message format
        finalMessage = finalMessage.replace("%server%", this.tryFriendlyName() + "&r"); // Replacing %server% placeholder
        finalMessage = finalMessage.replace("%status%", serverStatus + "&r"); // Replacing %status% placeholder
        finalMessage = finalMessage.replace("%join_here%", join_here + "&r"); // Replacing %join_here% placeholder
        finalMessage = finalMessage.replace("%rawname%", this.name + "&r"); // Replacing %rawname% placeholder
        finalMessage = finalMessage + "\n"; // Adding newline

        message.setText(Utils.formatter(finalMessage));
        return message;
    }
}
