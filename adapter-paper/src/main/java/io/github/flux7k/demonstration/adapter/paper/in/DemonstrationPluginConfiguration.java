package io.github.flux7k.demonstration.adapter.paper.in;

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.PluginClassLoader;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemonstrationPluginConfiguration {

    @SuppressWarnings("UnstableApiUsage")
    @Bean
    public Plugin plugin() {
        final var classLoader = getClass().getClassLoader();
        if (classLoader instanceof PluginClassLoader pluginClassLoader) {
            return pluginClassLoader.getPlugin();
        }
        throw new IllegalStateException("Not running in a Bukkit environment");
    }

    @SuppressWarnings("UnstableApiUsage")
    @Bean
    public LifecycleEventManager<@NotNull Plugin> lifecycleEventManager(Plugin plugin) {
        return plugin.getLifecycleManager();
    }

}
