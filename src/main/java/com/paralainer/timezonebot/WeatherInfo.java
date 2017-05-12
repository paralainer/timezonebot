package com.paralainer.timezonebot;

/**
 * Created by stalov on 12/05/2017.
 */
public class WeatherInfo {
    private String weather;
    private String timezone;


    public WeatherInfo(String weather, String timezone) {
        this.weather = weather;
        this.timezone = timezone;
    }

    public String getWeather() {
        return weather;
    }

    public String getTimezone() {
        return timezone;
    }
}
