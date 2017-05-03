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

        if (result == null){
            return "";
        }

        int temp =  (int) result.getAsJsonObject("main").get("temp").getAsDouble();

        return "\u2600 " + temp + "℃";
    }

    private JsonObject callWs(String cityName) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&units=metric&APPID=" + apiKey);

        HttpResponse response = client.execute(httpGet);
        JsonParser parser = new JsonParser();
        return parser.parse(new InputStreamReader(response.getEntity().getContent())).getAsJsonObject();
    }
}
