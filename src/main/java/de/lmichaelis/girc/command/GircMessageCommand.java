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

                    if (GircClient.CHAT_TOGGLED) {
                        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ChatMessageC2SPacket(message));
                    } else {
                        sendMessage(message);
                    }

                    return 1;
                })
        ));
    }

    public static void sendMessage(String message) {
        GircClient.IRC_CLIENT.sendMessage(GircClient.CURRENT_CHANNEL, message);

        MinecraftClient.getInstance().player.sendMessage(
                new LiteralText("[")
                        .append(new LiteralText(GircClient.CURRENT_CHANNEL).formatted(Formatting.RED))
                        .append("]")
                        .append(new LiteralText(" » ").formatted(Formatting.GRAY))
                        .append(message),
                false
        );
    }
}
