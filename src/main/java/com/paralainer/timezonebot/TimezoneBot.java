package com.paralainer.timezonebot;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by stalov on 02/05/2017.
 */
public class TimezoneBot extends TelegramLongPollingBot {

    private Map<Long, Set<TimeZone>> timezones = new HashMap<>();
    private String botToken;
    private String botUsername;

    public TimezoneBot(String botToken, String botUsername) {
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message == null || message.getText() == null){
            return;
        }
        if (message.isCommand()){
            if (message.getText().startsWith("/addtz")){
               addTimezone(message);
            } else if (message.getText().startsWith("/rmtz")) {
                removeTimezone(message);
            } else if (message.getText().equals("/tztime")) {
                sendTime(message);
            }
        }
    }

    private void sendTime(Message message) {
        Set<TimeZone> timezones = this.timezones.get(message.getChatId());
        if (timezones == null){
            return;
        }

        StringBuilder builder = new StringBuilder();
        Date currentDate = new Date();
        DateFormat df = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        for (TimeZone timezone : timezones) {
            df.setTimeZone(timezone);
            builder.append(timezone.getDisplayName()).append(": ").append(df.format(currentDate)).append("\n");
        }

        sendText(message.getChatId(), builder.toString());
    }

    private void removeTimezone(Message message) {

    }

    private void addTimezone(Message message) {
        String timezoneID = message.getText().split(" ")[1].trim();
        TimeZone timeZone = TimeZone.getTimeZone(timezoneID);
        if (timeZone.getID().equals(timezoneID)){
            Set<TimeZone> chatTimezones = timezones.computeIfAbsent(message.getChatId(), k -> new HashSet<>());
            chatTimezones.add(timeZone);
            sendText(message.getChatId(), "Timezone " + timezoneID + " added");
        } else {
            sendText(message.getChatId(), "No such timezone found " + timeZone.getID());
        }
    }

    private void sendText(Long chatId, String text){
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
