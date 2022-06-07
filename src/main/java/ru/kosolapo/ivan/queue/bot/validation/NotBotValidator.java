package ru.kosolapo.ivan.queue.bot.validation;

import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.kosolapo.ivan.queue.bot.data.Request;
import ru.kosolapo.ivan.queue.bot.util.DiscordUtils;


@Component
public class NotBotValidator implements Validator {


    @Override
    public Mono<?> apply(Request request) {
        var event = request.event();
        return DiscordUtils.getAuthor(event)
                .flatMap(author ->
                        Mono.just(author.isBot())
                                .flatMap(x -> Validator.writeAndThrowIllegalArgument(
                                        x, request,
                                        author.getDisplayName() + " is bot",
                                        request + " :" + author.getId() + " is bot")));
    }
}
