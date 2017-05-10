package com.paralainer.timezonebot;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by stalov on 10/05/2017.
 */
public class CachedWeatherService implements WeatherService {
    private LoadingCache<String, String> cache;

    public CachedWeatherService(WeatherService service) {
        cache = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
            @Override
            public String load(String s) throws Exception {
                return service.getWeather(s);
            }
        });

        Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(() ->
                                cache.asMap()
                                        .keySet()
                                        .parallelStream()
                                        .forEach((key) -> cache.refresh(key)),
                        0,
                        30, TimeUnit.MINUTES);
    }

    @Override
    public String getWeather(String locationName) {
        try {
            return cache.get(locationName);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "";
        }
    }
}
