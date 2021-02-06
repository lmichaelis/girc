package de.lmichaelis.girc;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GircConfig {
    private static final Gson GSON = new Gson();
    private static final File PATH = FabricLoader.getInstance().getConfigDir().resolve("girc.json").toFile();

    @SerializedName("hostname")
    public String server = "localhost";

    @SerializedName("port")
    public int serverPort = 6667;

    @SerializedName("password")
    public String serverPassword = "";

    @SerializedName("channels")
    public List<String> channels = new ArrayList<>();

    @SerializedName("defaultChannel")
    public String defaultChannel = "";

    @SerializedName("nick")
    public String nickname = MinecraftClient.getInstance().getSession().getUsername();

    @SerializedName("fingerprint")
    public String sha1Fingeprint = "";

    @SerializedName("tls")
    public boolean tls = false;

    public static GircConfig load() {
        try (FileReader fr = new FileReader(PATH)) {
            return GSON.fromJson(fr, GircConfig.class);
        } catch (IOException e) {
            return new GircConfig();
        }
    }

    public void save() {
        try (FileWriter fr = new FileWriter(PATH)) {
            GSON.toJson(this, fr);

            GircClient.IRC_CLIENT.shutdown();
            GircClient.ircConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Screen buildConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(new LiteralText("Girc Config"))
                .setParentScreen(parent)
                .setSavingRunnable(this::save);

        ConfigCategory general = builder.getOrCreateCategory(Text.of("General"));

        general.addEntry(builder.entryBuilder()
                .startStrField(Text.of("IRC Server Hostname"), server)
                .setDefaultValue("localhost")
                .setSaveConsumer(newHost -> server = newHost)
                .build());

        general.addEntry(builder.entryBuilder()
                .startIntField(Text.of("IRC Server Port"), serverPort)
                .setDefaultValue(6667)
                .setSaveConsumer(newPort -> serverPort = newPort)
                .build());

        general.addEntry(builder.entryBuilder()
                .startStrField(Text.of("IRC Server Password"), serverPassword)
                .setTooltip(Text.of("Only required if the server you're connecting to requires a password."))
                .setSaveConsumer(newPwd -> serverPassword = newPwd)
                .setDefaultValue("")
                .build());

        general.addEntry(builder.entryBuilder()
                .startStrList(Text.of("IRC Channels"), channels)
                .setTooltip(Text.of("A list of channels to connect to"))
                .setDefaultValue(Collections.emptyList())
                .setSaveConsumer(newChannels -> channels = newChannels)
                .build());

        general.addEntry(builder.entryBuilder()
                .startStrField(Text.of("Default Channel"), defaultChannel)
                .setTooltip(Text.of("The default channel to send messages to"))
                .setSaveConsumer(newDefault -> defaultChannel = newDefault)
                .setDefaultValue("")
                .build());

        general.addEntry(builder.entryBuilder()
                .startStrField(Text.of("Nickname"), nickname)
                .setTooltip(Text.of("Your nickname on the IRC server"))
                .setSaveConsumer(newNick -> nickname = newNick)
                .setDefaultValue(MinecraftClient.getInstance().getSession().getUsername())
                .build());

        general.addEntry(builder.entryBuilder()
                .startBooleanToggle(Text.of("Enable TLS"), tls)
                .setTooltip(Text.of("Enable this if the IRC server you're connecting to has TLS enabled"))
                .setSaveConsumer(newTls -> tls = newTls)
                .setDefaultValue(false)
                .build());

        general.addEntry(builder.entryBuilder()
                .startStrField(Text.of("Server Certificate Fingeprint"), sha1Fingeprint)
                .setTooltip(Text.of("Enter the server's SHA1 TLS-Fingerprint (if you are using a self-signed certificate)"))
                .setSaveConsumer(newFingerprint -> sha1Fingeprint = newFingerprint)
                .setDefaultValue("")
                .build());

        return builder.build();
    }
}
