package com.paralainer.timezonebot;

import com.github.fedy2.weather.YahooWeatherService;
import com.github.fedy2.weather.data.Channel;
import com.github.fedy2.weather.data.Condition;
import com.github.fedy2.weather.data.unit.DegreeUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by stalov on 10/05/2017.
 */
public class YahooApiWeatherService implements WeatherService {
    private static final Map<Integer, String> EMOJI_MAPPING = new HashMap<>();

    static {
        EMOJI_MAPPING.put(0, "\uD83C\uDF2A"); //tornado
        EMOJI_MAPPING.put(1, "\uD83C\uDF2A"); //tropical storm
        EMOJI_MAPPING.put(2, "⛈"); //hurricane
        EMOJI_MAPPING.put(3, "⛈"); //severe thunderstorms
        EMOJI_MAPPING.put(4, "\uD83C\uDF29"); //thunderstorms
        EMOJI_MAPPING.put(5, "❄️ \uD83C\uDF27"); //mixed rain and snow
        EMOJI_MAPPING.put(6, "❄️ \uD83C\uDF27"); //mixed rain and sleet
        EMOJI_MAPPING.put(7, "❄️ \uD83C\uDF27"); //mixed snow and sleet
        EMOJI_MAPPING.put(8, "\uD83C\uDF02"); //freezing drizzle
        EMOJI_MAPPING.put(9, "\uD83C\uDF02"); //drizzle
        EMOJI_MAPPING.put(10, "☔"); //freezing rain
        EMOJI_MAPPING.put(11, "\uD83C\uDF27"); //showers
        EMOJI_MAPPING.put(12, "\uD83C\uDF27"); //showers
        EMOJI_MAPPING.put(13, "❄️ \uD83D\uDCA8"); //snow flurries
        EMOJI_MAPPING.put(14, "❄ ️\uD83D\uDCA8"); //light snow showers
        EMOJI_MAPPING.put(15, "❄️ \uD83D\uDCA8"); //blowing snow
        EMOJI_MAPPING.put(16, "❄️ ☃"); //snow
        EMOJI_MAPPING.put(17, "\uD83C\uDF27"); //hail
        EMOJI_MAPPING.put(18, "\uD83C\uDF27 \uD83C\uDF28"); //sleet
        EMOJI_MAPPING.put(19, "\uD83C\uDF2C"); //dust
        EMOJI_MAPPING.put(20, "\uD83C\uDF2B"); //foggy
        EMOJI_MAPPING.put(21, "\uD83C\uDF2B"); //haze
        EMOJI_MAPPING.put(22, "\uD83C\uDF2B"); //smoky
        EMOJI_MAPPING.put(23, "\uD83D\uDCA8"); //blustery
        EMOJI_MAPPING.put(24, "\uD83D\uDCA8"); //windy
        EMOJI_MAPPING.put(25, "☁️"); //cold
        EMOJI_MAPPING.put(26, "☁️"); //cloudy
        EMOJI_MAPPING.put(27, "☁️"); //mostly cloudy (night)
        EMOJI_MAPPING.put(28, "☁️"); //mostly cloudy (day)
        EMOJI_MAPPING.put(29, "⛅"); //partly cloudy (night)
        EMOJI_MAPPING.put(30, "⛅"); //partly cloudy (day)
        EMOJI_MAPPING.put(31, "☀️"); //clear (night)
        EMOJI_MAPPING.put(32, "☀️"); //sunny
        EMOJI_MAPPING.put(33, "☀️"); //fair (night)
        EMOJI_MAPPING.put(34, "☀️"); //fair (day)
        EMOJI_MAPPING.put(35, "\uD83C\uDF27"); //mixed rain and hail
        EMOJI_MAPPING.put(36, "☀"); //hot
        EMOJI_MAPPING.put(37, "\uD83C\uDF29"); //isolated thunderstorms
        EMOJI_MAPPING.put(38, "\uD83C\uDF29"); //scattered thunderstorms
        EMOJI_MAPPING.put(39, "\uD83C\uDF29"); //scattered thunderstorms
        EMOJI_MAPPING.put(40, "⛈"); //scattered showers
        EMOJI_MAPPING.put(41, "❄️ ☃"); //heavy snow
        EMOJI_MAPPING.put(42, "❄️ \uD83C\uDF27"); //scattered snow showers
        EMOJI_MAPPING.put(43, "❄️ ☃"); //heavy snow
        EMOJI_MAPPING.put(44, "\uD83C\uDF24"); //partly cloudy
        EMOJI_MAPPING.put(45, "\uD83C\uDF29"); //thundershowers
        EMOJI_MAPPING.put(46, "❄️ \uD83C\uDF27"); //snow showers
        EMOJI_MAPPING.put(47, "⛈"); //isolated thundershowers
        EMOJI_MAPPING.put(3200, ""); //not available

    }

    @Override
    public String getWeather(String locationName) {
        try {
            YahooWeatherService service = new YahooWeatherService();
            List<Channel> weather = service.getForecastForLocation(locationName, DegreeUnit.CELSIUS).first(1);
            StringBuilder builder = new StringBuilder();
            for (Channel channel : weather) {
                Condition currentWeather = channel.getItem().getCondition();
                int code = currentWeather.getCode();
                int temp = currentWeather.getTemp();
                builder.append(" ")
                        .append(getEmoji(code))
                        .append(" ")
                        .append(temp)
                        .append("℃");
            }

            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getEmoji(int code) {
        String result = EMOJI_MAPPING.get(code);
        return result == null ? "" : result;
    }
}
