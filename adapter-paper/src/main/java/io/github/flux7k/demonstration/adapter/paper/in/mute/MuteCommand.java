package io.github.flux7k.demonstration.adapter.paper.in.mute;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.flux7k.demonstration.adapter.paper.in.PaperCommandSpec;
import io.github.flux7k.demonstration.application.mute.usecases.MuteUseCase;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@Component
public class MuteCommand implements PaperCommandSpec {

    private final MuteUseCase muteUseCase;

    public MuteCommand(MuteUseCase muteUseCase) {
        this.muteUseCase = muteUseCase;
    }

    @Override
    public List<String> getAliases() {
        return List.of("silence");
    }

    public String getDescription() {
        return "Mute a player for a specified duration with a reason.";
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getNode() {
        return Commands.literal("mute")
            .then(Commands.argument("target", ArgumentTypes.player())
                .then(Commands.argument("seconds", LongArgumentType.longArg(0))
                    .then(Commands.argument("reason", StringArgumentType.greedyString())
                        .executes(context -> {
                            final var executor = context.getSource().getExecutor();
                            if (executor == null) {
                                return 0;
                            }
                            final var target = context
                                .getArgument("target", PlayerSelectorArgumentResolver.class)
                                .resolve(context.getSource())
                                .getFirst();
                            final var seconds = LongArgumentType.getLong(context, "seconds");
                            final var reason = StringArgumentType.getString(context, "reason");
                            final var duration = Duration.ofSeconds(seconds);
                            muteUseCase
                                .issue(executor.getUniqueId(), target.getUniqueId(), reason, duration)
                                .subscribe();
                            return Command.SINGLE_SUCCESS;
                        })
                    )
                )
            )
            .build();
    }

}