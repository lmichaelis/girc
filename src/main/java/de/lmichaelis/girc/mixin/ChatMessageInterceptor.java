package de.lmichaelis.girc.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.lmichaelis.girc.GircClient;
import de.lmichaelis.girc.command.GircMessageCommand;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import io.github.cottonmc.clientcommands.impl.CommandCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ChatMessageInterceptor {
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;
    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    public abstract void sendMessage(Text text_1, boolean boolean_1);

    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    public void sendChatMessage(String message, CallbackInfo ci) {
        if (message.length() > 1 && message.startsWith("/")) {
            if (CommandCache.hasCommand(message.substring(1).split(" ")[0])) {
                try {
                    // The game freezes when using heavy commands. Run your heavy code somewhere else pls
                    int result = CommandCache.execute(
                            message.substring(1),
                            (CottonClientCommandSource) new ClientCommandSource(networkHandler, client)
                    );
                } catch (CommandException e) {
                    sendMessage(e.getTextMessage().copy().formatted(Formatting.RED), false);
                } catch (CommandSyntaxException e) {
                    sendMessage(new LiteralText(e.getMessage()).formatted(Formatting.RED), false);
                } catch (Exception e) {
                    sendMessage(new TranslatableText("command.failed").formatted(Formatting.RED), false);
                }

                ci.cancel();
                return;
            }

            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ChatMessageC2SPacket(message));
            ci.cancel();
            return;
        }

        if (GircClient.chatToggled) {
            GircMessageCommand.sendMessage(message);
            ci.cancel();
        }
    }
}
