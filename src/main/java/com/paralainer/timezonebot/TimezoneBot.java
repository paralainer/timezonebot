package com.paralainer.timezonebot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
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
            String weather = weatherService.getWeather(timezone.getTimeZone().getID(), timezone.getAlias());
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

    }

    private void addTimezone(Message message) {
        String[] parts = message.getText().split(" ");

        if (parts.length < 3) {
            sendText(message.getChatId(), "Usage: /addtz <timezoneId> <alias>");
        }
        String timezoneID = parts[1].trim();
        String alias = Arrays.stream(parts).skip(2).collect(Collectors.joining(" "));
        TimeZone timeZone = TimeZone.getTimeZone(timezoneID);
        if (timeZone.getID().equals(timezoneID)) {
            timezoneService.addTimezone(message.getChatId(), new TimeZoneInfo(alias, timeZone));
            sendText(message.getChatId(), "Timezone " + timezoneID + " added");
        } else {
            sendText(message.getChatId(), "No such timezone found " + timeZone.getID());
        }
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
