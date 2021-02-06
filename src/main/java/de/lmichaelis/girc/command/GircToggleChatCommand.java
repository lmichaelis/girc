package de.lmichaelis.girc.command;

import com.mojang.brigadier.CommandDispatcher;
import de.lmichaelis.girc.GircClient;
import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

public class GircToggleChatCommand {
    public static void register(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        dispatcher.register(ArgumentBuilders.literal("irctoggle").executes(context -> {
            toggle();
            return 1;
        }));
    }

    private static void toggle() {
        GircClient.chatToggled = !GircClient.chatToggled;

        GircClient.sendMessage(
                new LiteralText(" All messages are sent to ")
                        .append(new LiteralText(GircClient.chatToggled ? "the IRC server" : "Minecraft chat").formatted(Formatting.GREEN))
                        .append(".")
        );
    }
}
