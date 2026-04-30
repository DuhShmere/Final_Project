package com.loginapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.BorderFactory;  
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

public class MealPlanPanel extends JPanel {
    public MealPlanPanel(MongoDBHelper db, String username, Runnable onPlanSelected) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Meal Plans", JLabel.CENTER);
        titleLabel.setFont(new Font(null, Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(25, 0, 5, 0));
        add(titleLabel, BorderLayout.NORTH);

        //loading user goals 
        HashMap<String, String> userGoals = db.loadUserGoals(username);
        int targetCalories = Integer.parseInt(userGoals.getOrDefault("targetCalories", "0"));
        String savedPlan = db.loadSavedMealPlan(username);

        String recommendation = findRecommendation(targetCalories);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));

        //hint for user at the top 
        if(targetCalories > 0) {
            JLabel hintLabel = new JLabel("Daily Calorie Target: " + targetCalories + " kcal - ⭐ marks plan most suited for you", JLabel.CENTER);
            hintLabel.setFont(new Font(null, Font.ITALIC, 12));
            hintLabel.setForeground(new Color(120, 100, 0));
            hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(hintLabel);
            contentPanel.add(Box.createVerticalStrut(15));

        }
        contentPanel.revalidate();
        contentPanel.repaint();

        //section 1 - weight loss
         contentPanel.add(makeSectionHeader("Weight Loss Plans", new Color(70, 130, 180)));
        contentPanel.add(Box.createVerticalStrut(10));
        for (MealPlanData.MealPlan plan : MealPlanData.getPlansByCategory("Weight Loss")) {
            boolean recommended = plan.name.equals(recommendation);
            contentPanel.add(makePlanCard(plan, savedPlan, recommended, db, username, onPlanSelected));
            contentPanel.add(Box.createVerticalStrut(10));
        }


        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(makeDivider());
        contentPanel.add(Box.createVerticalStrut(20));


        //section 2 - maintenance
        contentPanel.add(makeSectionHeader("Maintenance Plans", new Color(70, 130, 180)));
        contentPanel.add(Box.createVerticalStrut(10));
        for (MealPlanData.MealPlan plan : MealPlanData.getPlansByCategory("Maintenance")) {
            boolean recommended = plan.name.equals(recommendation);
            contentPanel.add(makePlanCard(plan, savedPlan, recommended, db, username, onPlanSelected));
            contentPanel.add(Box.createVerticalStrut(10));
        }

        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(makeDivider());
        contentPanel.add(Box.createVerticalStrut(20));


        //section 3 - weight gain
        contentPanel.add(makeSectionHeader("Weight Gain Plans", new Color(70, 130, 180)));
        contentPanel.add(Box.createVerticalStrut(10));
        for (MealPlanData.MealPlan plan : MealPlanData.getPlansByCategory("Muscle Gain")) {
            boolean recommended = plan.name.equals(recommendation);
            contentPanel.add(makePlanCard(plan, savedPlan, recommended, db, username, onPlanSelected));
            contentPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }
    private String findRecommendation(int targetCalories) {
        if (targetCalories <= 0) return null;
 
        MealPlanData.MealPlan[] all = MealPlanData.getAllPlans();
        String bestName = null;
        int    bestDiff = Integer.MAX_VALUE;
 
        for (MealPlanData.MealPlan plan : all) {
            int avgCals = getAverageDailyCalories(plan);
            int diff    = Math.abs(avgCals - targetCalories);
            if (diff < bestDiff) {
                bestDiff = diff;
                bestName = plan.name;
            }
        }
        return bestName;
    }
    private int getAverageDailyCalories(MealPlanData.MealPlan plan) {
        int total = 0;
        int days  = 0;
        for (MealPlanData.DayPlan day : plan.days) {
            int dayCals = parseCalories(day.breakfast.calories)
                        + parseCalories(day.lunch.calories)
                        + parseCalories(day.dinner.calories);
            total += dayCals;
            days++;
        }
        return days > 0 ? total / days : 0;
    }
 
    private int parseCalories(String cals) {
        try {
            return Integer.parseInt(cals.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    private JPanel makeSectionHeader(String category, Color accent) {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setBackground(Color.WHITE);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel label = new JLabel(category);
        label.setFont(new Font(null, Font.BOLD, 18));       
        label.setForeground(accent);
        header.add(label);
        return header;
    }
    private JSeparator makeDivider() {
        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(200, 200, 200));
        return sep;
    }
    private JPanel makePlanCard(MealPlanData.MealPlan plan, String savedPlan, boolean recommended, MongoDBHelper db, String username, Runnable onPlanSelected) {
        boolean isActive = plan.name.equals(savedPlan);
        Color bgColor = isActive ? new Color(235, 255, 235)
                      : recommended ? new Color(255, 250, 220)
                      : new Color (250, 250, 250);
        Color border = isActive ? new Color(60, 160, 60)
                     : recommended ? new Color(255, 252, 220)
                     : new Color(250, 250, 250);
        Color selectColor = isActive ? new Color(60, 160, 60) : new Color(70, 130, 180);

        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border, 2, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JPanel nameLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        nameLine.setBackground(bgColor);

        JLabel nameLabel = new JLabel(plan.name+ (isActive ? " ✓ Active Plan" : "")); 
        nameLabel.setFont(new Font(null, Font.BOLD, 15)); 
        nameLabel.setForeground(isActive ? new Color(40, 130, 40) : Color.BLACK); 
        nameLine.add(nameLabel);

        if(recommended) {
            JLabel star = new JLabel("⭐ Recommend for you!");
            star.setFont(new Font(null, Font.BOLD, 11));
            star.setFont(new Font(null, Font.BOLD, 11));
            star.setForeground(isActive ? new Color(40, 130, 40) : new Color(180, 120, 0));

            nameLine.add(star);
        }

        int avgCals = getAverageDailyCalories(plan);
        JLabel calLabel = new JLabel("Avg Daily Calories: " + avgCals + plan.description);
        calLabel.setFont(new Font(null, Font.PLAIN, 12));
        calLabel.setForeground(Color.DARK_GRAY);

        JPanel textPanel = new JPanel(); 
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(bgColor);
        textPanel.add(nameLine);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(calLabel);

        //buttons on each meal plan card

        JPanel btnPanel = new JPanel(); 
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBackground(bgColor);

        JButton selectBtn = new JButton(isActive ? "Selected" : "Select Plan");
        selectBtn.setBackground(selectColor); 
        selectBtn.setForeground(Color.BLACK);
        selectBtn.setFocusPainted(false);
        selectBtn.setFont(new Font(null, Font.BOLD, 12));
        selectBtn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        selectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
        selectBtn.setEnabled(!isActive);

        JButton viewBtn = new JButton("View Plan");
        viewBtn.setBackground(new Color(160, 160, 160));
        viewBtn.setForeground(Color.BLACK);
        viewBtn.setFocusPainted(false);
        viewBtn.setFont(new Font(null, Font.BOLD, 12));
        viewBtn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        viewBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        viewBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        selectBtn.addActionListener(e -> {
            db.saveMealPlanSelection(username, plan.name); 
            JOptionPane.showMessageDialog(this, plan.name + " is now currently active", "Plan is selected", JOptionPane.INFORMATION_MESSAGE); 
            Container topLevel = SwingUtilities.getAncestorOfClass(JFrame.class, this); 
             if (topLevel instanceof JFrame) {
                JFrame frame = (JFrame) topLevel;
                BorderLayout layout = (BorderLayout) frame.getContentPane().getLayout();
                Component current = layout.getLayoutComponent(BorderLayout.CENTER);
                if (current != null) frame.remove(current);
                frame.add(new MealPlanPanel(db, username, onPlanSelected), BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
        });
        viewBtn.addActionListener(e -> {
            Container topLevel = SwingUtilities.getAncestorOfClass(JFrame.class, this);
            if (topLevel instanceof JFrame) {
                JFrame frame = (JFrame) topLevel;
                BorderLayout layout = (BorderLayout) frame.getContentPane().getLayout();
                Component current = layout.getLayoutComponent(BorderLayout.CENTER);
                if (current != null) frame.remove(current);
                frame.add(new MealPlanDetailPanel(plan, db, username, onPlanSelected),
                    BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
        });
        btnPanel.add(selectBtn);
        btnPanel.add(Box.createVerticalStrut(8));
        btnPanel.add(viewBtn);
 
        card.add(textPanel, BorderLayout.CENTER);
        card.add(btnPanel,  BorderLayout.EAST);
        return card;

    }
    
}
