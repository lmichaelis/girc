package de.lmichaelis.girc.mixin;

import de.lmichaelis.girc.GircClient;
import de.lmichaelis.girc.command.GircMessageCommand;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ChatMessageInterceptor {
    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    public void sendChatMessage(String message, CallbackInfo ci)  {
        if (GircClient.chatToggled) {
            GircMessageCommand.sendMessage(message);
            ci.cancel();
        }
    }
}
