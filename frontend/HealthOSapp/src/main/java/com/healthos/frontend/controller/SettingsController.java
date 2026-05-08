package com.healthos.frontend.controller;

import com.healthos.backend.database.MongoDBHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SettingsController {

    private static final String INGREDIENTS_URL = "https://www.themealdb.com/api/json/v1/1/list.php?i=list";

    private final MongoDBHelper db;
    private final String username;

    public SettingsController(MongoDBHelper db, String username) {
        this.db = db;
        this.username = username;
    }

    public List<String> loadUserPreferences() {
        return db.loadUserPreferences(username);
    }

    public void savePreferences(List<String> checked, List<String> unchecked) {
        db.saveUserPreferences(username, checked);
        db.saveDeselectedIngredients(username, unchecked);
    }

    public List<String> fetchIngredientsFromAPI() {
        List<String> ingredients = new ArrayList<>();
        try {
            URL url = new URL(INGREDIENTS_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            String response = new String(is.readAllBytes());
            conn.disconnect();

            JSONObject root = new JSONObject(response);
            JSONArray meals = root.getJSONArray("meals");
            for (int i = 0; i < meals.length(); i++) {
                String name = meals.getJSONObject(i).optString("strIngredient", "").trim();
                if (!name.isEmpty())
                    ingredients.add(name);
            }
            ingredients.sort(String::compareToIgnoreCase);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ingredients;
    }
}
