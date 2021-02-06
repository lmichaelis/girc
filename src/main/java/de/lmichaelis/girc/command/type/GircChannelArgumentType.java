package de.lmichaelis.girc.command.type;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.lmichaelis.girc.GircClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class GircChannelArgumentType implements ArgumentType<String> {
    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        final String s = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());

        if (!GircClient.ircClient.getChannel(s).isPresent()) {
            throw new CommandSyntaxException(
                    new SimpleCommandExceptionType(new LiteralMessage("2")),
                    new LiteralMessage("Channel does not exist.")
            );
        }

        return s;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        GircClient.ircClient.getChannels().stream().forEach(channel -> builder.suggest(channel.getName()));
        return CompletableFuture.completedFuture(builder.build());
    }

    @Override
    public Collection<String> getExamples() {
        return new ArrayList<>();
    }
}
