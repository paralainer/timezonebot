package com.paralainer.timezonebot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stalov on 12/05/2017.
 */
public class DarkSkyWeatherService implements WeatherService {

    private static final Map<String, String> EMOJI_MAPPING = new HashMap<>();

    static {
        EMOJI_MAPPING.put("clear-day", "");
        EMOJI_MAPPING.put("clear-night", "☀");
        EMOJI_MAPPING.put("rain", "\uD83C\uDF27");
        EMOJI_MAPPING.put("snow", "❄️ ☃");
        EMOJI_MAPPING.put("sleet", "\uD83C\uDF27 \uD83C\uDF28");
        EMOJI_MAPPING.put("wind", "\uD83D\uDCA8");
        EMOJI_MAPPING.put("fog", "\uD83C\uDF2B");
        EMOJI_MAPPING.put("cloudy", "☁️");
        EMOJI_MAPPING.put("partly-cloudy-day", "⛅");
        EMOJI_MAPPING.put("partly-cloudy-night", "⛅");
        EMOJI_MAPPING.put("hail", "\uD83C\uDF27");
        EMOJI_MAPPING.put("thunderstorm", "⛈");
        EMOJI_MAPPING.put("tornado", "\uD83C\uDF2A");

    }

    private String key;

    public DarkSkyWeatherService(String key) {
        this.key = key;
    }


    @Override
    public WeatherInfo getWeather(String coordinates) {
        try {
            URL url = new URL("https://api.darksky.net/forecast/" + key + "/" + coordinates);
            JsonObject result = getJson(url.openConnection().getInputStream());
            String timezone = result.get("timezone").getAsString();
            JsonObject currentWeather = result.getAsJsonObject("currently");

            String emoji = getEmoji(currentWeather.get("icon").getAsString());
            int temp = (int) fahrenheitToCelsius(currentWeather.get("temperature").getAsDouble());
            return new WeatherInfo(
                    " " + emoji + " " + temp + "℃",
                    timezone
            );
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    private double fahrenheitToCelsius(double t) {
        return (t - 32) / 1.8;
    }

    private String getEmoji(String icon) {
        String s = EMOJI_MAPPING.get(icon);

        if (s == null) {
            System.out.println("No emoji for " + icon);
            return "";
        }

        return s;
    }


    private JsonObject getJson(InputStream inputStream) {
        JsonParser parser = new JsonParser();
        return parser.parse(new InputStreamReader(inputStream)).getAsJsonObject();
    }


}
