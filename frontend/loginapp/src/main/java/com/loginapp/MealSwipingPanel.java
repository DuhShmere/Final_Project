package com.loginapp;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.json.JSONArray;
import org.json.JSONObject;
import io.github.cdimascio.dotenv.Dotenv;

public class MealSwipingPanel extends JPanel {

    private static final String MEALDB_URL = "https://www.themealdb.com/api/json/v1/1/random.php";
    private static final String CALORIE_URL = "https://api.calorieninjas.com/v1/nutrition?query=";
    private static final int MAX_ATTEMPTS = 20;
    private static final String CALORIE_API_KEY;

    static {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .ignoreIfMissing()
                .load();
        CALORIE_API_KEY = dotenv.get("CALORIE_API_KEY", "");
    }

    private boolean showingFront = true;
    private List<String> userPreferences = null;

    // Front card data
    private String mealName = "Loading...";
    private String mealCategory = "";
    private String mealArea = "";
    private BufferedImage mealImage = null;

    // Back card data
    private String calories = "--";
    private String fat = "--";
    private String protein = "--";
    private String carbs = "--";
    private String ingredients = "";

    // MongoDB and user
    private MongoDBHelper db;
    private String username;

    // UI Components
    private JPanel cardPanel = new JPanel();
    private JPanel frontPanel = new JPanel();
    private JPanel backPanel = new JPanel();
    private JLabel imageLabel = new JLabel("", JLabel.CENTER);
    private JLabel nameLabel = new JLabel("", JLabel.CENTER);
    private JLabel categoryLabel = new JLabel("", JLabel.CENTER);
    private JLabel calLabel = new JLabel();
    private JLabel fatLabel = new JLabel();
    private JLabel proteinLabel = new JLabel();
    private JLabel carbsLabel = new JLabel();
    private JTextArea ingredientsArea = new JTextArea();
    private JButton likeButton = new JButton("👍 Like");
    private JButton dislikeButton = new JButton("👎 Dislike");
    private JButton nextButton = new JButton("Next ➡");
    private JButton backButton = new JButton("⬅ Prev");
    private JLabel flipHint = new JLabel("Click card to flip", JLabel.CENTER);
    private JLabel statusLabel = new JLabel("", JLabel.CENTER);

    public MealSwipingPanel(MongoDBHelper db, String username) {
        this.db = db;
        this.username = username;

        // Load user preferences from MongoDB
        userPreferences = db.loadUserPreferences(username);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Title
        JLabel title = new JLabel("Meal Swiping", JLabel.CENTER);
        title.setFont(new Font(null, Font.BOLD, 25));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        add(title, BorderLayout.NORTH);

        // Card panel
        cardPanel.setLayout(new CardLayout());
        cardPanel.setPreferredSize(new Dimension(340, 320));
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 180, 120), 2));
        cardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        setupFrontPanel();
        setupBackPanel();

        cardPanel.add(frontPanel, "front");
        cardPanel.add(backPanel, "back");

        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                flipCard();
            }
        });

        add(cardPanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        flipHint.setFont(new Font(null, Font.ITALIC, 11));
        flipHint.setForeground(Color.DARK_GRAY);
        statusLabel.setFont(new Font(null, Font.BOLD, 13));

        styleButton(likeButton, new Color(100, 200, 100));
        styleButton(dislikeButton, new Color(200, 100, 100));
        styleButton(nextButton, new Color(180, 180, 180));
        styleButton(backButton, new Color(180, 180, 180));

        // Like - saves to MongoDB but stays on current card
        likeButton.addActionListener(e -> {
            db.saveLikedMeal(username, mealName, mealCategory, calories);
            statusLabel.setForeground(new Color(0, 150, 0));
            statusLabel.setText("Liked: " + mealName + " saved!");
        });

        // Dislike - saves to MongoDB but stays on current card
        dislikeButton.addActionListener(e -> {
            db.saveDislikedMeal(username, mealName);
            statusLabel.setForeground(new Color(180, 0, 0));
            statusLabel.setText("Disliked: " + mealName);
        });

        // Next - loads a new meal
        nextButton.addActionListener(e -> {
            statusLabel.setText("");
            loadNextMeal();
        });

        // Prev - placeholder
        backButton.addActionListener(e -> {
            statusLabel.setText("");
            System.out.println("Back clicked");
        });

        buttonRow.add(dislikeButton);
        buttonRow.add(backButton);
        buttonRow.add(nextButton);
        buttonRow.add(likeButton);

        bottomPanel.add(flipHint, BorderLayout.NORTH);
        bottomPanel.add(buttonRow, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        loadNextMeal();
    }

    private void setupFrontPanel() {
        frontPanel.setLayout(new BorderLayout(5, 5));
        frontPanel.setBackground(new Color(255, 248, 220));

        imageLabel.setPreferredSize(new Dimension(340, 220));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);

        nameLabel.setFont(new Font(null, Font.BOLD, 16));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);

        categoryLabel.setFont(new Font(null, Font.ITALIC, 12));
        categoryLabel.setForeground(Color.GRAY);
        categoryLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(255, 248, 220));
        infoPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        infoPanel.add(nameLabel);
        infoPanel.add(categoryLabel);

        frontPanel.add(imageLabel, BorderLayout.CENTER);
        frontPanel.add(infoPanel, BorderLayout.SOUTH);
    }

    private void setupBackPanel() {
        backPanel.setLayout(new BorderLayout(5, 5));
        backPanel.setBackground(new Color(220, 240, 255));
        backPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel statsTitle = new JLabel("Nutrition Facts", JLabel.CENTER);
        statsTitle.setFont(new Font(null, Font.BOLD, 16));
        backPanel.add(statsTitle, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        statsPanel.setBackground(new Color(220, 240, 255));

        calLabel = makeStatLabel("Calories", "--", " kcal");
        fatLabel = makeStatLabel("Fat", "--", "g");
        proteinLabel = makeStatLabel("Protein", "--", "g");
        carbsLabel = makeStatLabel("Carbs", "--", "g");

        statsPanel.add(calLabel);
        statsPanel.add(fatLabel);
        statsPanel.add(proteinLabel);
        statsPanel.add(carbsLabel);

        JLabel ingrTitle = new JLabel("Ingredients:");
        ingrTitle.setFont(new Font(null, Font.BOLD, 12));
        statsPanel.add(ingrTitle);

        ingredientsArea.setEditable(false);
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        ingredientsArea.setBackground(new Color(220, 240, 255));
        ingredientsArea.setFont(new Font(null, Font.PLAIN, 11));
        ingredientsArea.setRows(3);
        statsPanel.add(ingredientsArea);

        backPanel.add(statsPanel, BorderLayout.CENTER);
    }

    private JLabel makeStatLabel(String name, String value, String unit) {
        JLabel label = new JLabel(name + ": " + value + unit);
        label.setFont(new Font(null, Font.PLAIN, 13));
        return label;
    }

    private void updateStatLabel(JLabel label, String name, String value, String unit) {
        label.setText(name + ": " + value + unit);
    }

    private void flipCard() {
        showingFront = !showingFront;
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, showingFront ? "front" : "back");
    }

    private void loadNextMeal() {
        showingFront = true;
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, "front");

        imageLabel.setIcon(null);
        imageLabel.setText("Loading...");
        nameLabel.setText("Finding a meal for you...");
        categoryLabel.setText("");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private boolean found = false;

            @Override
            protected Void doInBackground() throws Exception {
                for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
                    boolean matches = fetchMealData();
                    if (matches) {
                        found = true;
                        break;
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                if (found) {
                    updateFrontUI();
                    updateBackUI();
                } else {
                    showNoMealsFound();
                }
            }
        };
        worker.execute();
    }

    private boolean fetchMealData() {
        try {
            // Step 1: Fetch meal from TheMealDB
            URL url = new URL(MEALDB_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            String response = new String(is.readAllBytes());
            conn.disconnect();

            JSONObject root = new JSONObject(response);
            JSONObject meal = root.getJSONArray("meals").getJSONObject(0);

            // Extract meal ingredients
            List<String> mealIngredients = new java.util.ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                String ingr = meal.optString("strIngredient" + i, "").trim();
                if (!ingr.isEmpty())
                    mealIngredients.add(ingr.toLowerCase());
            }

            // Check if meal matches user preferences
            if (userPreferences != null && !userPreferences.isEmpty()) {
                boolean hasMatch = false;
                for (String mealIngr : mealIngredients) {
                    for (String pref : userPreferences) {
                        if (pref.toLowerCase().contains(mealIngr) ||
                                mealIngr.contains(pref.toLowerCase())) {
                            hasMatch = true;
                            break;
                        }
                    }
                    if (hasMatch)
                        break;
                }
                if (!hasMatch)
                    return false;
            }

            // Store meal data
            mealName = meal.optString("strMeal", "Unknown");
            mealCategory = meal.optString("strCategory", "");
            mealArea = meal.optString("strArea", "");

            StringBuilder ingrBuilder = new StringBuilder();
            for (int i = 1; i <= 20; i++) {
                String ingr = meal.optString("strIngredient" + i, "").trim();
                String measure = meal.optString("strMeasure" + i, "").trim();
                if (!ingr.isEmpty()) {
                    if (ingrBuilder.length() > 0)
                        ingrBuilder.append(", ");
                    ingrBuilder.append(measure).append(" ").append(ingr);
                }
            }
            ingredients = ingrBuilder.toString();

            String imageUrl = meal.optString("strMealThumb", "");
            if (!imageUrl.isEmpty()) {
                mealImage = ImageIO.read(new URL(imageUrl));
            }

            // Step 2: Fetch nutrition from CalorieNinjas
            if (!CALORIE_API_KEY.isEmpty()) {
                // Clean meal name
                String cleanName = mealName
                        .replaceAll("[^a-zA-Z0-9 ]", " ")
                        .replaceAll("\\s+", " ")
                        .trim();

                // Try full meal name first
                boolean nutritionFound = fetchNutrition(cleanName);

                // Try first two words if full name fails
                if (!nutritionFound && cleanName.contains(" ")) {
                    String shortName = cleanName.split(" ")[0] + " "
                            + cleanName.split(" ")[1];
                    nutritionFound = fetchNutrition(shortName);
                }

                // Try main ingredient if name attempts fail
                if (!nutritionFound && !mealIngredients.isEmpty()) {
                    nutritionFound = fetchNutrition(mealIngredients.get(0));
                }

                if (!nutritionFound) {
                    calories = "N/A";
                    fat = "N/A";
                    protein = "N/A";
                    carbs = "N/A";
                }
            } else {
                calories = "N/A";
                fat = "N/A";
                protein = "N/A";
                carbs = "N/A";
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean fetchNutrition(String query) {
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
                calories = String.format("%.0f", nutrition.optDouble("calories", 0));
                fat = String.format("%.1f", nutrition.optDouble("fat_total_g", 0));
                protein = String.format("%.1f", nutrition.optDouble("protein_g", 0));
                carbs = String.format("%.1f", nutrition.optDouble("carbohydrates_total_g", 0));
                return true;
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }

    private void showNoMealsFound() {
        imageLabel.setIcon(null);
        imageLabel.setText("");
        nameLabel.setFont(new Font(null, Font.BOLD, 18));
        nameLabel.setText("No more available recipes");
        categoryLabel.setText("Try updating your ingredient preferences in Settings");
        cardPanel.setBackground(new Color(240, 240, 240));
        likeButton.setEnabled(false);
        dislikeButton.setEnabled(false);
    }

    private void updateFrontUI() {
        likeButton.setEnabled(true);
        dislikeButton.setEnabled(true);
        nameLabel.setFont(new Font(null, Font.BOLD, 16));
        nameLabel.setText(mealName);
        categoryLabel.setText(mealCategory + (mealArea.isEmpty() ? "" : " · " + mealArea));

        if (mealImage != null) {
            Image scaled = mealImage.getScaledInstance(320, 210, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
            imageLabel.setText("");
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText("No image available");
        }
    }

    private void updateBackUI() {
        updateStatLabel(calLabel, "Calories", calories, " kcal");
        updateStatLabel(fatLabel, "Fat", fat, "g");
        updateStatLabel(proteinLabel, "Protein", protein, "g");
        updateStatLabel(carbsLabel, "Carbs", carbs, "g");
        ingredientsArea.setText(ingredients);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font(null, Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }
}