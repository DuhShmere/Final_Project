package com.loginapp;

import com.mongodb.client.*;
import org.bson.Document;
import java.util.HashMap;
import java.util.List;

public class MongoDBHelper {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "loginApp";
    private static final String COLLECTION_NAME = "users";

    private MongoClient mongoClient;
    private MongoCollection<Document> usersCollection;

    public MongoDBHelper() {
        mongoClient = MongoClients.create(CONNECTION_STRING);
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        usersCollection = database.getCollection(COLLECTION_NAME);
    }

    public HashMap<String, String> getloginInfo() {
        HashMap<String, String> users = new HashMap<>();
        for (Document doc : usersCollection.find()) {
            String username = doc.getString("username");
            String password = doc.getString("password");
            if (username != null && password != null) {
                users.put(username, password);
            }
        }
        return users;
    }

    public boolean saveUser(String username, String password) {
        Document existing = usersCollection
                .find(new Document("username", username))
                .first();
        if (existing != null)
            return false;

        usersCollection.insertOne(
                new Document("username", username)
                        .append("password", password));
        return true;
    }

    public void saveUserPreferences(String username, List<String> checkedIngredients) {
        usersCollection.updateOne(
                new Document("username", username),
                new Document("$set", new Document("ingredients", checkedIngredients)));
    }

    public List<String> loadUserPreferences(String username) {
        Document user = usersCollection.find(new Document("username", username)).first();
        if (user != null && user.containsKey("ingredients")) {
            return user.getList("ingredients", String.class);
        }
        return null;
    }

    public void close() {
        mongoClient.close();
    }
}