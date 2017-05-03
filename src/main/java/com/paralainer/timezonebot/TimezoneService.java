package com.paralainer.timezonebot;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import static com.mongodb.client.model.Filters.eq;


/**
 * Created by stalov on 03/05/2017.
 */
public class TimezoneService {

    MongoCollection<Document> tzCollection;

    public TimezoneService(MongoCollection<Document> tzCollection) {
        this.tzCollection = tzCollection;
    }

    public Set<TimeZoneInfo> getTimezones(Long chatId) {
        Document chatTz = tzCollection.find(eq("chatId", chatId)).first();
        List<Document> timezones = (List<Document>) chatTz.get("timezones");

        Set<TimeZoneInfo> result = new HashSet<>();

        for (Document timezone : timezones) {
            result.add(new TimeZoneInfo(timezone.getString("alias"), TimeZone.getTimeZone(timezone.getString("timezoneId"))));
        }

        return result;
    }

    public void addTimezone(Long chatId, TimeZoneInfo timeZoneInfo) {
        Document chatTz = tzCollection.find(eq("chatId", chatId)).first();
        if (chatTz == null) {
            tzCollection.insertOne(new Document("chatId", chatId).append("timezones", new HashSet<Document>()));
        }

        tzCollection.updateOne(eq("chatId", chatId),
                Updates.addToSet("timezones",
                        new Document("alias", timeZoneInfo.getAlias())
                                .append("timezoneId", timeZoneInfo.getTimeZone().getID())
                )
        );

    }

    public boolean removeTimezone(Long chatId, String timezoneIdOrAlias) {
        return true;
    }


}
