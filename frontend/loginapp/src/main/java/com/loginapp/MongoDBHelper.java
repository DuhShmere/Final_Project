package com.loginapp;

import com.mongodb.client.*;
import org.bson.Document;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
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

    // Returns only usernames — passwords are hashed so we
    // can no longer store them in a plain HashMap for comparison
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

    // Register a new user with a hashed password
    public boolean saveUser(String username, String password) {
        Document existing = usersCollection
                .find(new Document("username", username))
                .first();
        if (existing != null)
            return false;

        // Hash the password before saving
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        usersCollection.insertOne(
                new Document("username", username)
                        .append("password", hashedPassword));
        return true;
    }

    // Verify a plain text password against the stored hash
    public boolean verifyPassword(String username, String plainPassword) {
        Document user = usersCollection
                .find(new Document("username", username))
                .first();
        if (user == null)
            return false;

        String storedHash = user.getString("password");
        if (storedHash == null)
            return false;

        return BCrypt.checkpw(plainPassword, storedHash);
    }

    // Check if a username exists
    public boolean userExists(String username) {
        return usersCollection
                .find(new Document("username", username))
                .first() != null;
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
    public void saveUserGoals(String username, String goal, String activityLevel) {
        Document preferenceData = new Document("fitnessGoal", goal)
                                .append("activityLevel", activityLevel);

        Document updateOperation = new Document("$set", preferenceData);

        usersCollection.updateOne(
            new Document("username", username),
            updateOperation
        );
    }

    public HashMap<String, String> loadUserGoals(String username) {
        HashMap<String, String> goals = new HashMap<>();
        Document user = usersCollection.find(new Document("username", username)).first();

        if(user != null && user.containsKey("userGoals")) {
            Document goalDoc = (Document) user.get("userGoals");
            goals.put("goal", goalDoc.getString("fitnessGoal"));
            goals.put("activity", goalDoc.getString("activityLevel"));

        }
        return goals;
    }

    public void saveLikedMeal(String username, String mealName, String category, String calories) {
        Document meal = new Document("name", mealName)
                .append("category", category)
                .append("calories", calories);

        usersCollection.updateOne(
                new Document("username", username),
                new Document("$addToSet", new Document("likedMeals", meal)));

        usersCollection.updateOne(
                new Document("username", username),
                new Document("$pull", new Document("dislikedMeals", mealName)));
    }

    public void saveDislikedMeal(String username, String mealName) {
        usersCollection.updateOne(
                new Document("username", username),
                new Document("$addToSet", new Document("dislikedMeals", mealName)));

        usersCollection.updateOne(
                new Document("username", username),
                new Document("$pull", new Document("likedMeals", new Document("name", mealName))));
    }

    public List<Document> loadLikedMeals(String username) {
        Document user = usersCollection.find(new Document("username", username)).first();
        if (user != null && user.containsKey("likedMeals")) {
            return user.getList("likedMeals", Document.class);
        }
        return new ArrayList<>();
    }

    public void close() {
        mongoClient.close();
    }
}