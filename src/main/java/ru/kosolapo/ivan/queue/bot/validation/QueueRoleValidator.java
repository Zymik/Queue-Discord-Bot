package ru.kosolapo.ivan.queue.bot.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.kosolapo.ivan.queue.bot.data.Request;
import ru.kosolapo.ivan.queue.bot.service.RoleService;
import ru.kosolapo.ivan.queue.bot.util.DiscordUtils;

@AllArgsConstructor
@Component
public class QueueRoleValidator implements Validator {
    private final RoleService roleService;

    @Override
    public Mono<?> apply(Request request) {
        var event = request.event();
        var id = event.getGuildId().get();

        return DiscordUtils.getAuthor(event)
                .flatMap(x -> roleService.hasQueueRole(id, x))
                .flatMap(x -> {
                    if (!x) {
                        return event.getMessage().getChannel()
                                .flatMap(s -> s.createMessage("User don't have queue admin role"))
                                .doOnNext(t -> {
                                    throw new IllegalArgumentException("Do not have queue role: " + request);
                                });
                    }
                    return Mono.empty();
                });

    }
}
