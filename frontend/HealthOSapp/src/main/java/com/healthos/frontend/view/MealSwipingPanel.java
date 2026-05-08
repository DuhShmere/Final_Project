package com.healthos.frontend.view;

import com.healthos.backend.database.MongoDBHelper;
import com.healthos.frontend.controller.MealController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MealSwipingPanel extends JPanel {

    private static final int MAX_ATTEMPTS = 20;
    private String imageUrl = "";

    private List<MealData> mealHistory = new ArrayList<>();
    private int historyIndex = -1;

    private static class MealData {
        String name, category, area, ingredients;
        String calories, fat, protein, carbs;
        BufferedImage image;

        MealData(String name, String category, String area, String ingredients,
                String calories, String fat, String protein, String carbs,
                BufferedImage image) {
            this.name = name;
            this.category = category;
            this.area = area;
            this.ingredients = ingredients;
            this.calories = calories;
            this.fat = fat;
            this.protein = protein;
            this.carbs = carbs;
            this.image = image;
        }
    }

    private boolean showingFront = true;
    private List<String> userPreferences = null;

    // Current meal data
    private String mealName = "Loading...";
    private String mealCategory = "";
    private String mealArea = "";
    private BufferedImage mealImage = null;
    private String calories = "--";
    private String fat = "--";
    private String protein = "--";
    private String carbs = "--";
    private String ingredients = "";

    private MealController mealController;

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
    private JLabel historyLabel = new JLabel("", JLabel.CENTER);

    public MealSwipingPanel(MongoDBHelper db, String username) {
        this.mealController = new MealController(db, username);
        userPreferences = mealController.loadUserPreferences();

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
        historyLabel.setFont(new Font(null, Font.ITALIC, 11));
        historyLabel.setForeground(Color.GRAY);

        styleButton(likeButton, new Color(100, 200, 100));
        styleButton(dislikeButton, new Color(200, 100, 100));
        styleButton(nextButton, new Color(180, 180, 180));
        styleButton(backButton, new Color(180, 180, 180));

        // Disable back button initially since there is no history yet
        backButton.setEnabled(false);

        likeButton.addActionListener(e -> {
            mealController.saveLikedMeal(mealName, mealCategory, calories,
                    fat, protein, carbs, ingredients, imageUrl);
            statusLabel.setForeground(new Color(0, 150, 0));
            statusLabel.setText("Liked: " + mealName + " saved!");
        });

        dislikeButton.addActionListener(e -> {
            mealController.saveDislikedMeal(mealName);
            statusLabel.setForeground(new Color(180, 0, 0));
            statusLabel.setText("Disliked: " + mealName);
        });

        nextButton.addActionListener(e -> {
            statusLabel.setText("");
            // If we are not at the end of history navigate forward
            if (historyIndex < mealHistory.size() - 1) {
                historyIndex++;
                displayMealFromHistory(mealHistory.get(historyIndex));
                updateHistoryLabel();
                updateBackButton();
            } else {
                // Fetch a brand new meal
                loadNextMeal();
            }
        });

        backButton.addActionListener(e -> {
            statusLabel.setText("");
            if (historyIndex > 0) {
                historyIndex--;
                displayMealFromHistory(mealHistory.get(historyIndex));
                updateHistoryLabel();
                updateBackButton();
            }
        });

        buttonRow.add(dislikeButton);
        buttonRow.add(backButton);
        buttonRow.add(nextButton);
        buttonRow.add(likeButton);

        JPanel labelRow = new JPanel(new GridLayout(2, 1));
        labelRow.add(flipHint);
        labelRow.add(historyLabel);

        bottomPanel.add(labelRow, BorderLayout.NORTH);
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

    // Display a meal from history without fetching from API
    private void displayMealFromHistory(MealData meal) {
        showingFront = true;
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, "front");

        mealName = meal.name;
        mealCategory = meal.category;
        mealArea = meal.area;
        mealImage = meal.image;
        ingredients = meal.ingredients;
        calories = meal.calories;
        fat = meal.fat;
        protein = meal.protein;
        carbs = meal.carbs;

        updateFrontUI();
        updateBackUI();
    }

    private void updateHistoryLabel() {
        historyLabel.setText("Meal " + (historyIndex + 1) + " of " + mealHistory.size());
    }

    private void updateBackButton() {
        backButton.setEnabled(historyIndex > 0);
    }

    private void loadNextMeal() {
        showingFront = true;
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, "front");

        imageLabel.setIcon(null);
        imageLabel.setText("Loading...");
        nameLabel.setText("Finding a meal for you...");
        categoryLabel.setText("");
        historyLabel.setText("Loading...");

        SwingWorker<MealController.MealFetchResult, Void> worker =
                new SwingWorker<MealController.MealFetchResult, Void>() {
            @Override
            protected MealController.MealFetchResult doInBackground() {
                for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
                    MealController.MealFetchResult result = mealController.fetchMealData(userPreferences);
                    if (result != null) return result;
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    MealController.MealFetchResult result = get();
                    if (result != null) {
                        mealName = result.name;
                        mealCategory = result.category;
                        mealArea = result.area;
                        ingredients = result.ingredients;
                        calories = result.calories;
                        fat = result.fat;
                        protein = result.protein;
                        carbs = result.carbs;
                        mealImage = result.image;
                        imageUrl = result.imageUrl;

                        MealData newMeal = new MealData(
                                mealName, mealCategory, mealArea, ingredients,
                                calories, fat, protein, carbs, mealImage);

                        while (mealHistory.size() > historyIndex + 1) {
                            mealHistory.remove(mealHistory.size() - 1);
                        }

                        mealHistory.add(newMeal);
                        historyIndex = mealHistory.size() - 1;

                        updateFrontUI();
                        updateBackUI();
                        updateHistoryLabel();
                        updateBackButton();
                    } else {
                        showNoMealsFound();
                    }
                } catch (Exception ex) {
                    showNoMealsFound();
                }
            }
        };
        worker.execute();
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
        historyLabel.setText("");
    }

    private void updateFrontUI() {
        likeButton.setEnabled(true);
        dislikeButton.setEnabled(true);
        cardPanel.setBackground(new Color(255, 248, 220));
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