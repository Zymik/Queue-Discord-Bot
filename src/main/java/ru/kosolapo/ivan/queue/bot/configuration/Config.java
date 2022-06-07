package ru.kosolapo.ivan.queue.bot.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class Config {
    @Value ("${token}")
    private String token;

    @Value("${prefix}")
    private String prefix;
}
