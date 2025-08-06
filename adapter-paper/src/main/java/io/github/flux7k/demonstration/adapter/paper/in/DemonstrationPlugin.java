package io.github.flux7k.demonstration.adapter.paper.in;

import io.github.flux7k.demonstration.application.DemonstrationApplication;
import org.bukkit.plugin.java.JavaPlugin;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class DemonstrationPlugin extends JavaPlugin {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void onEnable() {
        this.applicationContext = new SpringApplicationBuilder(DemonstrationApplication.class)
            .bannerMode(Banner.Mode.LOG)
            .web(WebApplicationType.NONE)
            .headless(true)
            .run();
    }

    @Override
    public void onDisable() {
        applicationContext.close();
    }

}
