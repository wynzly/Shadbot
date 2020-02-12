package com.shadorc.shadbot.db.stats.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class DailyCommandStatsBean implements StatsBean {

    @JsonProperty("_id")
    private String id;
    @JsonProperty("command_stats")
    private Map<String, Integer> commandStats;

    public String getId() {
        return this.id;
    }

    public Map<String, Integer> getCommandStats() {
        return this.commandStats;
    }

    @Override
    public String toString() {
        return "DailyCommandStatsBean{" +
                "id='" + this.id + '\'' +
                ", commandStats=" + this.commandStats +
                '}';
    }
}
