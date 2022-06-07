package ru.kosolapo.ivan.queue.bot.service;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import reactor.core.publisher.Mono;

public interface RoleService {

    Mono<Role> setQueueRole(Mono<Guild> guild, String role);

    Mono<Boolean> isQueueRole(Snowflake serverId, Role role);

    Mono<Boolean> hasQueueRole(Snowflake serverId, Member member);
}
