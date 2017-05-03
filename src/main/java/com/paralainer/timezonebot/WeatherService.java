package com.paralainer.timezonebot;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by stalov on 03/05/2017.
 */
public class WeatherService {

    private String apiKey;

    String thunderstorm = "🌩";
    String drizzle = "🌧";
    String rain = "🌧";
    String snowflake = "❄️";
    String snowman = "☃️";
    String atmosphere = "🌫";
    String clearSky = "☀️";
    String fewClouds = "🌤";
    String clouds = "☁️";
    String hot = "☀️";
    String defaultEmoji = "";

    public WeatherService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getWeather(String timezoneId, String alias) {
        String[] split = timezoneId.split("/");
        JsonObject result = null;
        try {
            if (split.length == 2) {
                result = callWs(split[1]);
            } else {
                result = callWs(alias);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null) {
            return "";
        }

        int temp = (int) result.getAsJsonObject("main").get("temp").getAsDouble();
        String weatherId = String.valueOf(result.getAsJsonArray("weather").get(0).getAsJsonObject().get("id").getAsInt());
        return getEmoji(weatherId) + " " + temp + "℃";
    }

    private JsonObject callWs(String cityName) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&APPID=" + apiKey);

        HttpResponse response = client.execute(httpGet);
        JsonParser parser = new JsonParser();
        return parser.parse(new InputStreamReader(response.getEntity().getContent())).getAsJsonObject();
    }

    private String getEmoji(String weatherID) {
        if (weatherID != null) {
            if (weatherID.charAt(0) == '2' || weatherID.equals("900") || weatherID.equals("901") || weatherID.equals("902") || weatherID.equals("905"))
                return thunderstorm;
            else if (weatherID.charAt(0) == '3')
                return drizzle;
            else if (weatherID.charAt(0) == '5')
                return rain;
            else if (weatherID.charAt(0) == '6' || weatherID.equals("903") || weatherID.equals("906"))
                return snowflake + ' ' + snowman;
            else if (weatherID.charAt(0) == '7')
                return atmosphere;
            else if (weatherID.equals("800"))
                return clearSky;
            else if (weatherID.equals("801"))
                return fewClouds;
            else if (weatherID.equals("802") || weatherID.equals("803"))
                return clouds;
            else if (weatherID.equals("904"))
                return hot;
            else
                return defaultEmoji;

        } else return defaultEmoji;
    }
}
