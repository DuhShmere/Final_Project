package com.loginapp;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class SettingsPanel extends JPanel {

    private List<JCheckBox> allCheckBoxes = new ArrayList<>();
    private MongoDBHelper db;
    private String username;

    public SettingsPanel(Runnable onHome, MongoDBHelper db, String username) {
        this.db = db;
        this.username = username;

        setLayout(new BorderLayout());

        // Top bar with return button and save button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton homeBtn = new JButton("← Home");
        JButton saveBtn = new JButton("Save Preferences");

        homeBtn.addActionListener(e -> {
            savePreferences();
            onHome.run();
        });

        saveBtn.addActionListener(e -> {
            savePreferences();
            JOptionPane.showMessageDialog(this, "Preferences saved!", "Saved", JOptionPane.INFORMATION_MESSAGE);
        });

        topBar.add(homeBtn);
        topBar.add(saveBtn);
        add(topBar, BorderLayout.NORTH);

        // Title
        JLabel title = new JLabel("Dietary Preferences & Ingredients");
        title.setFont(new Font(null, Font.BOLD, 18));
        title.setHorizontalAlignment(JLabel.CENTER);

        // Main panel with categories
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        listPanel.add(title);
        listPanel.add(Box.createVerticalStrut(10));

        // Define categories and ingredients
        String[][] categories = {
                { " Meats", "Chicken", "Beef", "Pork", "Lamb", "Turkey", "Duck", "Bacon", "Sausage", "Veal",
                        "Bison" },
                { " Seafood", "Salmon", "Tuna", "Shrimp", "Cod", "Tilapia", "Crab", "Lobster", "Scallops", "Sardines",
                        "Mahi Mahi" },
                { " Vegetables", "Broccoli", "Spinach", "Carrots", "Bell Peppers", "Onions", "Garlic", "Zucchini",
                        "Mushrooms", "Tomatoes", "Cucumber", "Celery", "Asparagus", "Kale", "Cabbage", "Corn" },
                { " Fruits", "Apples", "Bananas", "Oranges", "Strawberries", "Blueberries", "Grapes", "Mango",
                        "Pineapple", "Watermelon", "Peaches", "Pears", "Cherries" },
                { " Dairy", "Milk", "Cheese", "Butter", "Yogurt", "Cream", "Sour Cream", "Cream Cheese", "Mozzarella",
                        "Cheddar", "Parmesan" },
                { " Grains & Pasta", "Rice", "Pasta", "Bread", "Oats", "Quinoa", "Barley", "Couscous", "Tortillas",
                        "Noodles", "Flour" },
                { " Eggs & Legumes", "Eggs", "Black Beans", "Chickpeas", "Lentils", "Kidney Beans", "Edamame", "Tofu",
                        "Pinto Beans" },
                { " Nuts & Seeds", "Almonds", "Peanuts", "Walnuts", "Cashews", "Sunflower Seeds", "Chia Seeds",
                        "Flaxseeds", "Pecans" },
                { " Condiments & Sauces", "Olive Oil", "Soy Sauce", "Hot Sauce", "Ketchup", "Mustard", "Mayonnaise",
                        "Vinegar", "Honey", "Maple Syrup" },
                { " Herbs & Spices", "Salt", "Pepper", "Cumin", "Paprika", "Oregano", "Basil", "Thyme", "Rosemary",
                        "Cinnamon", "Turmeric", "Ginger" }
        };

        // Load saved preferences from MongoDB
        List<String> savedPreferences = db.loadUserPreferences(username);

        // Build the list
        for (String[] category : categories) {
            JLabel catLabel = new JLabel(category[0]);
            catLabel.setFont(new Font(null, Font.BOLD, 15));
            catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            catLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
            listPanel.add(catLabel);

            for (int i = 1; i < category.length; i++) {
                JCheckBox checkBox = new JCheckBox(category[i]);
                checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);

                // If no saved prefs default to all checked, otherwise load from MongoDB
                if (savedPreferences == null) {
                    checkBox.setSelected(true);
                } else {
                    checkBox.setSelected(savedPreferences.contains(category[i]));
                }

                allCheckBoxes.add(checkBox);
                listPanel.add(checkBox);
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void savePreferences() {
        List<String> checked = new ArrayList<>();
        for (JCheckBox cb : allCheckBoxes) {
            if (cb.isSelected()) {
                checked.add(cb.getText());
            }
        }
        db.saveUserPreferences(username, checked);
    }
}