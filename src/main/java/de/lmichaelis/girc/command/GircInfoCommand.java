package de.lmichaelis.girc.command;

import com.mojang.brigadier.CommandDispatcher;
import de.lmichaelis.girc.GircClient;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.kitteh.irc.client.library.element.Channel;

public class GircInfoCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("ircinfo").executes(context -> {
            sendMessages();
            return 1;
        }));
    }

    private static void sendMessages() {
        if (GircClient.connected) {
            GircClient.sendMessage(
                    new LiteralText(" Current Channel: ")
                            .append(new LiteralText(GircClient.currentChannel).formatted(Formatting.RED))
            );

            GircClient.sendMessage(
                    new LiteralText(" Messages are sent to: ")
                            .append(new LiteralText(GircClient.chatToggled ? "the IRC server" : "Minecraft chat").formatted(Formatting.GREEN))
            );

            GircClient.sendMessage(new LiteralText(" Joined Channels: "));

            for (Channel chan : GircClient.ircClient.getChannels()) {
                GircClient.sendMessage(new LiteralText("    " + chan.getName()).formatted(Formatting.RED));
            }
        } else {
            GircClient.sendMessage(new LiteralText(" Not connected."));
        }
    }
}
