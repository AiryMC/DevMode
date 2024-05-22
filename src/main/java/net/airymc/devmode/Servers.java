package net.airymc.devmode;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.airymc.core.file.Config;

import java.util.ArrayList;
import java.util.List;

public class Servers {

    private final Config config;

    public Servers() {
        config = new Config("DevMode/servers.yml");
        config.setDefault("dev", new ArrayList<>());
        config.setDefault("maintenance", new ArrayList<>());
    }

    public void setServerClosed(RegisteredServer server, CloseType type, boolean state) {
        List<String> originalDevServers = config.get("dev");
        List<String> originalMaintenanceServers = config.get("maintenance");

        List<String> newDevServers = new ArrayList<>(originalDevServers);
        List<String> newMaintenanceServers = new ArrayList<>(originalMaintenanceServers);

        String serverName = server.getServerInfo().getName();

        if (type == CloseType.DEV) {
            if (!state) {
                newDevServers.remove(serverName);
                newMaintenanceServers.remove(serverName);
            } else {
                if (!newDevServers.contains(serverName))
                    newDevServers.add(serverName);
                newMaintenanceServers.remove(serverName);
            }
        }
        if (type == CloseType.MAINTENANCE) {
            if (state) {
                newDevServers.remove(serverName);
                if (!newMaintenanceServers.contains(serverName))
                    newMaintenanceServers.add(serverName);
            } else {
                newDevServers.remove(serverName);
                newMaintenanceServers.remove(serverName);
            }
        }

        config.set("dev", newDevServers);
        config.set("maintenance", newMaintenanceServers);
        config.save();
    }

    public boolean isServerClosed(RegisteredServer server, CloseType type) {
        String serverName = server.getServerInfo().getName();
        String typeName = "";

        switch (type) {
            case DEV:
                typeName = "dev";
                break;
            case MAINTENANCE:
                typeName = "maintenance";
                break;
            case ANY:
                return ((List<String>) config.get("dev")).contains(serverName) || ((List<String>) config.get("maintenance")).contains(serverName);
        }

        List<String> servers = config.get(typeName);
        if (servers == null) {
            return false;
        }

        return servers.contains(serverName);
    }
}
