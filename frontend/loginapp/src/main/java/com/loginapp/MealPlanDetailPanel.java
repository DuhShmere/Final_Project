package com.loginapp;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MealPlanDetailPanel extends JPanel {
 public MealPlanDetailPanel(MealPlanData.MealPlan plan, MongoDBHelper db,
                                String username, Runnable onPlanSelected)  {
        this.setBackground(Color.WHITE);
        JLabel title = new JLabel("Meal Plan Options");
        title.setFont(new Font(null, Font.BOLD, 20));
        JButton testBtn = new JButton("Plan A: ");
        this.add(title);
        this.add(testBtn);
    }
    }

    

