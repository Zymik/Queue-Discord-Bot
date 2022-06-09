package ru.kosolapo.ivan.queue.bot.service;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.kosolapo.ivan.queue.bot.data.QueueIdentifier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

@Service
public class QueueServiceInMemory implements QueueService {

    private final ConcurrentMap<QueueIdentifier, ConcurrentLinkedQueue<Member>> queues = new ConcurrentHashMap<>();
    private final ConcurrentMap<QueueIdentifier, Message> messages = new ConcurrentHashMap<>();

    private ConcurrentLinkedQueue<Member> get(Snowflake guild, String name) {
        return queues.get(new QueueIdentifier(guild, name));
    }

    @Override
    public Mono<Void> create(String name, Snowflake guild, Message message) {
        System.out.println("Create");
        return Mono.fromRunnable(() -> {
            var id = new QueueIdentifier(guild, name);
            queues.putIfAbsent(id, new ConcurrentLinkedQueue<>());
            messages.putIfAbsent(id, message);
        });
    }

    @Override
    public String getStringOf(Snowflake guild, String name) {
        var queue = get(guild, name);
        StringBuilder sb = new StringBuilder(name);
        for (var i : queue) {
            sb.append("\n");
            sb.append(i.getDisplayName());
        }
        return sb.toString();
    }

    @Override
    public Mono<Member> pollAndGetNext(Snowflake guild, String name) {
        System.out.println("Next");
        return Mono.fromCallable(() -> {
            var queue = get(guild, name);
            queue.poll();
            return queue.peek();
        });
    }

    @Override
    public Mono<Void> push(Snowflake guild, String name, Member member) {
        System.out.println("Push");
        return Mono.fromRunnable(() -> get(guild, name).add(member));
    }

    @Override
    public Mono<Boolean> exist(Snowflake guild, String name) {
        return Mono.fromCallable(() -> queues.containsKey(new QueueIdentifier(guild, name)));
    }

    @Override
    public Mono<Message> write(Snowflake guild, String name) {
        return messages.get(new QueueIdentifier(guild, name)).edit().withContentOrNull(getStringOf(guild, name));
    }

    @Override
    public Mono<Void> flush(Snowflake guild, String name) {
        return Mono.fromRunnable(() -> queues.computeIfPresent(new QueueIdentifier(guild, name),
                (a, b) -> new ConcurrentLinkedQueue<>()));
    }


}
