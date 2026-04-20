package com.loginapp;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class QuestionairePage implements ActionListener {
    JFrame frame = new JFrame();
    JButton submitBtn = new JButton();
    JComboBox<String> goalDropdown;
    JComboBox<String> activityDropdown; 

    String userID;
    MongoDBHelper db;

    public QuestionairePage(String userID, MongoDBHelper db) {
        this.userID = userID;
        this.db = db;

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 500);
        frame.setLayout(null);

        JLabel welcomeLabel = new JLabel("In order to finish your profile please answer the questions below");
        welcomeLabel.setBounds(50, 20, 300, 35);
        welcomeLabel.setFont(new Font(null, Font.BOLD, 18));

        //first question
        JLabel goalLabel = new JLabel("What is your fitness goal");
        goalLabel.setBounds(50, 70, 300, 25);
        String[] goals = {"Lose Weight", "Maintain Weight", "Gain Weight", "Build Muscle"};
        goalDropdown = new JComboBox<>(goals);
        goalDropdown.setBounds(50, 100, 150, 25);

        //second question
        JLabel activityLabel = new JLabel("What is your activity level");
        activityLabel.setBounds(50, 150, 300, 25);
        String[] activityLevels = {"Sedentary", "Lightly Active", "Moderately Active", "Very Active"};
        activityDropdown = new JComboBox<>(activityLevels);
        activityDropdown.setBounds(50, 180, 150, 25);

        frame.add(welcomeLabel);
        frame.add(goalLabel);
        frame.add(goalDropdown);
        frame.add(activityLabel);
        frame.add(activityDropdown);
        frame.add(submitBtn);

        submitBtn.setText("Submit");
        submitBtn.setBounds(50, 250, 100, 25);
        submitBtn.addActionListener(e -> actionPerformed(e));
        frame.setVisible(true);

        
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitBtn) {
           String goal = (String) goalDropdown.getSelectedItem();
           String activity = (String) activityDropdown.getSelectedItem();
           
           db.saveUserGoals(userID, goal, activity);

           frame.dispose();
           new HomePage(userID, db);
            

        }
    }

    
}
