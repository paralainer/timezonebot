package com.paralainer.timezonebot;

/**
 * Created by stalov on 12/05/2017.
 */
public interface WeatherService {
    WeatherInfo getWeather(String coordinates);
}
