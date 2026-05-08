package com.healthos.frontend.view;

import com.healthos.backend.database.MongoDBHelper;
import com.healthos.frontend.controller.SettingsController;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SettingsPanel extends JPanel {

    // Parent categories that will have subsections
    private static final List<String> PARENT_KEYWORDS = Arrays.asList(
            "Beef", "Chicken", "Pork", "Lamb", "Fish", "Cheese",
            "Mushroom", "Pepper", "Tomato", "Onion", "Rice", "Potato",
            "Milk", "Cream", "Vinegar", "Oil", "Sauce", "Wine",
            "Beans", "Lemon", "Orange", "Sugar", "Flour", "Butter");

    private List<JCheckBox> allCheckBoxes = new ArrayList<>();
    private List<String> allIngredients = new ArrayList<>();
    private SettingsController settingsController;
    private JPanel listPanel;
    private JScrollPane scrollPane;
    private JTextField searchField;

    public SettingsPanel(Runnable onHome, MongoDBHelper db, String username) {
        this.settingsController = new SettingsController(db, username);

        setLayout(new BorderLayout());

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // JButton homeBtn = new JButton("← Home");
        JButton saveBtn = new JButton("Save Preferences");
        JLabel savedLabel = new JLabel("");
        savedLabel.setForeground(Color.GREEN);

        // homeBtn.addActionListener(e -> {
        // savePreferences();
        // onHome.run();
        // });

        saveBtn.addActionListener(e -> {
            savePreferences();
            JOptionPane.showMessageDialog(this, "Preferences saved!", "Saved",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        // topBar.add(homeBtn);
        topBar.add(saveBtn);
        topBar.add(savedLabel);
        add(topBar, BorderLayout.NORTH);

        // Search bar on the right
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setPreferredSize(new Dimension(200, 0));

        JLabel searchTitle = new JLabel("Search Ingredients", JLabel.CENTER);
        searchTitle.setFont(new Font(null, Font.BOLD, 13));

        searchField = new JTextField();
        searchField.setFont(new Font(null, Font.PLAIN, 13));

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font(null, Font.PLAIN, 12));
        clearBtn.addActionListener(e -> searchField.setText(""));

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
                List<String> savedPreferences = settingsController.loadUserPreferences();
                buildIngredientList(fetchedIngredients, savedPreferences);
                listPanel.revalidate();
                listPanel.repaint();
            }
        };
        worker.execute();
    }

    private List<String> fetchIngredientsFromAPI() {
        return settingsController.fetchIngredientsFromAPI();
    }

    private void buildIngredientList(List<String> ingredients, List<String> savedPreferences) {
        // Group ingredients by parent keyword
        Map<String, List<String>> grouped = new LinkedHashMap<>();
        List<String> standalone = new ArrayList<>();

        for (String ingredient : ingredients) {
            String parent = findParent(ingredient);
            if (parent != null && !parent.equalsIgnoreCase(ingredient)) {
                grouped.computeIfAbsent(parent, k -> new ArrayList<>()).add(ingredient);
            } else if (parent != null && parent.equalsIgnoreCase(ingredient)) {
                // The ingredient IS the parent keyword itself
                grouped.computeIfAbsent(parent, k -> new ArrayList<>());
                standalone.add(ingredient);
            } else {
                standalone.add(ingredient);
            }
        }

        char currentLetter = 0;

        // Build grouped parents first
        for (String ingredient : ingredients) {
            char firstLetter = Character.toUpperCase(ingredient.charAt(0));

            // Letter header
            if (firstLetter != currentLetter) {
                currentLetter = firstLetter;
                JLabel letterLabel = new JLabel("— " + firstLetter + " —");
                letterLabel.setFont(new Font(null, Font.BOLD, 14));
                letterLabel.setForeground(new Color(80, 80, 80));
                letterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                letterLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
                listPanel.add(letterLabel);
            }

            String parent = findParent(ingredient);

            if (parent != null && parent.equalsIgnoreCase(ingredient)
                    && grouped.containsKey(parent)
                    && !grouped.get(parent).isEmpty()) {
                // This is a parent with children — make it collapsible
                addCollapsibleGroup(parent, grouped.get(parent), savedPreferences);
            } else if (parent == null || parent.equalsIgnoreCase(ingredient)) {
                // Standalone ingredient
                addCheckbox(ingredient, savedPreferences);
            }
            // Skip children here — they are added inside collapsible group
        }
    }

    private void addCollapsibleGroup(String parentName, List<String> children,
            List<String> savedPreferences) {
        // Parent row with toggle button
        JPanel parentRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        parentRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        parentRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton toggleBtn = new JButton("▶");
        toggleBtn.setFont(new Font(null, Font.PLAIN, 10));
        toggleBtn.setPreferredSize(new Dimension(28, 22));
        toggleBtn.setFocusPainted(false);
        toggleBtn.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        JCheckBox parentCheckbox = new JCheckBox(parentName);
        parentCheckbox.setFont(new Font(null, Font.BOLD, 13));
        boolean parentSelected = savedPreferences == null || savedPreferences.isEmpty()
                || savedPreferences.contains(parentName);
        parentCheckbox.setSelected(parentSelected);
        allCheckBoxes.add(parentCheckbox);

        parentRow.add(toggleBtn);
        parentRow.add(parentCheckbox);
        listPanel.add(parentRow);

        // Children panel (hidden by default)
        JPanel childrenPanel = new JPanel();
        childrenPanel.setLayout(new BoxLayout(childrenPanel, BoxLayout.Y_AXIS));
        childrenPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        childrenPanel.setVisible(false);
        childrenPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String child : children) {
            JCheckBox childBox = new JCheckBox(child);
            childBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            boolean selected = savedPreferences == null || savedPreferences.isEmpty()
                    || savedPreferences.contains(child);
            childBox.setSelected(selected);
            allCheckBoxes.add(childBox);
            childrenPanel.add(childBox);
        }

        listPanel.add(childrenPanel);

        // Toggle button shows/hides children
        toggleBtn.addActionListener(e -> {
            boolean visible = childrenPanel.isVisible();
            childrenPanel.setVisible(!visible);
            toggleBtn.setText(visible ? "▶" : "▼");
            listPanel.revalidate();
            listPanel.repaint();
        });

        // Parent checkbox toggles all children
        parentCheckbox.addActionListener(e -> {
            boolean selected = parentCheckbox.isSelected();
            for (Component c : childrenPanel.getComponents()) {
                if (c instanceof JCheckBox) {
                    ((JCheckBox) c).setSelected(selected);
                }
            }
        });
    }

    private void addCheckbox(String ingredient, List<String> savedPreferences) {
        JCheckBox checkBox = new JCheckBox(ingredient);
        checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        boolean selected = savedPreferences == null || savedPreferences.isEmpty()
                || savedPreferences.contains(ingredient);
        checkBox.setSelected(selected);
        allCheckBoxes.add(checkBox);
        listPanel.add(checkBox);
    }

    // Finds which parent keyword an ingredient belongs to
    private String findParent(String ingredient) {
        for (String keyword : PARENT_KEYWORDS) {
            if (ingredient.toLowerCase().contains(keyword.toLowerCase())) {
                return keyword;
            }
        }
        return null;
    }

    private void filterIngredients(JPanel searchResultsPanel) {
        String query = searchField.getText().trim().toLowerCase();
        searchResultsPanel.removeAll();

        if (query.isEmpty()) {
            searchResultsPanel.revalidate();
            searchResultsPanel.repaint();
            return;
        }

        for (JCheckBox original : allCheckBoxes) {
            if (original.getText().toLowerCase().contains(query)) {
                JCheckBox mirror = new JCheckBox(original.getText());
                mirror.setSelected(original.isSelected());
                mirror.setAlignmentX(Component.LEFT_ALIGNMENT);

                mirror.addActionListener(e -> original.setSelected(mirror.isSelected()));
                original.addActionListener(e -> mirror.setSelected(original.isSelected()));

                searchResultsPanel.add(mirror);
            }
        }

        searchResultsPanel.revalidate();
        searchResultsPanel.repaint();
    }

    private void savePreferences() {
        List<String> checked = new ArrayList<>();
        List<String> unchecked = new ArrayList<>();
        for (JCheckBox cb : allCheckBoxes) {
            if (cb.isSelected())
                checked.add(cb.getText());
            else
                unchecked.add(cb.getText());
        }
        settingsController.savePreferences(checked, unchecked);
    }
}