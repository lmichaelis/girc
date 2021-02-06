package de.lmichaelis.girc.command;

import com.mojang.brigadier.CommandDispatcher;
import de.lmichaelis.girc.GircClient;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
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
        GircClient.sendMessage(
                new LiteralText(" Current Channel: ")
                        .append(new LiteralText(GircClient.CURRENT_CHANNEL).formatted(Formatting.RED))
        );

        GircClient.sendMessage(
                new LiteralText(" Messages are sent to: ")
                        .append(new LiteralText(GircClient.CHAT_TOGGLED ? "the IRC server" : "Minecraft chat").formatted(Formatting.GREEN))
        );

        GircClient.sendMessage(new LiteralText(" Joined Channels: "));

        for (Channel chan : GircClient.IRC_CLIENT.getChannels()) {
            GircClient.sendMessage(new LiteralText("    " + chan.getName()).formatted(Formatting.RED));
        }
    }
}
