package com.shadorc.shadbot.db.stats.entity;

import com.shadorc.shadbot.db.stats.bean.DailyGameStatsBean;

import java.util.Map;
import java.util.stream.Collectors;

public class DailyGameStats extends StatsEntity<DailyGameStatsBean> {

    public DailyGameStats(DailyGameStatsBean bean) {
        super(bean);
    }

    /**
     * @return A {@link Map} with command names as keys and game stats during 1 day as values.
     */
    public Map<String, GameStats> getGameStats() {
        return this.getBean().getGameStats().entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> new GameStats(entry.getValue())));
    }

    @Override
    public String toString() {
        return "DailyGameStats{" +
                "bean=" + this.getBean() +
                '}';
    }

}
