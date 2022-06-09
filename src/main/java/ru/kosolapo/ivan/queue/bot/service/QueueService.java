package ru.kosolapo.ivan.queue.bot.service;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface QueueService {

    Mono<Void> create(String name, Snowflake guild, Message message);

    String getStringOf(Snowflake guild, String name);

    Mono<Member> pollAndGetNext(Snowflake guild, String name);

    Mono<Void> push(Snowflake guild, String name, Member member);

    Mono<Boolean> exist(Snowflake guild, String name);

    Mono<Message> write(Snowflake guild, String name);

    Mono<Void> flush(Snowflake guild, String name);
}
