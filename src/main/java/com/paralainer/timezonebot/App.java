package com.paralainer.timezonebot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

import java.util.TimeZone;

/**
 * Created by stalov on 02/05/2017.
 */
public class App {
    public static void main(String[] args) {
        TimeZone timeZone = TimeZone.getTimeZone("America/Vancouver");
        System.out.println(timeZone.getID());
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(
                    new TimezoneBot(
                            System.getenv("TELEGRAM_BOT_TOKEN"),
                            System.getenv("TELEGRAM_BOT_NAME")
                    )
            );

            System.out.println("Bot is running");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
