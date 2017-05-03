package com.paralainer.timezonebot;

import java.util.TimeZone;

/**
 * Created by stalov on 03/05/2017.
 */
class TimeZoneInfo {
    private String alias;
    private TimeZone timeZone;


    TimeZoneInfo(String alias, TimeZone timeZone) {
        this.alias = alias;
        this.timeZone = timeZone;
    }

    public String getAlias() {
        return alias;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeZoneInfo that = (TimeZoneInfo) o;

        return alias.equals(that.alias);
    }

    @Override
    public int hashCode() {
        return alias.hashCode();
    }
}
