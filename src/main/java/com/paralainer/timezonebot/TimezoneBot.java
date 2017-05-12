package com.paralainer.timezonebot;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Location;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by stalov on 02/05/2017.
 */
public class TimezoneBot extends TelegramLongPollingBot {

    private String botToken;
    private String botUsername;
    private LocationService timezoneService;
    private WeatherService weatherService;
    private Cache<Long, String> locationRequests;

    public TimezoneBot(String botToken, String botUsername, LocationService timezoneService, WeatherService weatherService) {
        this.botToken = botToken;
        this.botUsername = botUsername;

        this.timezoneService = timezoneService;
        this.weatherService = weatherService;
        this.locationRequests = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message == null) {
            return;
        }
        if (message.hasLocation()) {
            String alias = locationRequests.getIfPresent(message.getChatId());
            if (alias != null) {
                updateLocation(message, alias);
                locationRequests.invalidate(message.getChatId());
            }
        }
        if (message.isCommand()) {
            if (message.getText().startsWith("/addtz")) {
                addTimezone(message);
            } else if (message.getText().startsWith("/rmtz")) {
                removeTimezone(message);
            } else if (message.getText().startsWith("/tztime")) {
                sendTime(message);
            }
        }
    }

    private void updateLocation(Message message, String alias) {
        Location location = message.getLocation();
        String formattedLocation = String.format("%.6f,%.6f", location.getLatitude(), location.getLongitude());
        timezoneService.updateLocation(message.getChatId(), alias, formattedLocation);
        sendText(message.getChatId(), "Location added");
    }

    private void sendTime(Message message) {
        Set<LocationInfo> locationsSet = this.timezoneService.getLocations(message.getChatId());
        if (locationsSet == null) {
            return;
        }

        List<LocationInfo> locations = locationsSet.stream().sorted(Comparator.comparing(LocationInfo::getAlias)).collect(Collectors.toList());

        StringBuilder builder = new StringBuilder();
        Date currentDate = new Date();
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        for (LocationInfo location : locations) {
            WeatherInfo weather = weatherService.getWeather(location.getCoordinates());
            if (weather == null) {
                continue;
            }
            df.setTimeZone(TimeZone.getTimeZone(weather.getTimezone()));
            builder.append(location.getAlias())
                    .append(": ")
                    .append(df.format(currentDate))
                    .append(weather.getWeather())
                    .append("\n");
        }

        sendText(message.getChatId(), builder.toString());
    }

    private void removeTimezone(Message message) {
        String[] parts = message.getText().trim().split(" ");
        if (parts.length < 2) {
            sendText(message.getChatId(), "Usage: /rmtz <alias>");
        }
        String alias = Arrays.stream(parts).skip(1).collect(Collectors.joining(" "));
        boolean found = timezoneService.removeLocation(message.getChatId(), alias);
        if (found) {
            sendText(message.getChatId(), "Deleted");
        } else {
            sendText(message.getChatId(), "No tz found with alias: " + alias);
        }
    }

    private void addTimezone(Message message) {
        String[] parts = message.getText().split(" +");

        if (parts.length < 2) {
            sendText(message.getChatId(), "Usage: /addtz <alias>");
            return;
        }

        String alias = Arrays.stream(parts).skip(1).collect(Collectors.joining(" "));

        timezoneService.addLocation(message.getChatId(), alias);
        sendText(message.getChatId(), "Please send me location for " + alias);
        locationRequests.put(message.getChatId(), alias);
    }

    private void sendText(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace(System.out);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

}
