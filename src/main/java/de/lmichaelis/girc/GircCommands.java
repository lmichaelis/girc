package de.lmichaelis.girc;

import com.mojang.brigadier.CommandDispatcher;
import de.lmichaelis.girc.command.*;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;

public class GircCommands implements ClientCommandPlugin {
    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> dispatcher) {
        GircMessageCommand.register(dispatcher);
        GircSwitchCommand.register(dispatcher);
        GircJoinCommand.register(dispatcher);
        GircLeaveCommand.register(dispatcher);
        GircInfoCommand.register(dispatcher);
        GircToggleChatCommand.register(dispatcher);
    }
}
