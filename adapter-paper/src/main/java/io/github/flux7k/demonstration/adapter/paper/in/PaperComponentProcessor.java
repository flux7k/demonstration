package io.github.flux7k.demonstration.adapter.paper.in;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import jakarta.annotation.PostConstruct;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class PaperComponentProcessor {

    private final Plugin plugin;
    private final Iterable<Listener> listeners;
    private final Iterable<PaperCommandSpec> commands;

    public PaperComponentProcessor(Plugin plugin,
                                   ObjectProvider<Listener> listeners,
                                   ObjectProvider<PaperCommandSpec> commands) {
        this.plugin = plugin;
        this.listeners = listeners;
        this.commands = commands;
    }

    @PostConstruct
    public void registerListeners() {
        final var pluginManager = plugin.getServer().getPluginManager();
        for (final var listener : listeners) {
            pluginManager.registerEvents(listener, plugin);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @PostConstruct
    public void registerCommands() {
        for (final var command : commands) {
            plugin.getLifecycleManager()
                .registerEventHandler(
                    LifecycleEvents.COMMANDS,
                    event -> event.registrar().register(command.getNode(), command.getDescription(), command.getAliases())
                );
        }
    }

}