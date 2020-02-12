package com.shadorc.shadbot.db.stats.entity;

import com.shadorc.shadbot.db.stats.bean.DailyCommandStatsBean;

import java.util.Collections;
import java.util.Map;

public class DailyCommandStats extends StatsEntity<DailyCommandStatsBean> {

    public DailyCommandStats(DailyCommandStatsBean bean) {
        super(bean);
    }

    /**
     * @return A {@link Map} with command names as keys and number of utilization during 1 day as values.
     */
    public Map<String, Integer> getCommandStats() {
        return Collections.unmodifiableMap(this.getBean().getCommandStats());
    }

    @Override
    public String toString() {
        return "DailyCommandStats{" +
                "bean=" + this.getBean() +
                '}';
    }
}
