package net.airymc.devmode;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.airymc.core.file.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

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

        Config config = plugin.getConfig();

        if (args.length == 0) {

        } else if (args.length == 1 && args[0].equalsIgnoreCase("on")) {

            config.set("dev", false);
            config.set("maintenance", true);

            Component component = MiniMessage.miniMessage().deserialize(
                    "<#6BFF43>Maintenance mode is now <#BBBBBB>on<#6BFF43>!"
            );
            source.sendMessage(component);

        } else if (args.length == 1 && args[0].equalsIgnoreCase("off")) {

            config.set("dev", false);
            config.set("maintenance", false);

            Component component = MiniMessage.miniMessage().deserialize(
                    "<#6BFF43>Maintenance mode is now <#BBBBBB>off<#6BFF43>!"
            );
            source.sendMessage(component);
        }
    }
}
