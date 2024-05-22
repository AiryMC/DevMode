package net.airymc.devmode;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.airymc.core.misc.PlayerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;
import java.util.UUID;

public class AiryCommand implements SimpleCommand {

    public static void registerCommand(CommandManager commandManager, Plugin plugin) {
        CommandMeta commandMeta = commandManager.metaBuilder("airymc")
                .plugin(plugin)
                .build();
        commandManager.register(commandMeta, new AiryCommand(plugin));
    }

    private final Plugin plugin;

    public AiryCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        // /airymc whitelist <add|remove> <player> <server>

        if (args.length != 4) {
            Component component = MiniMessage.miniMessage().deserialize(
                    "<#FF5555>Usage: <#BBBBBB>/airymc whitelist <add|remove> <player> <server>"
            );
            source.sendMessage(component);
            return;
        }

        Optional<RegisteredServer> serverOptional = plugin.getServer().getServer(args[3].toLowerCase());
        if (serverOptional.isEmpty()) {
            Component component = MiniMessage.miniMessage().deserialize(
                    "<#FF5555>Could not find server <#BBBBBB>" + args[3].toLowerCase() + "<#FF5555>."
            );
            source.sendMessage(component);
            return;
        }

        RegisteredServer server = serverOptional.get();
        
        if (args[0].equalsIgnoreCase("whitelist")) {
            Optional<UUID> targetUuidOptional = PlayerUtils.getUuidByName(args[2]);

            if (targetUuidOptional.isEmpty()) {
                // TODO: Format message nicely
                source.sendMessage(Component.text("Failed to whitelist player: " + args[2]));
                return;
            }

            UUID targetUuid = targetUuidOptional.get();

            if (args[1].equalsIgnoreCase("add")) {

                plugin.getWhitelist().addToWhitelist(targetUuid, server);

                Component component = MiniMessage.miniMessage().deserialize(
                        "<#6BFF43>Successfully added <#BBBBBB>" + args[2] + " <#6BFF43>to the <#BBBBBB>"  + server.getServerInfo().getName() +" <#6BFF43>whitelist!"
                );
                source.sendMessage(component);
            } else if (args[1].equalsIgnoreCase("remove")) {

                plugin.getWhitelist().removeFromWhitelist(targetUuid, server);

                Component component = MiniMessage.miniMessage().deserialize(
                        "<#6BFF43>Successfully removed <#BBBBBB>" + args[2] + " <#6BFF43>from the <#BBBBBB>"  + server.getServerInfo().getName() +" <#6BFF43>whitelist!"
                );
                source.sendMessage(component);
            }
        }
    }
}
