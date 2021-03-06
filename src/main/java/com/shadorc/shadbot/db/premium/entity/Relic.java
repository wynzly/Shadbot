package com.shadorc.shadbot.db.premium.entity;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.shadorc.shadbot.db.DatabaseEntity;
import com.shadorc.shadbot.db.DatabaseManager;
import com.shadorc.shadbot.db.SerializableEntity;
import com.shadorc.shadbot.db.premium.RelicType;
import com.shadorc.shadbot.db.premium.bean.RelicBean;
import com.shadorc.shadbot.utils.TimeUtils;
import discord4j.core.object.util.Snowflake;
import reactor.util.annotation.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static com.shadorc.shadbot.db.premium.PremiumCollection.LOGGER;

public class Relic extends SerializableEntity<RelicBean> implements DatabaseEntity {

    public Relic(RelicBean bean) {
        super(bean);
    }

    public Relic(UUID id, RelicType type, Duration duration) {
        super(new RelicBean(id.toString(), type.toString(), duration.toMillis(), null, null, null));
    }

    public String getId() {
        return this.getBean().getId();
    }

    public RelicType getType() {
        return RelicType.valueOf(this.getBean().getType());
    }

    public Duration getDuration() {
        return Duration.ofMillis(this.getBean().getDuration());
    }

    public Optional<Instant> getActivation() {
        return Optional.ofNullable(this.getBean().getActivation())
                .map(Instant::ofEpochMilli);
    }

    public Optional<Snowflake> getUserId() {
        return Optional.ofNullable(this.getBean().getUserId())
                .map(Snowflake::of);
    }

    public Optional<Snowflake> getGuildId() {
        return Optional.ofNullable(this.getBean().getGuildId())
                .map(Snowflake::of);
    }

    public boolean isExpired() {
        return this.getActivation()
                .map(TimeUtils::getMillisUntil)
                .map(elapsedMillis -> elapsedMillis >= this.getDuration().toMillis())
                .orElse(false);
    }

    public void activate(Snowflake userId, @Nullable Snowflake guildId) {
        LOGGER.debug("[Relic {}] Activation", this.getId());

        DatabaseManager.getPremium()
                .getCollection()
                .updateOne(Filters.eq("_id", this.getId()),
                        Updates.combine(
                                Updates.set("user_id", userId.asString()),
                                Updates.set("guild_id", guildId == null ? null : guildId.asString()),
                                Updates.set("activation", Instant.now().toEpochMilli())));
    }

    @Override
    public void insert() {
        LOGGER.debug("[Relic {}] Insertion", this.getId());

        DatabaseManager.getPremium()
                .getCollection()
                .insertOne(this.toDocument());
    }

    @Override
    public void delete() {
        LOGGER.debug("[Relic {}] Deletion", this.getId());

        DatabaseManager.getPremium()
                .getCollection()
                .deleteOne(Filters.eq("_id", this.getId()));
    }

    @Override
    public String toString() {
        return "Relic{" +
                "bean=" + this.getBean() +
                '}';
    }
}
