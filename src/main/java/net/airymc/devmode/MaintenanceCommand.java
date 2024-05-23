package net.airymc.devmode;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.ConnectionRequestBuilder;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MaintenanceCommand implements SimpleCommand {

    public static void registerCommand(CommandManager commandManager, Plugin plugin) {
        CommandMeta commandMeta = commandManager.metaBuilder("maintenance")
                .plugin(plugin)
                .build();
        commandManager.register(commandMeta, new MaintenanceCommand(plugin));
    }

    private final Plugin plugin;

    public MaintenanceCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length != 2) {
            Component component = MiniMessage.miniMessage().deserialize(
                    "<#FF5555>Usage: <#BBBBBB>/maintenance <server> <on|off>"
            );
            source.sendMessage(component);
            return;
        }

        Optional<RegisteredServer> server = plugin.getServer().getServer(args[0].toLowerCase());
        if (server.isEmpty()) {
            Component component = MiniMessage.miniMessage().deserialize(
                    "<#FF5555>Could not find server <#BBBBBB>" + args[0].toLowerCase() + "<#FF5555>."
            );
            source.sendMessage(component);
            return;
        }

        if (args[1].equalsIgnoreCase("on")) {
            maintenanceOn(source, server.get());
        }
        else if (args[1].equalsIgnoreCase("off")) {
            maintenanceOff(source, server.get());
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("devmode.command.maintenance");
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length == 0) {
            List<String> servers = new ArrayList<>();
            for (RegisteredServer server : plugin.getServer().getAllServers()) {
                servers.add(server.getServerInfo().getName());
            }
            return CompletableFuture.completedFuture(servers);
        }

        if (args.length == 2) {
            return CompletableFuture.completedFuture(List.of("on", "off"));
        }

        return CompletableFuture.completedFuture(List.of());
    }

    private void maintenanceOn(CommandSource source, RegisteredServer server) {
        plugin.getServers().setServerClosed(server, CloseType.MAINTENANCE, true);

        for (Player player : server.getPlayersConnected()) {
            if (plugin.getWhitelist().isWhitelisted(player.getUniqueId(), server))
                continue;

            Optional<ServerConnection> serverConnection = player.getCurrentServer();
            if (serverConnection.isPresent()) {
                RegisteredServer currentServer = serverConnection.get().getServer();

                if (currentServer == plugin.getDefaultServer()) {
                    Component component = MiniMessage.miniMessage().deserialize(plugin.getConfig().get("maintenance-kick-message"));
                    player.disconnect(component);
                } else {

                    ConnectionRequestBuilder builder = player.createConnectionRequest(plugin.getDefaultServer());
                    builder.connect().thenAccept(result -> {

                        Component deniedComponent = MiniMessage.miniMessage().deserialize(
                                plugin.getConfig().get("maintenance-denied-message")
                        );
                        player.sendMessage(deniedComponent);

                    });
                }
            }
        }

        Component component = MiniMessage.miniMessage().deserialize(
                "<#6BFF43>Development mode is now set to <#BBBBBB>on <#6BFF43>for <#BBBBBB>" + server.getServerInfo().getName() + "<#6BFF43>."
        );
        source.sendMessage(component);
    }

    private void maintenanceOff(CommandSource source, RegisteredServer server) {
        plugin.getServers().setServerClosed(server, CloseType.MAINTENANCE, false);

        Component component = MiniMessage.miniMessage().deserialize(
                "<#6BFF43>Development mode is now set to <#BBBBBB>off <#6BFF43>for <#BBBBBB>" + server.getServerInfo().getName() + "<#6BFF43>."
        );
        source.sendMessage(component);
    }
}
