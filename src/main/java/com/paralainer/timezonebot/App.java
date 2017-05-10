package com.paralainer.timezonebot;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
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

        MongoClientURI connectionString = new MongoClientURI(
                System.getenv("MONGO_URL")
        );

        MongoClient mongoClient = new MongoClient(connectionString);
        MongoDatabase tzbot = mongoClient.getDatabase(System.getenv("MONGO_DB_NAME"));
        MongoCollection<Document> chatTz = tzbot.getCollection(System.getenv("MONGO_TZ_COLLECTION"));
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(
                    new TimezoneBot(
                            System.getenv("TELEGRAM_BOT_TOKEN"),
                            System.getenv("TELEGRAM_BOT_NAME"),
                            new TimezoneService(chatTz),
                            new YahooApiWeatherService()
                    )
            );

            System.out.println("Bot is running");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
