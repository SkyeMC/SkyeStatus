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
        TextComponent answer = new TextComponent();
        Configuration config = plugin.getConfig();

        StringBuilder headerMsgBuilder = new StringBuilder();
        headerMsgBuilder.
                append(config.getString("servers_spacer") + "&r\n").
                append(config.getString("servers_title") + "&r\n").
                append(config.getString("servers_spacer") + "&r\n");

        answer.setText(Utils.formatter(headerMsgBuilder.toString()));

        for(SkyeServer server : plugin.getServers()){
            boolean isCurrentServer = false;
            if(sender instanceof ProxiedPlayer ){ // Checking if executer is a player
                isCurrentServer = ((ProxiedPlayer) sender).getServer().getInfo() == server.getInfo();
            }
            answer.addExtra(server.getFancyStatus(isCurrentServer)); // Adding current server to list
        }
        sender.sendMessage(answer);
    }

}
