package com.shadorc.shadbot.db.stats.entity;

import com.shadorc.shadbot.db.Bean;
import com.shadorc.shadbot.db.SerializableEntity;
import com.shadorc.shadbot.db.stats.bean.StatsBean;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

public abstract class StatsEntity<T extends StatsBean> extends SerializableEntity<T> {

    public StatsEntity(T bean) {
        super(bean);
    }

    public String getId() {
        return this.getBean().getId();
    }

    /**
     * @return A {@link LocalDate} that corresponds to the day on which these statistics were collected.
     */
    public LocalDate getDate() {
        return LocalDate.ofInstant(
                Instant.ofEpochMilli(
                        TimeUnit.DAYS.toMillis(
                                Integer.parseInt(this.getBean().getId()))),
                ZoneId.systemDefault());
    }
}
