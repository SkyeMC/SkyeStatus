package net.skyemc.skyestatus;

import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.skyemc.skyestatus.commands.SkyeStatusReload;

import java.io.*;
import java.util.Map;
import java.util.logging.Logger;

public final class SkyeStatus extends Plugin {

    private Logger logger;
    Configuration config; // Config object
    private SkyeServer[] serverArray;

    @Override
    public void onEnable() {
        logger = getLogger();
        if (!loadConfig()){
            logger.severe("Plugin will not be loaded!");
            return;
        }
        getProxy().getPluginManager().registerCommand(this, new SkyeStatusReload("skyestatus_reload", this, "ssr"));
        pingStarter();
    }

    @Override
    public void onDisable() {
        pingStopper();
    }

    public void pingStarter(){
        Map<String, ServerInfo> servers = getProxy().getServers(); // Populating list of Servers
        serverArray = new SkyeServer[servers.size()]; // Creating array with fixed size

        int i = 0;
        for (Map.Entry<String, ServerInfo> entry : servers.entrySet()){
            SkyeServer tempServer = new SkyeServer(entry, this);

            if(config.getList("server_blacklist").contains(entry.getKey())){ // Checking if server is blacklisted
                tempServer.getsPinged = false;
            }
            if(config.getList("servers_to_notify").contains(entry.getKey())){ // Checking if server should be notified
                tempServer.getsNotified = true;
            }

            tempServer.startPinging(config.getInt("ping_interval", 10)); // Start pinging this server
            serverArray[i] = tempServer;
            i++;
        }
        logger.info("Started ping timers");
    }

    public void pingStopper(){
        for (SkyeServer server : serverArray){
            server.stopPinging();
        }
        logger.info("Stopped ping timers");
    }

    public void notifyServers(SkyeServer server){
        String hoverText = config.getString("hover_text"); // Loading configured hover text
        TextComponent message = new TextComponent();
            message.setText("Server " + server.name + " is back online!");
            message.setColor(ChatColor.GREEN);
            message.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
            message.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/server " + server.name));

        for (SkyeServer serv : serverArray){
            if(!serv.getsNotified || serv.getInfo().getPlayers().isEmpty()){ // Checking whether server should be notified or is empty
                continue; // Skipping this iteration
            }
            serv.sendMessage(message); // Sending the message to the server
        }
    }

    //TODO: maybe make this prettier
    public boolean loadConfig() {
        if (!getDataFolder().exists()){ // Checking if config folder exists
            getDataFolder().mkdir(); // Creating config folder
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()){ // Checking if config file exists
            try {
                configFile.createNewFile(); // Creating config file
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os); // Copying default config to config folder
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile); // Filling config object
        } catch (IOException e) {
            logger.severe("Unable to load configuration file");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
