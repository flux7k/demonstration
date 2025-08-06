package io.github.flux7k.demonstration.adapter.paper.in.mute;

import io.github.flux7k.demonstration.application.mute.usecases.MuteUseCase;
import net.minecraft.network.HandlerNames;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.stereotype.Component;

@Component
public class MutePlayerChannelHandlerInjector implements Listener {

    private final MuteUseCase muteUseCase;

    private static final String HANDLER_NAME = "demonstration:mute_handler";

    public MutePlayerChannelHandlerInjector(MuteUseCase muteUseCase) {
        this.muteUseCase = muteUseCase;
    }

    @EventHandler
    public void inject(PlayerJoinEvent event) {
        if (event.getPlayer() instanceof CraftPlayer craftPlayer) {
            inject(craftPlayer);
        }
    }

    private void inject(CraftPlayer player) {
        final var handler = new MutePlayerChannelHandler(muteUseCase, player);
        final var pipeline = player.getHandle().connection.connection.channel.pipeline();
        pipeline.addBefore(HandlerNames.PACKET_HANDLER, HANDLER_NAME, handler);
    }

    @EventHandler
    public void eject(PlayerQuitEvent event) {
        if (event.getPlayer() instanceof CraftPlayer craftPlayer) {
            eject(craftPlayer);
        }
    }

    private void eject(CraftPlayer player) {
        final var pipeline = player.getHandle().connection.connection.channel.pipeline();
        if (pipeline.get(HANDLER_NAME) != null) {
            pipeline.remove(HANDLER_NAME);
        }
    }

}