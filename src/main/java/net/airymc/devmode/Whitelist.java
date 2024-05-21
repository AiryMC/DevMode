package net.airymc.devmode;

import com.velocitypowered.api.proxy.Player;
import net.airymc.core.file.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Whitelist {

    private Config config;

    public Whitelist() {
        config = new Config("DevMode/whitelist.yml");
        config.setDefault("whitelist", List.of());
    }

    public void addToWhitelist(UUID uuid) {
        List<String> uuids = config.get("whitelist");
        List<String> whitelist = new ArrayList<>(uuids);
        if (!uuids.contains(uuid.toString())) {
            whitelist.add(uuid.toString());
            config.set("whitelist", whitelist);
            config.save();
        }
    }

    public void removeFromWhitelist(UUID uuid) {
        List<String> uuids = config.get("whitelist");
        List<String> whitelist = new ArrayList<>(uuids);
        whitelist.remove(uuid.toString());
        config.set("whitelist", whitelist);
        config.save();
    }

    public boolean isWhitelisted(UUID uuid) {
        return ((List<String>) config.get("whitelist")).contains(uuid.toString());
    }
}
