package com.loginapp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HomePage {
    JFrame frame = new JFrame();
    JLabel homeLabel = new JLabel("Welcome");
    JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

    JButton mealPlanBtn = new JButton("Meal Plan");
    JButton mealSwipeBtn = new JButton("Meal Swiping");
    JButton viewRecipesBtn = new JButton("View Recipes");
    JButton settingsBtn = new JButton("Settings");
    JButton logOutBtn = new JButton("Log Out");
    JButton homeBtn = new JButton("Home");

    MongoDBHelper db;
    String username;

    HomePage(String UserID, MongoDBHelper db) {
        this.db = db;
        this.username = UserID;

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLayout(new BorderLayout());

        homeLabel.setFont(new Font(null, Font.BOLD, 25));
        homeLabel.setText("Hello " + UserID);
        homeLabel.setHorizontalAlignment(JLabel.CENTER);

        navBar.add(mealPlanBtn);
        navBar.add(mealSwipeBtn);
        navBar.add(viewRecipesBtn);
        navBar.add(settingsBtn);
        navBar.add(logOutBtn);

        mealPlanBtn.addActionListener(e -> showPage(new MealPlanPanel(), false));
        mealSwipeBtn.addActionListener(e -> showPage(new MealSwipingPanel(), false));
        homeBtn.addActionListener(e -> showPage(homeLabel, true));
        settingsBtn.addActionListener(
                e -> showPage(new SettingsPanel(() -> showPage(homeLabel, true), db, username), false));
        logOutBtn.addActionListener(e -> {
            frame.dispose();
            System.out.println("Logged out successfully");
        });

        frame.add(navBar, BorderLayout.NORTH);
        frame.add(homeLabel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void showPage(Component page, boolean isHome) {
        navBar.removeAll();
        if (isHome) {
            navBar.add(mealPlanBtn);
            navBar.add(mealSwipeBtn);
            navBar.add(viewRecipesBtn);
            navBar.add(settingsBtn);
            navBar.add(logOutBtn);
        } else {
            navBar.add(homeBtn);
        }

        BorderLayout layout = (BorderLayout) frame.getContentPane().getLayout();
        Component current = layout.getLayoutComponent(BorderLayout.CENTER);
        if (current != null) {
            frame.remove(current);
        }

        frame.add(page, BorderLayout.CENTER);
        navBar.revalidate();
        navBar.repaint();
        frame.revalidate();
        frame.repaint();
    }
}