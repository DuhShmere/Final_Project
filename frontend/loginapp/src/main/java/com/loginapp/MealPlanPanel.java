package com.loginapp;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MealPlanPanel extends JPanel {
    public MealPlanPanel() {
        this.setBackground(Color.WHITE);
        JLabel title = new JLabel("Meal Plan Options");
        title.setFont(new Font(null, Font.BOLD, 20));
        JButton testBtn = new JButton("Plan A: ");
        this.add(title);
        this.add(testBtn);
    }
}
