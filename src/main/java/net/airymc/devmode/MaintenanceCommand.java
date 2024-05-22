package net.airymc.devmode;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;

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

    private void maintenanceOn(CommandSource source, RegisteredServer server) {
        plugin.getServers().setServerClosed(server, CloseType.MAINTENANCE, true);

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
