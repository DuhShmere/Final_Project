package com.healthos.frontend.view;
import com.healthos.backend.database.*;
import com.healthos.backend.model.*;
import com.healthos.backend.model.*;

import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.bson.Document;

public class RecipeDetailPanel extends JPanel {

    public RecipeDetailPanel(Document meal, Runnable onBack) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Top bar with back button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("← Back to Recipes");
        backBtn.addActionListener(e -> onBack.run());
        topBar.add(backBtn);
        add(topBar, BorderLayout.NORTH);

        // Main scroll content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));

        String name = meal.getString("name") != null ? meal.getString("name") : "Unknown";
        String category = meal.getString("category") != null ? meal.getString("category") : "";
        String area = meal.getString("area") != null ? meal.getString("area") : "";
        String calories = meal.getString("calories") != null ? meal.getString("calories") : "N/A";
        String fat = meal.getString("fat") != null ? meal.getString("fat") : "N/A";
        String protein = meal.getString("protein") != null ? meal.getString("protein") : "N/A";
        String carbs = meal.getString("carbs") != null ? meal.getString("carbs") : "N/A";
        String ingredients = meal.getString("ingredients") != null ? meal.getString("ingredients") : "";
        String instructions = meal.getString("instructions") != null ? meal.getString("instructions")
                : "No instructions available.";
        String imageUrl = meal.getString("imageUrl") != null ? meal.getString("imageUrl") : "";

        // Title
        JLabel title = new JLabel(name);
        title.setFont(new Font(null, Font.BOLD, 22));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);

        // Category and area
        JLabel meta = new JLabel(category + (area.isEmpty() ? "" : " · " + area));
        meta.setFont(new Font(null, Font.ITALIC, 13));
        meta.setForeground(Color.GRAY);
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(meta);
        content.add(Box.createVerticalStrut(15));

        // Image
        if (!imageUrl.isEmpty()) {
            JLabel imageLabel = new JLabel("Loading image...");
            imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(imageLabel);

            // Load image in background
            new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    Image img = ImageIO.read(new URL(imageUrl));
                    Image scaled = img.getScaledInstance(400, 250, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }

                @Override
                protected void done() {
                    try {
                        imageLabel.setIcon(get());
                        imageLabel.setText("");
                    } catch (Exception e) {
                        imageLabel.setText("Image not available");
                    }
                }
            }.execute();
            content.add(Box.createVerticalStrut(15));
        }

        // Nutrition facts section
        JLabel nutritionTitle = new JLabel("Nutrition Facts");
        nutritionTitle.setFont(new Font(null, Font.BOLD, 16));
        nutritionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(nutritionTitle);
        content.add(Box.createVerticalStrut(5));

        JPanel nutritionPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        nutritionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        nutritionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        nutritionPanel.add(makeStatLabel("Calories", calories.equals("N/A") ? "N/A" : calories + " kcal"));
        nutritionPanel.add(makeStatLabel("Fat", fat.equals("N/A") ? "N/A" : fat + "g"));
        nutritionPanel.add(makeStatLabel("Protein", protein.equals("N/A") ? "N/A" : protein + "g"));
        nutritionPanel.add(makeStatLabel("Carbs", carbs.equals("N/A") ? "N/A" : carbs + "g"));

        content.add(nutritionPanel);
        content.add(Box.createVerticalStrut(15));

        // Ingredients section
        JLabel ingTitle = new JLabel("Ingredients");
        ingTitle.setFont(new Font(null, Font.BOLD, 16));
        ingTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(ingTitle);
        content.add(Box.createVerticalStrut(5));

        JTextArea ingArea = new JTextArea(ingredients);
        ingArea.setEditable(false);
        ingArea.setLineWrap(true);
        ingArea.setWrapStyleWord(true);
        ingArea.setFont(new Font(null, Font.PLAIN, 13));
        ingArea.setBackground(new Color(250, 250, 245));
        ingArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        ingArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(ingArea);
        content.add(Box.createVerticalStrut(15));

        // Instructions section
        JLabel instTitle = new JLabel("Instructions");
        instTitle.setFont(new Font(null, Font.BOLD, 16));
        instTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(instTitle);
        content.add(Box.createVerticalStrut(5));

        JTextArea instArea = new JTextArea(instructions);
        instArea.setEditable(false);
        instArea.setLineWrap(true);
        instArea.setWrapStyleWord(true);
        instArea.setFont(new Font(null, Font.PLAIN, 13));
        instArea.setBackground(new Color(250, 250, 245));
        instArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        instArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(instArea);

        JScrollPane scroll = new JScrollPane(content);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel makeStatLabel(String name, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel label = new JLabel(name + ":");
        label.setFont(new Font(null, Font.BOLD, 13));
        JLabel val = new JLabel(value);
        val.setFont(new Font(null, Font.PLAIN, 13));
        panel.add(label);
        panel.add(val);
        return panel;
    }
}