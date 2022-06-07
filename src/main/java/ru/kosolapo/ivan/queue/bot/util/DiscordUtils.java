package ru.kosolapo.ivan.queue.bot.util;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import java.util.Optional;

public class DiscordUtils {
    public static String tagUser(User user) {
        return "<@" + user.getId().asLong() + ">";
    }

    public static Mono<Member> getAuthor(MessageCreateEvent event) {
        return Mono.fromCallable(event::getMember)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public static Mono<Snowflake> getGuildId(MessageCreateEvent event) {
        return Mono.fromCallable(event::getGuildId)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }
}
