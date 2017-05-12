package com.paralainer.timezonebot;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.eq;


/**
 * Created by stalov on 03/05/2017.
 */
public class LocationService {

    private static final String CHAT_ID = "chatId";
    private static final String LOCATIONS = "locations";
    private static final String LOCATION_ALIAS = "alias";
    private static final String LOCATION_COORDINATES = "coordinates";

    private MongoCollection<Document> tzCollection;

    public LocationService(MongoCollection<Document> tzCollection) {
        this.tzCollection = tzCollection;
    }

    public Set<LocationInfo> getLocations(Long chatId) {
        Document chatTz = tzCollection.find(eq(CHAT_ID, chatId)).first();
        List<Document> timezones = (List<Document>) chatTz.get(LOCATIONS);

        Set<LocationInfo> result = new HashSet<>();

        for (Document timezone : timezones) {
            result.add(new LocationInfo(timezone.getString(LOCATION_ALIAS), timezone.getString(LOCATION_COORDINATES)));
        }

        return result;
    }

    public void addLocation(Long chatId, String alias) {
        addLocation(chatId, alias, null);
    }

    private void addLocation(Long chatId, String alias, String coordinates) {
        Document chatTz = tzCollection.find(eq(CHAT_ID, chatId)).first();
        if (chatTz == null) {
            tzCollection.insertOne(new Document(CHAT_ID, chatId).append(LOCATIONS, new HashSet<Document>()));
        }

        Document document = new Document(LOCATION_ALIAS, alias);
        if (coordinates != null) {
            document.append(LOCATION_COORDINATES, coordinates);
        }
        tzCollection.updateOne(eq(CHAT_ID, chatId),
                Updates.addToSet(LOCATIONS,
                        document
                )
        );

    }

    public boolean removeLocation(Long chatId, String alias) {
        UpdateResult updateResult = tzCollection.updateOne(eq(CHAT_ID, chatId), Updates.pull(LOCATIONS, new Document(LOCATION_ALIAS, alias)));
        return updateResult.getMatchedCount() > 0 && updateResult.getModifiedCount() > 0;
    }


    public void updateLocation(Long chatId, String alias, String location) {
        removeLocation(chatId, alias);
        addLocation(chatId, alias, location);
    }
}
