package com.loginapp;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.bson.Document;

public class ViewRecipesPanel extends JPanel {

    private MongoDBHelper db;
    private String username;
    private JPanel recipesListPanel;

    public ViewRecipesPanel(MongoDBHelper db, String username) {
        this.db = db;
        this.username = username;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Title
        JLabel title = new JLabel("My Liked Recipes", JLabel.CENTER);
        title.setFont(new Font(null, Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Recipes list panel
        recipesListPanel = new JPanel();
        recipesListPanel.setLayout(new BoxLayout(recipesListPanel, BoxLayout.Y_AXIS));
        recipesListPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(recipesListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);

        // Load recipes on open
        loadRecipes();
    }

    private void loadRecipes() {
        recipesListPanel.removeAll();

        List<Document> likedMeals = db.loadLikedMeals(username);

        if (likedMeals.isEmpty()) {
            JLabel emptyLabel = new JLabel("You have not liked any meals yet!", JLabel.CENTER);
            emptyLabel.setFont(new Font(null, Font.ITALIC, 16));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(new EmptyBorder(40, 0, 0, 0));
            recipesListPanel.add(emptyLabel);
        } else {
            // Header row
            JPanel header = makeHeaderRow();
            recipesListPanel.add(header);
            recipesListPanel.add(Box.createVerticalStrut(4));

            // One row per liked meal
            for (int i = 0; i < likedMeals.size(); i++) {
                Document meal = likedMeals.get(i);
                String name = meal.getString("name") != null ? meal.getString("name") : "Unknown";
                String category = meal.getString("category") != null ? meal.getString("category") : "Unknown";
                String calories = meal.getString("calories") != null ? meal.getString("calories") : "N/A";

                JPanel row = makeMealRow(i + 1, name, category, calories);
                recipesListPanel.add(row);
                recipesListPanel.add(Box.createVerticalStrut(6));
            }

            // Total count label
            JLabel countLabel = new JLabel("Total liked recipes: " + likedMeals.size());
            countLabel.setFont(new Font(null, Font.ITALIC, 12));
            countLabel.setForeground(Color.GRAY);
            countLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            recipesListPanel.add(Box.createVerticalStrut(10));
            recipesListPanel.add(countLabel);
        }

        recipesListPanel.revalidate();
        recipesListPanel.repaint();
    }

    private JPanel makeHeaderRow() {
        JPanel header = new JPanel(new GridLayout(1, 4, 10, 0));
        header.setBackground(new Color(70, 130, 180));
        header.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        header.add(makeHeaderLabel("#"));
        header.add(makeHeaderLabel("Meal Name"));
        header.add(makeHeaderLabel("Category"));
        header.add(makeHeaderLabel("Calories"));

        return header;
    }

    private JLabel makeHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(null, Font.BOLD, 13));
        label.setForeground(Color.WHITE);
        return label;
    }

    private JPanel makeMealRow(int index, String name, String category, String calories) {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Alternate row colors
        if (index % 2 == 0) {
            row.setBackground(new Color(240, 248, 255));
        } else {
            row.setBackground(Color.WHITE);
        }

        JLabel numLabel = new JLabel(String.valueOf(index));
        JLabel nameLabel = new JLabel(name);
        JLabel catLabel = new JLabel(category);
        JLabel calLabel = new JLabel(calories.equals("N/A") ? "N/A" : calories + " kcal");

        numLabel.setFont(new Font(null, Font.PLAIN, 13));
        nameLabel.setFont(new Font(null, Font.BOLD, 13));
        catLabel.setFont(new Font(null, Font.PLAIN, 13));
        calLabel.setFont(new Font(null, Font.PLAIN, 13));

        // Color code the calories
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

        // Hover effect
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            Color original = row.getBackground();

            public void mouseEntered(java.awt.event.MouseEvent e) {
                row.setBackground(new Color(173, 216, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                row.setBackground(original);
            }
        });

        return row;
    }
}