package ru.kosolapo.ivan.queue.bot.data;

import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.List;

public record Request(MessageCreateEvent event, String command, List<String> args) {

    public int size() {
        return args.size();
    }

    public String get(int i) {
        return args.get(i);
    }

    public RequestMetaData getMetaData() {
        return new RequestMetaData(command, size());
    }

    public RequestMetaData unknownArgumentCountMetaData() {
        return new RequestMetaData(command, -1);
    }
}
