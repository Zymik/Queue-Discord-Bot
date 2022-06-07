package ru.kosolapo.ivan.queue.bot.bot;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.kosolapo.ivan.queue.bot.configuration.Config;
import ru.kosolapo.ivan.queue.bot.data.Request;
import ru.kosolapo.ivan.queue.bot.data.RequestMetaData;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class DiscordBotImpl implements DiscordBot{

    private Config config;

    @Override
    public void run(Map<RequestMetaData, Function<Request, Mono<?>>> map) {
        DiscordClient client = DiscordClient.create(config.getToken());
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> gateway.on(MessageCreateEvent.class, event -> {
            Message message = event.getMessage();
            String[] s = message.getContent().split(" ");
            if (s.length > 0 && s[0].startsWith(config.getPrefix())) {
                String command = s[0].substring(config.getPrefix().length());
                List<String> args = List.of(Arrays.copyOfRange(s, 1, s.length));
                Request request = new Request(event, command, args);
                RequestMetaData metaData = request.getMetaData();
                var func = map.getOrDefault(metaData,
                        map.get(request.unknownArgumentCountMetaData()));

                if (func != null) {
                   return func.apply(request);
                }
            }
            return Mono.empty();
        }).then());

        login.subscribe();
    }
}
