package de.lmichaelis.girc.command;

import com.mojang.brigadier.CommandDispatcher;
import de.lmichaelis.girc.GircClient;
import de.lmichaelis.girc.command.type.GircChannelArgumentType;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class GircLeaveCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("ircleave")
                .then(ArgumentBuilders.argument("channel", new GircChannelArgumentType()).executes(context -> {
                    leaveChannel(context.getArgument("channel", String.class));
                    return 1;
                }))
        );
    }

    public static void leaveChannel(String name) {
        GircClient.removeChannel(name);

        GircClient.sendMessage(
                new LiteralText(" Left ")
                        .append(new LiteralText(name).formatted(Formatting.RED))
        );

        GircSwitchCommand.switchChannel(GircClient.ircClient.getChannels().stream().findFirst().get().getName());
    }
}
