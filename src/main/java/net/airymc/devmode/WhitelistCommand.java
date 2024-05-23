package net.airymc.devmode;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.airymc.core.misc.PlayerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WhitelistCommand implements SimpleCommand {

    public static void registerCommand(CommandManager commandManager, Plugin plugin) {
        CommandMeta commandMeta = commandManager.metaBuilder("whitelist")
                .aliases("wl")
                .plugin(plugin)
                .build();
        commandManager.register(commandMeta, new WhitelistCommand(plugin));
    }

    private final Plugin plugin;

    public WhitelistCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length != 3 || !(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {

            Component component = MiniMessage.miniMessage().deserialize(
                    "<#FF5555>Usage: <#BBBBBB>/whitelist <add|remove> <player> <server>"
            );
            source.sendMessage(component);
            return;
        }

        Optional<RegisteredServer> serverOptional = plugin.getServer().getServer(args[2].toLowerCase());
        if (serverOptional.isEmpty()) {

            Component component = MiniMessage.miniMessage().deserialize(
                    "<#FF5555>Could not find server <#BBBBBB>" + args[2].toLowerCase() + "<#FF5555>."
            );
            source.sendMessage(component);

            return;
        }

        RegisteredServer server = serverOptional.get();

        Optional<UUID> targetUuidOptional = PlayerUtils.getUUIDByName(args[1]);

        if (targetUuidOptional.isEmpty()) {
            Component component = MiniMessage.miniMessage().deserialize(
                    args[0].equalsIgnoreCase("add") ?
                            "<#FF5555>Could not whitelist <#BBBBBB>" + args[1] + " <#FF5555>on server <#BBBBBB>" + server.getServerInfo().getName() + "<#FF5555>." :
                            "<#FF5555>Could not remove <#BBBBBB>" + args[1] + " <#FF5555>from whitelist on server <#BBBBBB>" + server.getServerInfo().getName() + "<#FF5555>."

            );
            source.sendMessage(component);

            return;
        }

        UUID targetUuid = targetUuidOptional.get();

        if (args[0].equalsIgnoreCase("add")) {

            plugin.getWhitelist().addToWhitelist(targetUuid, server);

            Component component = MiniMessage.miniMessage().deserialize(
                    "<#6BFF43>Successfully added <#BBBBBB>" + args[1] + " <#6BFF43>to the <#BBBBBB>" + server.getServerInfo().getName() + " <#6BFF43>whitelist!"
            );
            source.sendMessage(component);

        } else if (args[0].equalsIgnoreCase("remove")) {

            plugin.getWhitelist().removeFromWhitelist(targetUuid, server);

            Optional<Player> playerOptional = plugin.getServer().getPlayer(targetUuid);
            if (playerOptional.isPresent()) {

                Player player = playerOptional.get();
                Optional<ServerConnection> serverConnectionOptional = player.getCurrentServer();

                if (serverConnectionOptional.isPresent()) {

                    RegisteredServer playerServer = serverConnectionOptional.get().getServer();

                    if (playerServer == server) {

                        Servers servers = plugin.getServers();

                        Component component;
                        if (servers.isServerClosed(server, CloseType.DEV))
                            component = MiniMessage.miniMessage().deserialize(plugin.getConfig().get("dev-kick-message"));
                        else
                            component = MiniMessage.miniMessage().deserialize(plugin.getConfig().get("maintenance-kick-message"));

                        if (servers.isServerClosed(server, CloseType.ANY))
                            player.disconnect(component);
                    }
                }
            }

            Component component = MiniMessage.miniMessage().deserialize(
                    "<#6BFF43>Successfully removed <#BBBBBB>" + args[1] + " <#6BFF43>from the <#BBBBBB>" + server.getServerInfo().getName() + " <#6BFF43>whitelist!"
            );
            source.sendMessage(component);
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("devmode.command.whitelist");
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length == 0) {
            return CompletableFuture.completedFuture(List.of("add", "remove"));
        }

        if (args.length == 2) {
            return CompletableFuture.completedFuture(plugin.getWhitelist().getWhitelistedPlayers());
        }

        if (args.length == 3) {
            List<String> servers = new ArrayList<>();
            for (RegisteredServer server : plugin.getServer().getAllServers()) {
                servers.add(server.getServerInfo().getName());
            }
            return CompletableFuture.completedFuture(servers);
        }

        return CompletableFuture.completedFuture(List.of());
    }
}
