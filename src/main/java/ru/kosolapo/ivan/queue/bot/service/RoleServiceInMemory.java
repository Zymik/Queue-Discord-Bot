package ru.kosolapo.ivan.queue.bot.service;


import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class RoleServiceInMemory implements RoleService{
    private final ConcurrentMap<Snowflake, Snowflake> roles = new ConcurrentHashMap<>();

    @Override
    public Mono<Role> setQueueRole(Mono<Guild> guild, String role) {
        return guild.flatMap(g ->
                g.getRoles().filter(role1 -> role1.getName().equals(role))
                        .next()
                        .doOnNext(role1 -> roles.put(g.getId(), role1.getId())));
    }

    @Override
    public Mono<Boolean> isQueueRole(Snowflake serverId, Role role) {
        return null;
    }

    @Override
    public Mono<Boolean> hasQueueRole(Snowflake serverId, Member member) {
        var role = roles.get(serverId);
        if (role == null) {
            return Mono.just(false);
        }
        return Mono.just(member.getRoleIds().contains(role));
    }
}
