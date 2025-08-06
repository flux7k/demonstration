package io.github.flux7k.demonstration.adapter.paper.in.mute;

import io.github.flux7k.demonstration.application.mute.usecases.MuteUseCase;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class MutePlayerChannelHandler extends ChannelInboundHandlerAdapter {

    private final MuteUseCase muteUseCase;
    private final Player player;

    private static final Logger logger = LoggerFactory.getLogger(MutePlayerChannelHandler.class);

    public MutePlayerChannelHandler(MuteUseCase muteUseCase,
                                    Player player) {
        this.muteUseCase = muteUseCase;
        this.player = player;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof ServerboundChatPacket)) {
            ctx.fireChannelRead(msg);
            return;
        }
        ReferenceCountUtil.retain(msg);
        muteUseCase.isActive(player.getUniqueId())
            .doOnSubscribe(active -> logger.debug("Chat received from player: {}", player.getName()))
            .filter(Boolean::booleanValue)
            .flatMap(active ->
                Mono.fromRunnable(() -> {
                    player.sendRichMessage("<red>You are muted and cannot send messages.");
                    ReferenceCountUtil.release(msg);
                })
            )
            .switchIfEmpty(Mono.fromRunnable(() -> {
                ctx.fireChannelRead(msg);
                logger.debug("Player {} is not muted, message allowed.", player.getName());
            }))
            .timeout(Duration.ofMillis(300))
            .onErrorResume(throwable -> {
                logger.warn("Error while checking mute status for player: {}", player.getName(), throwable);
                return Mono.fromRunnable(() -> ctx.fireChannelRead(msg));
            })
            .subscribe();
    }

}