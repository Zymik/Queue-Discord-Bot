package ru.kosolapo.ivan.queue.bot.bot;

import reactor.core.publisher.Mono;
import ru.kosolapo.ivan.queue.bot.data.Request;
import ru.kosolapo.ivan.queue.bot.data.RequestMetaData;

import java.util.Map;
import java.util.function.Function;

public interface DiscordBot {
    void run(Map<RequestMetaData, Function<Request, Mono<?>>> map);
}
