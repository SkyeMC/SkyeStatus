package net.skyemc.skyestatus.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.skyemc.skyestatus.SkyeServer;
import net.skyemc.skyestatus.SkyeStatus;
import net.skyemc.skyestatus.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class SkyeStatusServers extends Command {

    SkyeStatus plugin;

    public SkyeStatusServers(String name, SkyeStatus plugin, String... aliases) {
        super(name, "skyemc.skyestatus.command.servers", aliases);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Map<String, String> messages = fillMessageMap();
        TextComponent answer = new TextComponent();
        StringBuilder headerMsgBuilder = new StringBuilder();

        headerMsgBuilder.
                append(messages.get("servers_spacer") + "&r\n").
                append(messages.get("servers_title") + "&r\n").
                append(messages.get("servers_spacer") + "&r\n");

        answer.setText(Utils.formatter(headerMsgBuilder.toString()));

        for(SkyeServer server : plugin.getServers()){
            boolean isCurrentServer = false;
            if(sender instanceof ProxiedPlayer ){
                isCurrentServer = ((ProxiedPlayer) sender).getServer().getInfo() == server.getInfo();
            }
            //sender.sendMessage(server.getFancyStatus(isCurrentServer));
            answer.addExtra(server.getFancyStatus(isCurrentServer));
        }

        sender.sendMessage(answer);


    }

    private Map<String, String> fillMessageMap(){
        Configuration config = plugin.getConfig();
        String error = "Error while loading config";
        Map<String, String> messages = new HashMap<String, String>()
        {{
            put("servers_spacer", config.getString("servers_spacer", error));
            put("servers_title", config.getString("servers_title", error));
        }};

        return messages;
    }

}
