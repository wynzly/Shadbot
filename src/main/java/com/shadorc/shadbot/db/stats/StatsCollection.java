package com.shadorc.shadbot.db.stats;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.shadorc.shadbot.core.command.BaseCmd;
import com.shadorc.shadbot.db.DatabaseCollection;
import com.shadorc.shadbot.db.stats.bean.DailyCommandStatsBean;
import com.shadorc.shadbot.db.stats.bean.DailyGameStatsBean;
import com.shadorc.shadbot.db.stats.entity.DailyCommandStats;
import com.shadorc.shadbot.db.stats.entity.DailyGameStats;
import com.shadorc.shadbot.utils.Utils;
import org.bson.Document;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class StatsCollection extends DatabaseCollection {

    public static final Logger LOGGER = Loggers.getLogger("shadbot.database.stats");

    public StatsCollection(MongoDatabase database) {
        super(database.getCollection("stats"));
    }

    public Mono<UpdateResult> logCommand(BaseCmd cmd) {
        LOGGER.debug("[Command log] Logging {}.", cmd.getName());

        return Mono.from(this.getCollection()
                .updateOne(Filters.eq("_id", this.today()),
                        Updates.inc(String.format("command_stats.%s", cmd.getName()), 1),
                        new UpdateOptions().upsert(true)));
    }

    public Mono<UpdateResult> logEarnedCoins(BaseCmd cmd, long earnedCoins) {
        LOGGER.debug("[Earned coins log] Logging {}: {} coins.", cmd.getName(), earnedCoins);

        return Mono.from(this.getCollection()
                .updateOne(Filters.eq("_id", this.today()),
                        Updates.inc(String.format("game_stats.%s.earned_coins", cmd.getName()), earnedCoins),
                        new UpdateOptions().upsert(true)));
    }

    public Mono<UpdateResult> logLostCoins(BaseCmd cmd, long lostCoins) {
        LOGGER.debug("[Lost coins log] Logging {}: {} coins.", cmd.getName(), lostCoins);

        return Mono.from(this.getCollection()
                .updateOne(Filters.eq("_id", this.today()),
                        Updates.inc(String.format("game_stats.%s.lost_coins", cmd.getName()), lostCoins),
                        new UpdateOptions().upsert(true)));
    }

    public Flux<DailyCommandStats> getCommandStats() {
        LOGGER.debug("[Command stats] Request.");

        // TODO
        final Publisher<Document> request = this.getCollection().find();

        return Flux.from(request)
                .map(document -> document.toJson(Utils.JSON_WRITER_SETTINGS))
                .flatMap(json -> Mono.fromCallable(() -> Utils.MAPPER.readValue(json, DailyCommandStatsBean.class)))
                .map(DailyCommandStats::new);
    }

    public Flux<DailyGameStats> getGameStats() {
        LOGGER.debug("[Game stats] Request.");

        // TODO
        final Publisher<Document> request = this.getCollection().find();

        return Flux.from(request)
                .map(document -> document.toJson(Utils.JSON_WRITER_SETTINGS))
                .flatMap(json -> Mono.fromCallable(() -> Utils.MAPPER.readValue(json, DailyGameStatsBean.class)))
                .map(DailyGameStats::new);
    }

    private long today() {
        return TimeUnit.MILLISECONDS.toDays(Instant.now().toEpochMilli());
    }
}
