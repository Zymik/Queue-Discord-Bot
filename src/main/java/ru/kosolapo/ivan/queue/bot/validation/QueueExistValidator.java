package ru.kosolapo.ivan.queue.bot.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.kosolapo.ivan.queue.bot.data.Request;
import ru.kosolapo.ivan.queue.bot.service.QueueService;
import ru.kosolapo.ivan.queue.bot.util.DiscordUtils;

@Component
@AllArgsConstructor
public class QueueExistValidator implements Validator {
    private final QueueService queueService;

    @Override
    public Mono<?> apply(Request request) {
        String error = "Queue doesn't exist: " + request.get(0);
        return DiscordUtils.getGuildId(request.event())
                .flatMap(id -> queueService.exist(id, request.get(0)))
                .flatMap(x -> Validator.writeAndThrowIllegalArgument(x, request, error,
                        request + ": " + error));
    }
}
