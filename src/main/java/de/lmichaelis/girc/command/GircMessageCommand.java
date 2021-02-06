package de.lmichaelis.girc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.lmichaelis.girc.GircClient;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class GircMessageCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("i").then(
                ArgumentBuilders.argument("message", StringArgumentType.greedyString()
                ).executes(context -> {
                    String message = StringArgumentType.getString(context, "message");

                    if (GircClient.chatToggled) {
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ChatMessageC2SPacket(message));
                    } else {
                        sendMessage(message);
                    }

                    return 1;
                })
        ));
    }

    public static void sendMessage(String message) {
        if (GircClient.currentChannel != null) {
            GircClient.ircClient.sendMessage(GircClient.currentChannel, message);

            MinecraftClient.getInstance().player.sendMessage(
                    new LiteralText("[")
                            .append(new LiteralText(GircClient.currentChannel).formatted(Formatting.RED))
                            .append("]")
                            .append(new LiteralText(" » ").formatted(Formatting.GRAY))
                            .append(message),
                    false
            );
        } else {
            GircClient.sendMessage(
                    new LiteralText(" No channel set. Use ")
                            .append(new LiteralText("/is <channel>").formatted(Formatting.LIGHT_PURPLE))
                            .append(" to switch to a joined channel or join a channel using ")
                            .append(new LiteralText("/ircjoin <channel>").formatted(Formatting.LIGHT_PURPLE)));
        }
    }
}
