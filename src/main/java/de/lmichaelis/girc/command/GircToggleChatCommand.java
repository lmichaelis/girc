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
        GircClient.CHAT_TOGGLED = !GircClient.CHAT_TOGGLED;

        GircClient.sendMessage(
                new LiteralText(" All messages are sent to ")
                        .append(new LiteralText(GircClient.CHAT_TOGGLED ? "the IRC server" : "Minecraft chat").formatted(Formatting.GREEN))
                        .append(".")
        );
    }
}
