package de.lmichaelis.girc.mixin;

import de.lmichaelis.girc.GircClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class WorldJoinInteceptor {
    @Inject(at = @At("TAIL"), method = "onGameJoin")
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (GircClient.CURRENT_CHANNEL != null) {
            GircClient.sendMessage(
                    new LiteralText(" You are talking in ")
                            .append(new LiteralText(GircClient.CURRENT_CHANNEL).formatted(Formatting.RED))
            );
        }
    }
}
