package de.lmichaelis.girc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.lmichaelis.girc.GircClient;
import de.lmichaelis.girc.command.type.GircChannelArgumentType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class GircSwitchCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("is")
                .then(ArgumentBuilders.argument("channel", new GircChannelArgumentType()).executes(context -> {
                    switchChannel(context.getArgument("channel", String.class));
                    return 1;
                }))
        );

        dispatcher.register(ArgumentBuilders.literal("ircswitch")
                .then(ArgumentBuilders.argument("channel", new GircChannelArgumentType()).executes(context -> {
                    switchChannel(context.getArgument("channel", String.class));
                    return 1;
                }))
        );
    }

    public static void switchChannel(String newChannel) {
        GircClient.CURRENT_CHANNEL = newChannel;

        GircClient.sendMessage(
                new LiteralText(" Switched to ")
                        .append(new LiteralText(newChannel).formatted(Formatting.RED))
        );
    }

}
