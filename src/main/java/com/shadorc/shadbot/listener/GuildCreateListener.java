package com.shadorc.shadbot.listener;

import com.shadorc.shadbot.Shadbot;
import com.shadorc.shadbot.utils.LogUtils;
import discord4j.core.event.domain.guild.GuildCreateEvent;
import reactor.core.publisher.Mono;

public class GuildCreateListener implements EventListener<GuildCreateEvent> {

    @Override
    public Class<GuildCreateEvent> getEventType() {
        return GuildCreateEvent.class;
    }

    @Override
    public Mono<Void> execute(GuildCreateEvent event) {
        return Mono.fromRunnable(() -> {
            final int shardIndex = event.getClient().getConfig().getShardIndex();
            if (Shadbot.getShards().get(shardIndex).isFullyReady()) {
                final long guildId = event.getGuild().getId().asLong();
                final int memberCount = event.getGuild().getMemberCount().orElse(-1);
                LogUtils.info("{Guild ID: %d} Connected (%d users).", guildId, memberCount);
            }
        });
    }

}
