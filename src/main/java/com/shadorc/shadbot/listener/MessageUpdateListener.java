package com.shadorc.shadbot.listener;

import com.shadorc.shadbot.utils.TimeUtils;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageUpdateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Snowflake;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

public class MessageUpdateListener implements EventListener<MessageUpdateEvent> {

    @Override
    public Class<MessageUpdateEvent> getEventType() {
        return MessageUpdateEvent.class;
    }

    @Override
    public Mono<Void> execute(MessageUpdateEvent event) {
        if (!event.isContentChanged() || event.getOld().isEmpty() || event.getGuildId().isEmpty()) {
            return Mono.empty();
        }

        // If the message has been sent more than 30 seconds ago, ignore it
        final Message oldMessage = event.getOld().get();
        if (TimeUtils.getMillisUntil(oldMessage.getTimestamp()) > TimeUnit.SECONDS.toMillis(30)) {
            return Mono.empty();
        }

        final Snowflake guildId = event.getGuildId().get();
        return Mono.zip(event.getMessage(), event.getMessage().flatMap(Message::getAuthorAsMember))
                .doOnNext(tuple -> event.getClient()
                        .getServiceMediator()
                        .getEventDispatcher()
                        .publish(new MessageCreateEvent(event.getClient(), tuple.getT1(), guildId.asLong(), tuple.getT2())))
                .then();
    }

}
