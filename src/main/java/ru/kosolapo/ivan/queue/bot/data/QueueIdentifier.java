package ru.kosolapo.ivan.queue.bot.data;

import discord4j.common.util.Snowflake;

public record QueueIdentifier(Snowflake guild, String name) {
}
