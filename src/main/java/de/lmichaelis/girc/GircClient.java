package de.lmichaelis.girc;

import io.netty.handler.ssl.util.FingerprintTrustManagerFactory;
import net.engio.mbassy.listener.Handler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;

import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;

@Environment(EnvType.CLIENT)
public class GircClient implements ClientModInitializer {
    public static Client IRC_CLIENT;
    public static String CURRENT_CHANNEL;
    public static boolean CHAT_TOGGLED = false;
    public static GircConfig CONFIG;


    public static void sendMessage(Text text) {
        MinecraftClient.getInstance().player.sendMessage(
                new LiteralText("[")
                        .append(new LiteralText("IRC").formatted(Formatting.AQUA))
                        .append("]")
                        .append(text),
                false
        );
    }

    public static void ircConnect() {
        try {
            TrustManagerFactory f = null;

            if (!CONFIG.sha1Fingeprint.isEmpty()) {
                f = new FingerprintTrustManagerFactory(CONFIG.sha1Fingeprint);
            }

            IRC_CLIENT = Client.builder()
                    .nick(CONFIG.nickname)
                    .server()
                    .host(CONFIG.server)
                    .password(CONFIG.serverPassword.isEmpty() ? null : CONFIG.serverPassword)
                    .port(
                            CONFIG.serverPort,
                            CONFIG.tls ? Client.Builder.Server.SecurityType.SECURE : Client.Builder.Server.SecurityType.INSECURE
                    )
                    .secureTrustManagerFactory(f)
                    .then()
                    .buildAndConnect();

            IRC_CLIENT.getEventManager().registerEventListener(new IrcEventListener());

            for (String chan : CONFIG.channels) {
                IRC_CLIENT.addChannel(chan);
            }

            if (!CONFIG.defaultChannel.isEmpty()) {
                CURRENT_CHANNEL = CONFIG.defaultChannel;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void addChannel(String channel) {
        IRC_CLIENT.addChannel(channel);
        CONFIG.channels.add(channel);
        CONFIG.save();
    }

    public static void removeChannel(String channel) {
        IRC_CLIENT.removeChannel(channel);
        CONFIG.channels.remove(channel);
        CONFIG.save();
    }

    @Override
    public void onInitializeClient() {
        CONFIG = GircConfig.load();
        ircConnect();
    }

    public static class IrcEventListener {
        @Handler
        public void onChannelMessage(ChannelMessageEvent event) {
            if (MinecraftClient.getInstance().player == null) {
                // We just don't send a message if the player is not currently in a game
                return;
            }

            MinecraftClient.getInstance().player.sendMessage(
                    new LiteralText("[")
                            .append(new LiteralText(event.getChannel().getName()).formatted(Formatting.RED))
                            .append("] ")
                            .append(event.getActor().getNick())
                            .append(new LiteralText(" » ").formatted(Formatting.GRAY))
                            .append(event.getMessage()),
                    false
            );
        }
    }
}
