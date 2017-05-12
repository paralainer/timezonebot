package com.paralainer.timezonebot;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by stalov on 10/05/2017.
 */
public class CachedWeatherService implements WeatherService {
    private LoadingCache<String, WeatherInfo> cache;

    public CachedWeatherService(WeatherService service) {
        cache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build(new CacheLoader<String, WeatherInfo>() {
            @Override
            public WeatherInfo load(String s) throws Exception {
                return service.getWeather(s);
            }
        });
    }

    @Override
    public WeatherInfo getWeather(String locationName) {
        try {
            return cache.get(locationName);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
