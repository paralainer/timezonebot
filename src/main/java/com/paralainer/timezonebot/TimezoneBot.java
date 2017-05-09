package com.paralainer.timezonebot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by stalov on 02/05/2017.
 */
public class TimezoneBot extends TelegramLongPollingBot {

    private String botToken;
    private String botUsername;
    private TimezoneService timezoneService;
    private WeatherService weatherService;

    public TimezoneBot(String botToken, String botUsername, TimezoneService timezoneService, WeatherService weatherService) {
        this.botToken = botToken;
        this.botUsername = botUsername;

        this.timezoneService = timezoneService;
        this.weatherService = weatherService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message == null || message.getText() == null) {
            return;
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

    private void sendTime(Message message) {
        Set<TimeZoneInfo> timezones = this.timezoneService.getTimezones(message.getChatId());
        if (timezones == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        Date currentDate = new Date();
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        for (TimeZoneInfo timezone : timezones) {
            df.setTimeZone(timezone.getTimeZone());
            String weather = weatherService.getWeather(timezone.getWeatherId(), timezone.getTimeZone().getID(), timezone.getAlias());
            builder.append(timezone.getAlias())
                    .append(": ")
                    .append(df.format(currentDate))
                    .append(" ")
                    .append(weather)
                    .append("\n");
        }

        sendText(message.getChatId(), builder.toString());
    }

    private void removeTimezone(Message message) {
        String[] parts = message.getText().trim().split(" ");
        if (parts.length < 2){
            sendText(message.getChatId(), "Usage: /rmtz <alias>");
        }
        String alias = Arrays.stream(parts).skip(1).collect(Collectors.joining(" "));
        boolean found = timezoneService.removeTimezone(message.getChatId(), alias);
        if (found){
            sendText(message.getChatId(), "Deleted");
        } else {
            sendText(message.getChatId(), "No tz found with alias: " + alias);
        }
    }

    private void addTimezone(Message message) {
        String[] parts = message.getText().split(" +");

        if (parts.length < 4) {
            sendText(message.getChatId(), "Usage: /addtz <timezoneId> <weatherId> <alias>");
        }
        String timezoneID = parts[1].trim();
        String weatherId = parts[2].trim();
        String alias = Arrays.stream(parts).skip(3).collect(Collectors.joining(" "));

        List<String> foundTimezones = Arrays.stream(TimeZone.getAvailableIDs())
                .filter(id -> id.contains("/"))
                .filter(id -> !id.matches(".*[0-9]+"))
                .filter(id -> id.contains(timezoneID)).collect(Collectors.toList());

        if (foundTimezones.isEmpty()) {
            sendText(message.getChatId(), "No timezone found by query: " + timezoneID);
            return;
        }

        if (foundTimezones.size() != 1) {
            sendText(message.getChatId(),
                    "Found several timezones by this query, please specify request, found timezones: \n" +
                            String.join("\n", foundTimezones));
            return;
        }

        TimeZone timeZone = TimeZone.getTimeZone(foundTimezones.get(0));
        timezoneService.addTimezone(message.getChatId(), new TimeZoneInfo(alias, weatherId, timeZone));
        sendText(message.getChatId(), "Timezone " + timezoneID + " added");
    }

    private void sendText(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
