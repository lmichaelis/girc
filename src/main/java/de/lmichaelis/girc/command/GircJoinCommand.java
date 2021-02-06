package de.lmichaelis.girc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.lmichaelis.girc.GircClient;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class GircJoinCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("ircjoin")
                .then(ArgumentBuilders.argument("channel", StringArgumentType.greedyString()).executes(context -> {
                    joinChannel(StringArgumentType.getString(context, "channel"));
                    return 1;
                }))
        );
    }

    public static void joinChannel(String name) {
        GircClient.addChannel(name);

        GircClient.sendMessage(
                new LiteralText(" Joined ")
                        .append(new LiteralText(name).formatted(Formatting.RED))
        );

        GircSwitchCommand.switchChannel(name);
        // TODO: Add to config
    }
}
