package io.github.flux7k.demonstration.application.mute;

import io.github.flux7k.demonstration.domain.mute.DefaultMutePolicy;
import io.github.flux7k.demonstration.domain.mute.MutePolicy;
import io.github.flux7k.demonstration.domain.mute.MuteService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class MuteDomainWiring {

    @Bean
    public MuteService muteService(MutePolicy mutePolicy, Clock clock) {
        return new MuteService(mutePolicy, clock);
    }

    @Bean
    public MutePolicy mutePolicy() {
        return new DefaultMutePolicy();
    }

}