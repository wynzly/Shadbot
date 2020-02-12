package com.shadorc.shadbot.db.stats.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shadorc.shadbot.db.Bean;

import java.util.Map;

public class DailyGameStatsBean implements StatsBean {

    @JsonProperty("_id")
    private String id;
    @JsonProperty("game_stats")
    private Map<String, GameStatsBean> gameStats;

    public String getId() {
        return this.id;
    }

    public Map<String, GameStatsBean> getGameStats() {
        return this.gameStats;
    }

    @Override
    public String toString() {
        return "DailyGameStatsBean{" +
                "id='" + this.id + '\'' +
                ", gameStats=" + this.gameStats +
                '}';
    }

}
