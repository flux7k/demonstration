package io.github.flux7k.demonstration.adapter.paper.out;

import io.github.flux7k.demonstration.application.notifier.ports.out.Notifier;
import org.bukkit.Bukkit;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class PaperNotifier implements Notifier {

    @Override
    public Mono<Void> notify(UUID targetId, String message) {
        final var player = Bukkit.getPlayer(targetId);
        if (player == null || !player.isOnline()) {
            return Mono.empty();
        }
        return Mono.fromRunnable(() -> player.sendMessage(message));
    }

}