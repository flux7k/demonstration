package io.github.flux7k.demonstration.application.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.format.DateTimeFormatter;

@Configuration
public class DateTimeFormatterConfiguration {

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(Clock.systemDefaultZone().getZone());
    }

}
