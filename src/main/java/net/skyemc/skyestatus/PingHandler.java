package net.skyemc.skyestatus;

import net.md_5.bungee.api.ServerPing;

import java.util.TimerTask;

public class PingHandler extends TimerTask {

    SkyeStatus plugin;
    SkyeServer server;

    public PingHandler(SkyeStatus plugin, SkyeServer server) {
        super();
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public void run() {
        server.getInfo().ping(this::pingCallback);
    }

    private void pingCallback(ServerPing result, Throwable error) {
        if(result == null){ // Result is null when server is unreachable
            this.server.statusChange(false);
            return;
        }
        if(result.getPlayers() == null) { // Result is null when server is still starting
            this.server.statusChange(false);
            this.server.isRestarting = true;
            return;
        }
        this.server.isRestarting = false;
        this.server.statusChange(true);
    }
}
