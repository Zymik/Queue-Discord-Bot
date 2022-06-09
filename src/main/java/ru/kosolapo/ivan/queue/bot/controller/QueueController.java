package ru.kosolapo.ivan.queue.bot.controller;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;
import ru.kosolapo.ivan.queue.bot.data.Request;
import ru.kosolapo.ivan.queue.bot.proccessing.annotation.Command;
import ru.kosolapo.ivan.queue.bot.proccessing.annotation.DiscordController;
import ru.kosolapo.ivan.queue.bot.service.QueueService;
import ru.kosolapo.ivan.queue.bot.util.DiscordUtils;
import ru.kosolapo.ivan.queue.bot.validation.annotation.NotBot;
import ru.kosolapo.ivan.queue.bot.validation.annotation.QueueExist;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@DiscordController
@AllArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @Getter
    private final Map<Snowflake, Map<String, ConcurrentLinkedQueue<Member>>> users = new HashMap<>();

    @Command(value = "join", count = 1)
    @QueueExist
    @NotBot
    public Mono<Message> add(Request request) {
        var event = request.event();
        String s = request.get(0);
        return DiscordUtils
                .getAuthor(event)
                .flatMap(x -> DiscordUtils.getGuildId(event).flatMap(id ->
                        queueService.push(id, s, x)))
                .then(DiscordUtils.getGuildId(event)
                        .flatMap(id -> queueService.write(id, s)));
    }

    @Command(value = "create", count = 1)
    @NotBot
    public Mono<Void> create(Request request) {
        var event = request.event();
        String name = request.get(0);
        return event.getMessage().getChannel()
                .flatMap(x -> x.createMessage("Queue: " + name))
                .flatMap(message -> DiscordUtils
                        .getGuildId(event)
                        .flatMap(id -> queueService.create(name, id, message)));

    }

    @Command(value = "remove", count = 1)
    @QueueExist
    @NotBot
    public Mono<Void> remove(Request request) {
        var event = request.event();
        String s = request.get(0);
        return DiscordUtils
                .getAuthor(event)
                .flatMap(x -> DiscordUtils.getGuildId(event)
                        .flatMap(id -> queueService.pollAndGetNext(id, s)
                                .flatMap(member -> queueService.write(id, s)
                                        .flatMap(m -> m.getChannel()
                                                .flatMap(channel -> channel.createMessage(getNotify(s, member)))
                                                .delayElement(Duration.ofMillis(1000))
                                                .flatMap(Message::delete)))));

    }

    @Command(value = "flush", count = 1)
    @QueueExist
    @NotBot
    public Mono<Message> flush(Request request) {
        var event = request.event();
        String s = request.get(0);
        return DiscordUtils.getGuildId(event).flatMap(id ->
                queueService.flush(id, s)
        ).then(DiscordUtils.getGuildId(event)
                .flatMap(id -> queueService.write(id, s)));
    }

    private String getNotify(String s, Member member) {
        return "Next in "
                + s
                + ": "
                + DiscordUtils.tagUser(member);
    }
}
