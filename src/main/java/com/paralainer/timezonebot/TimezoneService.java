package com.paralainer.timezonebot;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


/**
 * Created by stalov on 03/05/2017.
 */
public class TimezoneService {

    private static final String CHAT_ID = "chatId";
    private static final String TIMEZONES = "timezones";
    private static final String TIMEZONE_ALIAS = "alias";
    private static final String TIMEZONE_ID = "timezoneId";
    private static final String WEATHER_ID = "weatherId";

    private MongoCollection<Document> tzCollection;

    public TimezoneService(MongoCollection<Document> tzCollection) {
        this.tzCollection = tzCollection;
    }

    public Set<TimeZoneInfo> getTimezones(Long chatId) {
        Document chatTz = tzCollection.find(eq(CHAT_ID, chatId)).first();
        List<Document> timezones = (List<Document>) chatTz.get(TIMEZONES);

        Set<TimeZoneInfo> result = new HashSet<>();

        for (Document timezone : timezones) {
            result.add(new TimeZoneInfo(timezone.getString(TIMEZONE_ALIAS), timezone.getString(WEATHER_ID), TimeZone.getTimeZone(timezone.getString(TIMEZONE_ID))));
        }

        return result;
    }

    public void addTimezone(Long chatId, TimeZoneInfo timeZoneInfo) {
        Document chatTz = tzCollection.find(eq(CHAT_ID, chatId)).first();
        if (chatTz == null) {
            tzCollection.insertOne(new Document(CHAT_ID, chatId).append(TIMEZONES, new HashSet<Document>()));
        }

        tzCollection.updateOne(eq(CHAT_ID, chatId),
                Updates.addToSet(TIMEZONES,
                        new Document(TIMEZONE_ALIAS, timeZoneInfo.getAlias())
                                .append(TIMEZONE_ID, timeZoneInfo.getTimeZone().getID())
                                .append(WEATHER_ID, timeZoneInfo.getWeatherId())
                )
        );

    }

    public boolean removeTimezone(Long chatId, String timezoneAlias) {
        UpdateResult updateResult = tzCollection.updateOne(eq(CHAT_ID, chatId), Updates.pull(TIMEZONES, new Document(TIMEZONE_ALIAS, timezoneAlias)));
        return updateResult.getMatchedCount() > 0 && updateResult.getModifiedCount() > 0;
    }


}
