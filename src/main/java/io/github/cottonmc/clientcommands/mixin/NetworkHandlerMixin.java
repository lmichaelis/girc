package io.github.cottonmc.clientcommands.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.github.cottonmc.clientcommands.impl.ClientCommands;
import io.github.cottonmc.clientcommands.impl.CommandCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class NetworkHandlerMixin {
    @Shadow private CommandDispatcher<CommandSource> commandDispatcher;

    @Inject(method = "onCommandTree", at = @At("RETURN"))
    private void onCommandTree(CommandTreeS2CPacket packet, CallbackInfo info) {
        addCommands();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onConstruct(MinecraftClient mc, Screen screen, ClientConnection cc, GameProfile gp, CallbackInfo info) {
        addCommands();
        CommandCache.build();
    }

    @Unique
    @SuppressWarnings("unchecked")
    private void addCommands() {
        ClientCommands.getPlugins().forEach(c -> c.registerCommands((CommandDispatcher) commandDispatcher));
    }
}
