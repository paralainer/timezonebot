package com.paralainer.timezonebot;

/**
 * Created by stalov on 03/05/2017.
 */
class LocationInfo {
    private String alias;
    private String coordinates;


    LocationInfo(String alias, String coordinates) {
        this.alias = alias;
        this.coordinates = coordinates;
    }

    public String getAlias() {
        return alias;
    }

    public String getCoordinates() {
        return coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationInfo that = (LocationInfo) o;

        return alias.equals(that.alias);
    }
}
