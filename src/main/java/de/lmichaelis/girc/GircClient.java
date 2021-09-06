package de.lmichaelis.girc;

import io.netty.handler.ssl.util.FingerprintTrustManagerFactory;
import net.engio.mbassy.listener.Handler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionEstablishedEvent;
import org.kitteh.irc.client.library.event.connection.ClientConnectionFailedEvent;
import org.kitteh.irc.client.library.event.helper.ConnectionEvent;

import javax.net.ssl.TrustManagerFactory;

@Environment(EnvType.CLIENT)
public class GircClient implements ClientModInitializer {
    public static Client ircClient = null;
    public static String currentChannel;
    public static boolean chatToggled = false;
    public static boolean errorSent = false;
    public static boolean connected = false;
    public static GircConfig config;


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
        if (ircClient != null) {
            return;
        }

        try {
            TrustManagerFactory f = null;

            if (!config.sha1Fingeprint.isEmpty()) {
                f = new FingerprintTrustManagerFactory(config.sha1Fingeprint);
            }

            ircClient = Client.builder()
                    .nick(MinecraftClient.getInstance().getSession().getUsername())
                    .server()
                    .host(config.server)
                    .password(config.serverPassword.isEmpty() ? null : config.serverPassword)
                    .port(
                            config.serverPort,
                            config.tls ? Client.Builder.Server.SecurityType.SECURE : Client.Builder.Server.SecurityType.INSECURE
                    )
                    .secureTrustManagerFactory(f)
                    .then()
                    .build();

            ircClient.getEventManager().registerEventListener(new IrcEventListener());
            ircClient.connect();

            for (String chan : config.channels) {
                ircClient.addChannel(chan);
            }

            if (!config.defaultChannel.isEmpty()) {
                currentChannel = config.defaultChannel;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void addChannel(String channel) {
        ircClient.addChannel(channel);

        if (currentChannel == null) {
            config.defaultChannel = channel;
        }

        config.channels.add(channel);
        config.save();
    }

    public static void removeChannel(String channel) {
        ircClient.removeChannel(channel);
        config.channels.remove(channel);
        config.save();
    }

    @Override
    public void onInitializeClient() {
        config = GircConfig.load();
    }

    public static void notifyError() {
        if (!errorSent) {
            MinecraftClient.getInstance().getToastManager().add(
                    new SystemToast(
                            SystemToast.Type.NARRATOR_TOGGLE,
                            Text.of("IRC connection failed!"),
                            Text.of("Connection was closed.")
                    )
            );
        }
        errorSent = true;
    }

    public static class IrcEventListener {
        @Handler
        public void onConnect(ClientConnectionEstablishedEvent event) {
            MinecraftClient.getInstance().getToastManager().add(
                    new SystemToast(
                            SystemToast.Type.NARRATOR_TOGGLE,
                            Text.of("IRC Connected."),
                            Text.of("Fun chatting!")
                    )
            );

            connected = true;
            errorSent = false;
        }

        @Handler
        public void onConnectFailed(ClientConnectionFailedEvent event) {
            connected = false;
            notifyError();
        }

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
