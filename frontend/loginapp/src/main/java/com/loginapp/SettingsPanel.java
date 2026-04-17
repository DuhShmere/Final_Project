package com.loginapp;

import java.awt.*;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.json.JSONArray;
import org.json.JSONObject;

public class SettingsPanel extends JPanel {

    private static final String INGREDIENTS_URL = "https://www.themealdb.com/api/json/v1/1/list.php?i=list";

    private List<JCheckBox> allCheckBoxes = new ArrayList<>();
    private List<String> allIngredients = new ArrayList<>();
    private MongoDBHelper db;
    private String username;
    private JPanel listPanel;
    private JScrollPane scrollPane;
    private JTextField searchField;

    public SettingsPanel(Runnable onHome, MongoDBHelper db, String username) {
        this.db = db;
        this.username = username;

        setLayout(new BorderLayout());

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton homeBtn = new JButton("← Home");
        JButton saveBtn = new JButton("Save Preferences");
        JLabel savedLabel = new JLabel("");
        savedLabel.setForeground(Color.GREEN);

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
        topBar.add(savedLabel);
        add(topBar, BorderLayout.NORTH);

        // Search bar on the right side
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setPreferredSize(new Dimension(200, 0));

        JLabel searchTitle = new JLabel("Search Ingredients", JLabel.CENTER);
        searchTitle.setFont(new Font(null, Font.BOLD, 13));

        searchField = new JTextField();
        searchField.setFont(new Font(null, Font.PLAIN, 13));
        searchField.putClientProperty("JTextField.placeholderText", "Type to search...");

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font(null, Font.PLAIN, 12));
        clearBtn.addActionListener(e -> searchField.setText(""));

        // Results panel inside search
        JPanel searchResultsPanel = new JPanel();
        searchResultsPanel.setLayout(new BoxLayout(searchResultsPanel, BoxLayout.Y_AXIS));
        JScrollPane searchScroll = new JScrollPane(searchResultsPanel);
        searchScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        searchScroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel searchTop = new JPanel(new BorderLayout(4, 4));
        searchTop.add(searchTitle, BorderLayout.NORTH);
        searchTop.add(searchField, BorderLayout.CENTER);
        searchTop.add(clearBtn, BorderLayout.EAST);

        searchPanel.add(searchTop, BorderLayout.NORTH);
        searchPanel.add(searchScroll, BorderLayout.CENTER);

        // Live search listener
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterIngredients(searchResultsPanel);
            }

            public void removeUpdate(DocumentEvent e) {
                filterIngredients(searchResultsPanel);
            }

            public void changedUpdate(DocumentEvent e) {
                filterIngredients(searchResultsPanel);
            }
        });

        add(searchPanel, BorderLayout.EAST);

        // Main list panel
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Dietary Preferences & Ingredients");
        title.setFont(new Font(null, Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.add(title);
        listPanel.add(Box.createVerticalStrut(10));

        JLabel loadingLabel = new JLabel("Loading ingredients from TheMealDB...");
        loadingLabel.setFont(new Font(null, Font.ITALIC, 14));
        loadingLabel.setForeground(Color.GRAY);
        loadingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        listPanel.add(loadingLabel);

        scrollPane = new JScrollPane(listPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Load ingredients in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private List<String> fetchedIngredients = new ArrayList<>();

            @Override
            protected Void doInBackground() throws Exception {
                fetchedIngredients = fetchIngredientsFromAPI();
                return null;
            }

            @Override
            protected void done() {
                listPanel.remove(loadingLabel);
                allIngredients = fetchedIngredients;

                // Load saved preferences
                List<String> savedPreferences = db.loadUserPreferences(username);

                // Build the full ingredient list
                buildIngredientList(fetchedIngredients, savedPreferences);

                listPanel.revalidate();
                listPanel.repaint();
            }
        };
        worker.execute();
    }

    private List<String> fetchIngredientsFromAPI() {
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
                if (!name.isEmpty()) {
                    ingredients.add(name);
                }
            }

            ingredients.sort(String::compareToIgnoreCase);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ingredients;
    }

    private void buildIngredientList(List<String> ingredients, List<String> savedPreferences) {
        char currentLetter = 0;

        for (String ingredient : ingredients) {
            char firstLetter = Character.toUpperCase(ingredient.charAt(0));

            if (firstLetter != currentLetter) {
                currentLetter = firstLetter;

                JLabel catLabel = new JLabel("— " + firstLetter + " —");
                catLabel.setFont(new Font(null, Font.BOLD, 14));
                catLabel.setForeground(new Color(80, 80, 80));
                catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                catLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
                listPanel.add(catLabel);
            }

            JCheckBox checkBox = new JCheckBox(ingredient);
            checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Default all checked
            if (savedPreferences == null || savedPreferences.isEmpty()) {
                checkBox.setSelected(true);
            } else {
                checkBox.setSelected(savedPreferences.contains(ingredient));
            }

            allCheckBoxes.add(checkBox);
            listPanel.add(checkBox);
        }
    }

    private void filterIngredients(JPanel searchResultsPanel) {
        String query = searchField.getText().trim().toLowerCase();
        searchResultsPanel.removeAll();

        if (query.isEmpty()) {
            searchResultsPanel.revalidate();
            searchResultsPanel.repaint();
            return;
        }

        // Find matching checkboxes and mirror them in the search results panel
        for (JCheckBox original : allCheckBoxes) {
            if (original.getText().toLowerCase().contains(query)) {
                // Create a mirrored checkbox that syncs with the original
                JCheckBox mirror = new JCheckBox(original.getText());
                mirror.setSelected(original.isSelected());
                mirror.setAlignmentX(Component.LEFT_ALIGNMENT);

                mirror.addActionListener(e -> {
                    original.setSelected(mirror.isSelected());
                });

                original.addActionListener(e -> {
                    mirror.setSelected(original.isSelected());
                });

                searchResultsPanel.add(mirror);
            }
        }

        searchResultsPanel.revalidate();
        searchResultsPanel.repaint();
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