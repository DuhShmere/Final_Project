package com.healthos.frontend.controller;

import com.healthos.backend.database.MongoDBHelper;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MealController {

    private static final String MEALDB_URL = "https://www.themealdb.com/api/json/v1/1/random.php";
    private static final String CALORIE_URL = "https://api.calorieninjas.com/v1/nutrition?query=";
    private static final String CALORIE_API_KEY;

    static {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMissing()
                .load();
        CALORIE_API_KEY = dotenv.get("CALORIE_API_KEY", "");
    }

    public static class MealFetchResult {
        public final String name, category, area, ingredients;
        public final String calories, fat, protein, carbs;
        public final BufferedImage image;
        public final String imageUrl;

        public MealFetchResult(String name, String category, String area, String ingredients,
                               String calories, String fat, String protein, String carbs,
                               BufferedImage image, String imageUrl) {
            this.name = name;
            this.category = category;
            this.area = area;
            this.ingredients = ingredients;
            this.calories = calories;
            this.fat = fat;
            this.protein = protein;
            this.carbs = carbs;
            this.image = image;
            this.imageUrl = imageUrl;
        }
    }

    private final MongoDBHelper db;
    private final String username;

    public MealController(MongoDBHelper db, String username) {
        this.db = db;
        this.username = username;
    }

    public List<String> loadUserPreferences() {
        return db.loadUserPreferences(username);
    }

    public void saveLikedMeal(String name, String category, String calories,
                              String fat, String protein, String carbs,
                              String ingredients, String imageUrl) {
        db.saveLikedMeal(username, name, category, calories, fat, protein, carbs, ingredients, imageUrl);
    }

    public void saveDislikedMeal(String name) {
        db.saveDislikedMeal(username, name);
    }

    public MealFetchResult fetchMealData(List<String> userPreferences) {
        try {
            URL url = new URL(MEALDB_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            String response = new String(is.readAllBytes());
            conn.disconnect();

            JSONObject root = new JSONObject(response);
            JSONObject meal = root.getJSONArray("meals").getJSONObject(0);

            List<String> mealIngredients = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                String ingr = meal.optString("strIngredient" + i, "").trim();
                if (!ingr.isEmpty())
                    mealIngredients.add(ingr.toLowerCase());
            }

            if (userPreferences != null && !userPreferences.isEmpty()) {
                boolean hasMatch = false;
                outer:
                for (String mealIngr : mealIngredients) {
                    for (String pref : userPreferences) {
                        if (pref.toLowerCase().contains(mealIngr) || mealIngr.contains(pref.toLowerCase())) {
                            hasMatch = true;
                            break outer;
                        }
                    }
                }
                if (!hasMatch) return null;
            }

            String name = meal.optString("strMeal", "Unknown");
            String category = meal.optString("strCategory", "");
            String area = meal.optString("strArea", "");

            StringBuilder ingrBuilder = new StringBuilder();
            for (int i = 1; i <= 20; i++) {
                String ingr = meal.optString("strIngredient" + i, "").trim();
                String measure = meal.optString("strMeasure" + i, "").trim();
                if (!ingr.isEmpty()) {
                    if (ingrBuilder.length() > 0) ingrBuilder.append(", ");
                    ingrBuilder.append(measure).append(" ").append(ingr);
                }
            }
            String ingredients = ingrBuilder.toString();

            String fetchedImageUrl = meal.optString("strMealThumb", "");
            BufferedImage image = null;
            if (!fetchedImageUrl.isEmpty()) {
                image = ImageIO.read(new URL(fetchedImageUrl));
            }

            String calories, fat, protein, carbs;
            if (!CALORIE_API_KEY.isEmpty()) {
                String cleanName = name.replaceAll("[^a-zA-Z0-9 ]", " ").replaceAll("\\s+", " ").trim();
                String[] nutrition = fetchNutrition(cleanName);

                if (nutrition == null && cleanName.contains(" ")) {
                    String shortName = cleanName.split(" ")[0] + " " + cleanName.split(" ")[1];
                    nutrition = fetchNutrition(shortName);
                }
                if (nutrition == null && !mealIngredients.isEmpty()) {
                    nutrition = fetchNutrition(mealIngredients.get(0));
                }

                if (nutrition != null) {
                    calories = nutrition[0];
                    fat = nutrition[1];
                    protein = nutrition[2];
                    carbs = nutrition[3];
                } else {
                    calories = fat = protein = carbs = "N/A";
                }
            } else {
                calories = fat = protein = carbs = "N/A";
            }

            return new MealFetchResult(name, category, area, ingredients,
                    calories, fat, protein, carbs, image, fetchedImageUrl);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String[] fetchNutrition(String query) {
        try {
            String encodedQuery = query.replace(" ", "+");
            URL calUrl = new URL(CALORIE_URL + encodedQuery);
            HttpURLConnection calConn = (HttpURLConnection) calUrl.openConnection();
            calConn.setRequestMethod("GET");
            calConn.setRequestProperty("X-Api-Key", CALORIE_API_KEY);

            InputStream calIs = calConn.getInputStream();
            String calResponse = new String(calIs.readAllBytes());
            calConn.disconnect();

            JSONObject calRoot = new JSONObject(calResponse);
            JSONArray items = calRoot.getJSONArray("items");

            if (items.length() > 0) {
                JSONObject nutrition = items.getJSONObject(0);
                return new String[]{
                    String.format("%.0f", nutrition.optDouble("calories", 0)),
                    String.format("%.1f", nutrition.optDouble("fat_total_g", 0)),
                    String.format("%.1f", nutrition.optDouble("protein_g", 0)),
                    String.format("%.1f", nutrition.optDouble("carbohydrates_total_g", 0))
                };
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
