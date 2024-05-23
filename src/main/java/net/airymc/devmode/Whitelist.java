package net.airymc.devmode;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.airymc.core.file.Config;
import net.airymc.core.misc.PlayerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Whitelist {

    private Config config;

    public Whitelist() {
        config = new Config("DevMode/whitelist.yml");
    }

    public void addToWhitelist(UUID uuid, RegisteredServer server) {
        List<String> servers = config.get(uuid.toString());
        String serverName = server.getServerInfo().getName();

        List<String> newServers;

        if (servers != null)
            newServers = new ArrayList<>(servers);
        else
            newServers = new ArrayList<>();

        if (!newServers.contains(serverName))
            newServers.add(serverName);
        else
            newServers = List.of(serverName);

        config.set(uuid.toString(), newServers);
        config.save();
    }

    public void removeFromWhitelist(UUID uuid, RegisteredServer server) {
        List<String> servers = config.get(uuid.toString());
        String serverName = server.getServerInfo().getName();

        if (servers == null) {
            config.remove(uuid.toString());
            return;
        }

        List<String> newServers = new ArrayList<>(servers);

        newServers.remove(serverName);

        if (newServers.isEmpty())
            config.remove(uuid.toString());
        else
            config.set(uuid.toString(), newServers);
        config.save();
    }

    public boolean isWhitelisted(UUID uuid, RegisteredServer server) {
        if (!config.has(uuid.toString()))
            return false;
        return ((List<String>) config.get(uuid.toString())).contains(server.getServerInfo().getName());
    }

    public List<String> getWhitelistedPlayers() {
        List<String> uuids = config.getKeys("");
        List<String> players = new ArrayList<>();

        for (String strUUID : uuids) {
            UUID uuid = UUID.fromString(strUUID);
            Optional<String> optional = PlayerUtils.getNameByUUID(uuid);
            if (optional.isEmpty())
                continue;

            players.add(optional.get());
        }

        return players;
    }
}
