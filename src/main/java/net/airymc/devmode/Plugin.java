package net.airymc.devmode;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.airymc.core.file.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.slf4j.Logger;

import java.util.Optional;

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
    private Servers servers;
    private RegisteredServer defaultServer;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        config = new Config("config.yml", "DevMode/config.yml");
        whitelist = new Whitelist();
        servers = new Servers();

        Optional<RegisteredServer> serverOptional = server.getServer(config.get("default-server"));
        serverOptional.ifPresent(registeredServer -> defaultServer = registeredServer);
        if (defaultServer == null) {
            logger.error("Default server {} not found. Please check the config and try again.", Optional.ofNullable(config.get("default-server")));
        }

        CommandManager commandManager = server.getCommandManager();
        DevelopmentCommand.registerCommand(commandManager, this);
        MaintenanceCommand.registerCommand(commandManager, this);
        WhitelistCommand.registerCommand(commandManager, this);
    }

    @Subscribe
    public void onPlayerPreConnect(ServerPreConnectEvent event) {

        if (event.getPlayer().hasPermission("devmode.bypass"))
            return;

        if (whitelist.isWhitelisted(event.getPlayer().getUniqueId(), event.getOriginalServer()))
            return;


        // Check if server is closed for development
        if (servers.isServerClosed(event.getOriginalServer(), CloseType.DEV)) {
            Component component = MiniMessage.miniMessage().deserialize(config.get("dev-kick-message"));

            if (defaultServer == null) {
                event.getPlayer().disconnect(component);
                return;
            }

            if (event.getOriginalServer() == defaultServer) {
                event.getPlayer().disconnect(component);
            }

            event.setResult(ServerPreConnectEvent.ServerResult.denied());

            Component deniedComponent = MiniMessage.miniMessage().deserialize(
                    config.get("dev-denied-message")
            );
            event.getPlayer().sendMessage(deniedComponent);

            return;
        }

        // Check if server is closed for maintenance
        if (servers.isServerClosed(event.getOriginalServer(), CloseType.MAINTENANCE)) {
            Component component = MiniMessage.miniMessage().deserialize(config.get("maintenance-kick-message"));

            if (defaultServer == null) {
                event.getPlayer().disconnect(component);
                return;
            }

            if (event.getOriginalServer() == defaultServer) {
                event.getPlayer().disconnect(component);
            }

            event.setResult(ServerPreConnectEvent.ServerResult.denied());

            Component deniedComponent = MiniMessage.miniMessage().deserialize(
                    config.get("maintenance-denied-message")
            );
            event.getPlayer().sendMessage(deniedComponent);

            return;
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

    public Servers getServers() {
        return servers;
    }

    public RegisteredServer getDefaultServer() {
        return defaultServer;
    }
}
