package com.shadorc.shadbot.db.stats.entity;

import com.shadorc.shadbot.db.SerializableEntity;
import com.shadorc.shadbot.db.stats.bean.GameStatsBean;

public class GameStats extends SerializableEntity<GameStatsBean> {

    public GameStats(GameStatsBean bean) {
        super(bean);
    }

    public long getEarnedCoins() {
        return this.getBean().getEarnedCoins();
    }

    public long getLostCoins() {
        return this.getBean().getLostCoins();
    }

    @Override
    public String toString() {
        return "GameStats{" +
                "bean=" + this.getBean() +
                '}';
    }
}
