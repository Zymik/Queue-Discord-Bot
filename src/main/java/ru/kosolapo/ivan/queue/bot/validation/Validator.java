package ru.kosolapo.ivan.queue.bot.validation;

import reactor.core.publisher.Mono;
import ru.kosolapo.ivan.queue.bot.data.Request;

import java.util.function.Function;

public interface Validator extends Function<Request, Mono<?>> {
    Mono<?> apply(Request request);
    static Mono<?> writeAndThrowIllegalArgument(boolean value, Request request,
                                                String message, String exception) {
        var event = request.event();
        if (!value) {
            return event.getMessage().getChannel()
                    .flatMap(s -> s.createMessage(message))
                    .doOnNext(t -> {
                        throw new IllegalArgumentException(exception);
                    });
        }
        return Mono.empty();

    }
}
