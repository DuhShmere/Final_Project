package com.healthos.frontend.view;
import com.healthos.backend.database.*;
import com.healthos.backend.model.*;
import com.healthos.backend.model.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

public class ViewRecipesPanel extends JPanel {

    private static final String CALORIE_URL = "https://api.calorieninjas.com/v1/nutrition?query=";
    private static final String CALORIE_API_KEY = "8sXoC5SqpJvZg8FsOVPuGg1NNyC9CuoeqXEajJ76";
    private static final String MEALDB_SEARCH = "https://www.themealdb.com/api/json/v1/1/search.php?s=";

    private MongoDBHelper db;
    private String username;
    private JPanel recipesListPanel;

    public ViewRecipesPanel(MongoDBHelper db, String username) {
        this.db = db;
        this.username = username;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("My Liked Recipes", JLabel.CENTER);
        title.setFont(new Font(null, Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        recipesListPanel = new JPanel();
        recipesListPanel.setLayout(new BoxLayout(recipesListPanel, BoxLayout.Y_AXIS));
        recipesListPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(recipesListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);

        loadRecipes();
    }

    private void loadRecipes() {
        recipesListPanel.removeAll();

        List<Document> likedMeals = db.loadLikedMeals(username);
        List<String> allowedIngredients = db.loadUserPreferences(username);

        if (likedMeals.isEmpty()) {
            JLabel emptyLabel = new JLabel("You have not liked any meals yet!", JLabel.CENTER);
            emptyLabel.setFont(new Font(null, Font.ITALIC, 16));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(new EmptyBorder(40, 0, 0, 0));
            recipesListPanel.add(emptyLabel);
        } else {
            JLabel filterHint = new JLabel(
                    "Click any recipe to view full details. Deselected ingredients from Settings will be hidden.",
                    JLabel.CENTER);
            filterHint.setFont(new Font(null, Font.ITALIC, 11));
            filterHint.setForeground(Color.GRAY);
            filterHint.setAlignmentX(Component.CENTER_ALIGNMENT);
            recipesListPanel.add(filterHint);
            recipesListPanel.add(Box.createVerticalStrut(6));

            recipesListPanel.add(makeHeaderRow());
            recipesListPanel.add(Box.createVerticalStrut(2));

            for (int i = 0; i < likedMeals.size(); i++) {
                Document meal = likedMeals.get(i);
                String name = getString(meal, "name", "Unknown");
                String category = getString(meal, "category", "Unknown");
                String calories = getString(meal, "calories", "N/A");
                String fat = getString(meal, "fat", null);
                String protein = getString(meal, "protein", null);
                String carbs = getString(meal, "carbs", null);
                String ingredients = getString(meal, "ingredients", null);
                String imageUrl = getString(meal, "imageUrl", null);

                recipesListPanel.add(makeMealRow(
                        i + 1, name, category, calories,
                        fat, protein, carbs, ingredients, imageUrl,
                        allowedIngredients));
                // thin separator instead of large strut
                recipesListPanel.add(makeThinDivider());
            }

            JLabel countLabel = new JLabel("Total liked recipes: " + likedMeals.size());
            countLabel.setFont(new Font(null, Font.ITALIC, 12));
            countLabel.setForeground(Color.GRAY);
            countLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            countLabel.setBorder(new EmptyBorder(6, 0, 0, 0));
            recipesListPanel.add(countLabel);
        }

        recipesListPanel.revalidate();
        recipesListPanel.repaint();
    }

    // ------------------------------------------------------------------
    // Detail dialog
    // ------------------------------------------------------------------
    private void showRecipeDetail(String name, String category, String calories,
            String fat, String protein, String carbs,
            String savedIngredients, String imageUrl,
            List<String> allowedIngredients) {
        JDialog dialog = new JDialog(
                (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this),
                name, true);
        dialog.setSize(460, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JLabel loadingLabel = new JLabel("Loading recipe details...", JLabel.CENTER);
        loadingLabel.setFont(new Font(null, Font.ITALIC, 14));
        loadingLabel.setForeground(Color.GRAY);
        dialog.add(loadingLabel, BorderLayout.CENTER);

        // FetchResult holds all data including image
        SwingWorker<FetchResult, Void> worker = new SwingWorker<FetchResult, Void>() {
            @Override
            protected FetchResult doInBackground() {
                // MealPlan recipe — full data in MealPlanData, no image URL
                MealPlanData.Recipe r = findInMealPlanData(name);
                if (r != null) {
                    return new FetchResult(r.fat, r.protein, r.carbs,
                            r.ingredients, r.instructions, null);
                }

                // MealSwiping recipe with saved ingredients — just fetch image
                if (savedIngredients != null && !savedIngredients.isEmpty()) {
                    BufferedImage img = loadImage(imageUrl);
                    return new FetchResult(
                            fat != null ? fat : "N/A",
                            protein != null ? protein : "N/A",
                            carbs != null ? carbs : "N/A",
                            savedIngredients, null, img);
                }

                // Old format — fetch everything from APIs
                return fetchFromMealDB(name, imageUrl);
            }

            @Override
            protected void done() {
                try {
                    FetchResult data = get();
                    dialog.getContentPane().removeAll();
                    buildDetailUI(dialog, name, category, calories,
                            data.fat, data.protein, data.carbs,
                            data.ingredients, data.instructions,
                            data.image, allowedIngredients);
                    dialog.revalidate();
                    dialog.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    loadingLabel.setText("Error: " + ex.getMessage());
                    dialog.revalidate();
                    dialog.repaint();
                }
            }
        };

        worker.execute();
        dialog.setVisible(true);
    }

    // Simple result holder
    private static class FetchResult {
        String fat, protein, carbs, ingredients, instructions;
        BufferedImage image;

        FetchResult(String fat, String protein, String carbs,
                String ingredients, String instructions, BufferedImage image) {
            this.fat = fat;
            this.protein = protein;
            this.carbs = carbs;
            this.ingredients = ingredients;
            this.instructions = instructions;
            this.image = image;
        }
    }

    // ------------------------------------------------------------------
    // Builds the flip card dialog UI
    // ------------------------------------------------------------------
    private void buildDetailUI(JDialog dialog, String name, String category,
            String calories, String fat, String protein,
            String carbs, String rawIngredients, String instructions,
            BufferedImage image, List<String> allowedIngredients) {
        dialog.setLayout(new BorderLayout());

        JPanel cardPanel = new JPanel(new CardLayout());

        // --- Front panel ---
        JPanel frontPanel = new JPanel(new BorderLayout(5, 5));
        frontPanel.setBackground(new Color(255, 248, 220));

        // Show real image if available, otherwise emoji placeholder
        JLabel imageLabel;
        if (image != null) {
            Image scaled = image.getScaledInstance(420, 220, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(scaled), JLabel.CENTER);
        } else {
            imageLabel = new JLabel("🍽", JLabel.CENTER);
            imageLabel.setFont(new Font(null, Font.PLAIN, 80));
        }
        imageLabel.setPreferredSize(new Dimension(420, 220));

        JLabel nameLabel = new JLabel(name, JLabel.CENTER);
        nameLabel.setFont(new Font(null, Font.BOLD, 17));
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 4, 10));

        JLabel catLabel = new JLabel(category, JLabel.CENTER);
        catLabel.setFont(new Font(null, Font.ITALIC, 13));
        catLabel.setForeground(Color.GRAY);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(255, 248, 220));
        infoPanel.setBorder(new EmptyBorder(0, 10, 12, 10));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        catLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(nameLabel);
        infoPanel.add(catLabel);

        frontPanel.add(imageLabel, BorderLayout.CENTER);
        frontPanel.add(infoPanel, BorderLayout.SOUTH);

        // --- Back panel ---
        JPanel backPanel = new JPanel(new BorderLayout(5, 5));
        backPanel.setBackground(new Color(220, 240, 255));
        backPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel statsTitle = new JLabel("Nutrition & Details", JLabel.CENTER);
        statsTitle.setFont(new Font(null, Font.BOLD, 16));
        backPanel.add(statsTitle, BorderLayout.NORTH);

        // Use BoxLayout so rows are compact and not stretched
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(220, 240, 255));

        statsPanel.add(makeStatRow("Calories", calories + " kcal"));
        statsPanel.add(makeStatRow("Protein", protein != null ? protein + "g" : "N/A"));
        statsPanel.add(makeStatRow("Carbs", carbs != null ? carbs + "g" : "N/A"));
        statsPanel.add(makeStatRow("Fat", fat != null ? fat + "g" : "N/A"));
        statsPanel.add(Box.createVerticalStrut(8));

        // Filtered ingredients
        String displayIngredients;
        boolean wasFiltered = false;
        if (rawIngredients != null && !rawIngredients.isEmpty()) {
            displayIngredients = filterIngredientsByAllowed(rawIngredients, allowedIngredients);
            wasFiltered = !displayIngredients.equals(rawIngredients);
        } else {
            displayIngredients = "Ingredients not available.";
        }

        JLabel ingrTitle = new JLabel("Ingredients" +
                (wasFiltered ? " (⚠ some filtered by Settings):" : ":"));
        ingrTitle.setFont(new Font(null, Font.BOLD, 12));
        ingrTitle.setForeground(wasFiltered ? new Color(160, 100, 0) : Color.BLACK);
        ingrTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(ingrTitle);
        statsPanel.add(Box.createVerticalStrut(3));

        JTextArea ingrArea = new JTextArea(displayIngredients);
        ingrArea.setEditable(false);
        ingrArea.setLineWrap(true);
        ingrArea.setWrapStyleWord(true);
        ingrArea.setBackground(new Color(220, 240, 255));
        ingrArea.setFont(new Font(null, Font.PLAIN, 11));
        ingrArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(ingrArea);
        statsPanel.add(Box.createVerticalStrut(8));

        if (instructions != null && !instructions.isEmpty()) {
            JLabel instrTitle = new JLabel("Instructions:");
            instrTitle.setFont(new Font(null, Font.BOLD, 12));
            instrTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            statsPanel.add(instrTitle);
            statsPanel.add(Box.createVerticalStrut(3));

            JTextArea instrArea = new JTextArea(instructions);
            instrArea.setEditable(false);
            instrArea.setLineWrap(true);
            instrArea.setWrapStyleWord(true);
            instrArea.setBackground(new Color(220, 240, 255));
            instrArea.setFont(new Font(null, Font.PLAIN, 11));
            instrArea.setAlignmentX(Component.LEFT_ALIGNMENT);
            statsPanel.add(instrArea);
        }

        JScrollPane statsScroll = new JScrollPane(statsPanel);
        statsScroll.setBorder(null);
        statsScroll.setBackground(new Color(220, 240, 255));
        backPanel.add(statsScroll, BorderLayout.CENTER);

        cardPanel.add(frontPanel, "front");
        cardPanel.add(backPanel, "back");

        // Flip listener on child panels
        final boolean[] showingFront = { true };
        java.awt.event.MouseAdapter flipListener = new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showingFront[0] = !showingFront[0];
                CardLayout cl = (CardLayout) cardPanel.getLayout();
                cl.show(cardPanel, showingFront[0] ? "front" : "back");
            }
        };
        frontPanel.addMouseListener(flipListener);
        backPanel.addMouseListener(flipListener);
        imageLabel.addMouseListener(flipListener);
        infoPanel.addMouseListener(flipListener);

        // Bottom bar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(6, 10, 10, 10));

        JLabel flipHint = new JLabel("Click card to flip for nutrition & ingredients", JLabel.CENTER);
        flipHint.setFont(new Font(null, Font.ITALIC, 11));
        flipHint.setForeground(Color.DARK_GRAY);

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(180, 180, 180));
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setFocusPainted(false);
        closeBtn.setFont(new Font(null, Font.BOLD, 13));
        closeBtn.setBorder(BorderFactory.createEmptyBorder(7, 20, 7, 20));
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnRow.add(closeBtn);

        bottomPanel.add(flipHint, BorderLayout.NORTH);
        bottomPanel.add(btnRow, BorderLayout.CENTER);

        dialog.add(cardPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
    }

    // ------------------------------------------------------------------
    // Data fetching helpers
    // ------------------------------------------------------------------
    private MealPlanData.Recipe findInMealPlanData(String mealName) {
        for (MealPlanData.MealPlan plan : MealPlanData.getAllPlans()) {
            for (MealPlanData.DayPlan day : plan.days) {
                if (day.breakfast.name.equals(mealName))
                    return day.breakfast;
                if (day.lunch.name.equals(mealName))
                    return day.lunch;
                if (day.dinner.name.equals(mealName))
                    return day.dinner;
            }
        }
        return null;
    }

    private BufferedImage loadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty())
            return null;
        try {
            return ImageIO.read(new URL(imageUrl));
        } catch (Exception e) {
            return null;
        }
    }

    private FetchResult fetchFromMealDB(String mealName, String savedImageUrl) {
        try {
            String encoded = mealName.replace(" ", "%20");
            URL url = new URL(MEALDB_SEARCH + encoded);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String response = new String(conn.getInputStream().readAllBytes());
            conn.disconnect();

            JSONObject root = new JSONObject(response);
            if (root.isNull("meals")) {
                return new FetchResult("N/A", "N/A", "N/A", "Not available.", null, null);
            }

            JSONObject meal = root.getJSONArray("meals").getJSONObject(0);

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

            String instructions = meal.optString("strInstructions", "");

            // Load image — prefer savedImageUrl, fall back to API thumb
            String imgUrl = (savedImageUrl != null && !savedImageUrl.isEmpty())
                    ? savedImageUrl
                    : meal.optString("strMealThumb", "");
            BufferedImage image = loadImage(imgUrl);

            // Nutrition
            String fat = "N/A", protein = "N/A", carbs = "N/A";
            try {
                String cleanName = mealName.replaceAll("[^a-zA-Z0-9 ]", " ").trim().replace(" ", "+");
                URL calUrl = new URL(CALORIE_URL + cleanName);
                HttpURLConnection calConn = (HttpURLConnection) calUrl.openConnection();
                calConn.setRequestMethod("GET");
                calConn.setRequestProperty("X-Api-Key", CALORIE_API_KEY);
                String calResponse = new String(calConn.getInputStream().readAllBytes());
                calConn.disconnect();
                JSONArray items = new JSONObject(calResponse).getJSONArray("items");
                if (items.length() > 0) {
                    JSONObject n = items.getJSONObject(0);
                    fat = String.format("%.1f", n.optDouble("fat_total_g", 0));
                    protein = String.format("%.1f", n.optDouble("protein_g", 0));
                    carbs = String.format("%.1f", n.optDouble("carbohydrates_total_g", 0));
                }
            } catch (Exception ignored) {
            }

            return new FetchResult(fat, protein, carbs, ingrBuilder.toString(), instructions, image);

        } catch (Exception e) {
            return new FetchResult("N/A", "N/A", "N/A", "Could not load ingredients.", null, null);
        }
    }

    // ------------------------------------------------------------------
    // Ingredient filtering
    // ------------------------------------------------------------------
    // Settings saves a list of CHECKED (allowed) ingredients.
    // For each ingredient chunk (comma-separated), we extract meaningful words
    // (length > 3, letters only) and check if any of them match a deselected
    // ingredient — i.e. a word that IS a known ingredient name but is NOT
    // in the allowed list.
    // We detect "known ingredient name" by checking if the word matches
    // (case-insensitive) any entry in a hardcoded ingredient keyword list
    // that mirrors what Settings tracks.
    private static final List<String> KNOWN_INGREDIENT_KEYWORDS = java.util.Arrays.asList(
            "beef", "chicken", "pork", "lamb", "fish", "cheese", "mushroom", "pepper",
            "tomato", "onion", "rice", "potato", "milk", "cream", "vinegar", "olive",
            "sauce", "wine", "beans", "lemon", "lime", "orange", "sugar", "flour",
            "butter", "garlic", "ginger", "cumin", "paprika", "basil", "thyme",
            "rosemary", "parsley", "cilantro", "spinach", "broccoli", "carrot",
            "celery", "avocado", "cucumber", "zucchini", "asparagus", "salmon",
            "shrimp", "tuna", "turkey", "bacon", "sausage", "egg", "eggs",
            "yogurt", "honey", "oats", "quinoa", "pasta", "bread", "tortilla",
            "salsa", "hummus", "tahini", "soy", "sesame", "coconut", "almond",
            "peanut", "walnut", "cashew", "tofu", "tempeh", "edamame", "kale",
            "lettuce", "cabbage", "corn", "peas", "lentils", "chickpeas", "feta",
            "parmesan", "mozzarella", "cheddar", "mayo", "mustard", "ketchup");

    private String filterIngredientsByAllowed(String rawIngredients, List<String> allowedIngredients) {
        if (allowedIngredients == null || allowedIngredients.isEmpty())
            return rawIngredients;

        // Load deselected ingredients directly
        List<String> deselected = db.loadDeselectedIngredients(username);
        if (deselected == null || deselected.isEmpty())
            return rawIngredients;

        // Normalize to lowercase
        List<String> deselectedLower = new ArrayList<>();
        for (String d : deselected) {
            deselectedLower.add(d.toLowerCase().trim());
        }

        String[] parts = rawIngredients.split(",");
        List<String> kept = new ArrayList<>();

        for (String part : parts) {
            String trimmed = part.trim().toLowerCase();
            boolean blocked = false;

            for (String d : deselectedLower) {
                if (trimmed.contains(d)) {
                    blocked = true;
                    break;
                }
            }

            if (!blocked)
                kept.add(part.trim());
        }

        if (kept.isEmpty())
            return "(All ingredients filtered by your Settings preferences)";
        return String.join(", ", kept);
    }

    // ------------------------------------------------------------------
    // UI helpers
    // ------------------------------------------------------------------
    private JPanel makeHeaderRow() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(70, 130, 180));
        header.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JPanel inner = new JPanel(new GridLayout(1, 4, 10, 0));
        inner.setBackground(new Color(70, 130, 180));
        inner.add(makeHeaderLabel("#"));
        inner.add(makeHeaderLabel("Meal Name"));
        inner.add(makeHeaderLabel("Category"));
        inner.add(makeHeaderLabel("Calories"));

        header.add(inner, BorderLayout.CENTER);
        return header;
    }

    private JLabel makeHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(null, Font.BOLD, 13));
        label.setForeground(Color.WHITE);
        return label;
    }

    // Compact row using BorderLayout — no stretching
    private JPanel makeMealRow(int index, String name, String category, String calories,
            String fat, String protein, String carbs,
            String ingredients, String imageUrl,
            List<String> allowedIngredients) {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setPreferredSize(new Dimension(0, 36));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color rowBg = (index % 2 == 0) ? new Color(240, 248, 255) : Color.WHITE;
        row.setBackground(rowBg);

        JLabel numLabel = new JLabel(String.valueOf(index));
        JLabel nameLabel = new JLabel(name);
        JLabel catLabel = new JLabel(category);
        JLabel calLabel = new JLabel(calories.equals("N/A") ? "N/A" : calories + " kcal");

        numLabel.setFont(new Font(null, Font.PLAIN, 12));
        nameLabel.setFont(new Font(null, Font.BOLD, 12));
        catLabel.setFont(new Font(null, Font.PLAIN, 12));
        calLabel.setFont(new Font(null, Font.PLAIN, 12));

        if (!calories.equals("N/A")) {
            try {
                int cal = Integer.parseInt(calories.trim());
                if (cal < 300)
                    calLabel.setForeground(new Color(0, 150, 0));
                else if (cal < 600)
                    calLabel.setForeground(new Color(200, 130, 0));
                else
                    calLabel.setForeground(new Color(180, 0, 0));
            } catch (NumberFormatException ignored) {
            }
        }

        row.add(numLabel);
        row.add(nameLabel);
        row.add(catLabel);
        row.add(calLabel);

        row.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showRecipeDetail(name, category, calories, fat, protein,
                        carbs, ingredients, imageUrl, allowedIngredients);
            }

            public void mouseEntered(java.awt.event.MouseEvent e) {
                row.setBackground(new Color(173, 216, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                row.setBackground(rowBg);
            }
        });

        return row;
    }

    private JSeparator makeThinDivider() {
        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(220, 220, 220));
        return sep;
    }

    // Compact stat row — fixed height, left aligned
    private JPanel makeStatRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        row.setBackground(new Color(220, 240, 255));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font(null, Font.BOLD, 12));

        JLabel val = new JLabel(value);
        val.setFont(new Font(null, Font.PLAIN, 12));

        row.add(lbl);
        row.add(val);
        return row;
    }

    private String getString(Document doc, String key, String fallback) {
        String val = doc.getString(key);
        return (val != null) ? val : fallback;
    }
}