package net.airymc.devmode;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
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

        if (args.length == 3
        && args[0].equalsIgnoreCase("whitelist")) {
            Optional<UUID> targetUuid = PlayerUtils.getUuidByName(args[2]);

            if (!targetUuid.isPresent()) {
                source.sendMessage(Component.text("Failed to whitelist player: " + args[2]));
                return;
            }
            if (args[1].equalsIgnoreCase("add")) {

                plugin.getWhitelist().addToWhitelist(targetUuid.get());

                Component component = MiniMessage.miniMessage().deserialize(
                        "<#6BFF43>Successfully added <#BBBBBB>" + args[2] + " <#6BFF43>to the whitelist!"
                );
                source.sendMessage(component);
            }
            else if (args[1].equalsIgnoreCase("remove")) {

                plugin.getWhitelist().removeFromWhitelist(targetUuid.get());

                Component component = MiniMessage.miniMessage().deserialize(
                        "<#6BFF43>Successfully removed <#BBBBBB>" + args[2] + " <#6BFF43>to the whitelist!"
                );
                source.sendMessage(component);
            }
        }
    }
}
