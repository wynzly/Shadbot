package com.shadorc.shadbot.db.stats.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.shadorc.shadbot.db.Bean;

public class GameStatsBean implements Bean {

    @JsonProperty("earned_coins")
    private long earnedCoins;
    @JsonProperty("lost_coins")
    private long lostCoins;

    public long getEarnedCoins() {
        return this.earnedCoins;
    }

    public long getLostCoins() {
        return this.lostCoins;
    }

    @Override
    public String toString() {
        return "GameStatsBean{" +
                "earnedCoins=" + this.earnedCoins +
                ", lostCoins=" + this.lostCoins +
                '}';
    }
}
