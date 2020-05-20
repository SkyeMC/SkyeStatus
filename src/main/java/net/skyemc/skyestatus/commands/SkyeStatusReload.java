package net.skyemc.skyestatus.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.skyemc.skyestatus.SkyeStatus;

public class SkyeStatusReload extends Command {

    private SkyeStatus plugin;

    public SkyeStatusReload(String name, SkyeStatus plugin, String... aliases) {
        super(name, "skyemc.skyestatus.command.reload", aliases);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TextComponent answer = new TextComponent();
            answer.setText("Reloading SkyeStatus config...");

        sender.sendMessage(answer);
        if(!plugin.loadConfig()){
            answer.setText("Something went wrong while reloading config!");
            answer.setColor(ChatColor.DARK_RED);
            plugin.getLogger().severe("There was an error loading the config!");
        }else{
            answer.setText("Successfully reloaded config!");
            answer.setColor(ChatColor.GREEN);
        }
        sender.sendMessage(answer);

        // Stopping ping timers:
        answer.setText("Stopping ping timers...");
        answer.setColor(ChatColor.WHITE);
        sender.sendMessage(answer);
        plugin.pingStopper();
        answer.setText("Stopped ping timers");
        answer.setColor(ChatColor.GREEN);
        sender.sendMessage(answer);

        //Restarting ping timers:
        answer.setText("Starting ping timers...");
        answer.setColor(ChatColor.WHITE);
        sender.sendMessage(answer);
        plugin.pingStarter();
        answer.setText("Started ping timers");
        answer.setColor(ChatColor.GREEN);
        sender.sendMessage(answer);

    }
}
