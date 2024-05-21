package net.airymc.devmode;

import com.velocitypowered.api.command.*;
import net.airymc.core.file.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class DevelopmentCommand implements SimpleCommand {

    public static void registerCommand(CommandManager commandManager, Plugin plugin) {
        CommandMeta commandMeta = commandManager.metaBuilder("development")
                .aliases("dev")
                .plugin(plugin)
                .build();
        commandManager.register(commandMeta, new DevelopmentCommand(plugin));
    }

    private final Plugin plugin;

    public DevelopmentCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        Config config = plugin.getConfig();

        if (args.length == 0) {

        } else if (args.length == 1 && args[0].equalsIgnoreCase("on")) {

            config.set("dev", true);
            config.set("maintenance", false);

            Component component = MiniMessage.miniMessage().deserialize(
                    "<#6BFF43>Development mode is now <#BBBBBB>on<#6BFF43>!"
            );
            source.sendMessage(component);

        } else if (args.length == 1 && args[0].equalsIgnoreCase("off")) {

            config.set("dev", false);
            config.set("maintenance", false);

            Component component = MiniMessage.miniMessage().deserialize(
                    "<#6BFF43>Development mode is now <#BBBBBB>off<#6BFF43>!"
            );
            source.sendMessage(component);
        }
    }
}
