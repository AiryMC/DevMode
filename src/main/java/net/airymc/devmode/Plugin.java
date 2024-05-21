package net.airymc.devmode;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import net.airymc.core.file.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

@com.velocitypowered.api.plugin.Plugin(
        id = "devmode",
        name = "DevMode",
        version = BuildConstants.VERSION,
        authors = {"AiryyCodes"}
)
public class Plugin {

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer server;

    @Inject
    private PluginContainer plugin;

    private Config config;
    private Whitelist whitelist;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        config = new Config("config.yml", "DevMode/config.yml");
        whitelist = new Whitelist();

        CommandManager commandManager = server.getCommandManager();
        DevelopmentCommand.registerCommand(commandManager, this);
        MaintenanceCommand.registerCommand(commandManager, this);
        AiryCommand.registerCommand(commandManager, this);
    }

    @Subscribe
    public void onPlayerJoin(ServerPreConnectEvent event) {
        if ((boolean) config.get("dev")) {
            if (!whitelist.isWhitelisted(event.getPlayer().getUniqueId())) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                Component component = MiniMessage.miniMessage().deserialize(config.get("dev-kick-message"));
                event.getPlayer().disconnect(component);
            }
        }
        if ((boolean) config.get("maintenance")) {
            if (!whitelist.isWhitelisted(event.getPlayer().getUniqueId())) {
                event.setResult(ServerPreConnectEvent.ServerResult.denied());
                Component component = MiniMessage.miniMessage().deserialize(config.get("maintenance-kick-message"));
                event.getPlayer().disconnect(component);
            }
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public PluginContainer getPlugin() {
        return plugin;
    }

    public Config getConfig() {
        return config;
    }

    public Whitelist getWhitelist() {
        return whitelist;
    }
}
