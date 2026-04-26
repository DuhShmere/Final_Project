package com.loginapp;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class QuestionairePage implements ActionListener {
    JFrame frame = new JFrame();
    JButton submitBtn = new JButton();
    JComboBox<String> goalDropdown;
    JComboBox<String> activityDropdown; 
    JComboBox<String> sexDropdown; 

    JTextField ageField = new JTextField();
    JTextField weightField = new JTextField();
    JTextField heightFtField = new JTextField();
    JTextField heightInField = new JTextField();

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

        //thrid question 
        JLabel sexLabel = new JLabel("What is your sex");
        sexLabel.setBounds(50, 230, 300, 25);
        String[] sexes = {"Male", "Female"};
        sexDropdown = new JComboBox<>(sexes);
        sexDropdown.setBounds(200, 230, 150, 25);

        //fourth question 
        JLabel ageLabel = new JLabel("What is your age");
        ageLabel.setBounds(50, 280, 300, 25);
        ageField.setBounds(200, 280, 150, 25);
        ageField.setToolTipText("Enter your age");

        //fifth question
        JLabel weightLabel = new JLabel("What is your weight (lbs)");
        weightLabel.setBounds(50, 330, 300, 25);
        weightField.setBounds(200, 330, 150, 25);
        weightField.setToolTipText("Enter your weight in pounds");

        //sixth question
        JLabel heightLabel = new JLabel("What is your height (ft/in)");
        heightLabel.setBounds(50, 380, 300, 25);
        heightFtField.setBounds(200, 380, 50, 25);
        heightFtField.setToolTipText("Enter your height in feet");
        heightInField.setBounds(260, 380, 50, 25);
        heightInField.setToolTipText("Enter your height in inches");


        frame.add(welcomeLabel);
        frame.add(goalLabel);
        frame.add(goalDropdown);
        frame.add(activityLabel);
        frame.add(activityDropdown);
        frame.add(submitBtn);
        frame.add(sexLabel);
        frame.add(sexDropdown);
        frame.add(ageLabel);
        frame.add(ageField);
        frame.add(weightLabel);
        frame.add(weightField);
        frame.add(heightLabel);
        frame.add(heightFtField);
        frame.add(heightInField);


        submitBtn.setText("Submit");
        submitBtn.setBounds(50, 250, 100, 25);
        submitBtn.addActionListener(e -> actionPerformed(e));
        frame.setVisible(true);

        
    }
    private int[] calculateTDEE(String sex, int age, double weight, int heightFt, int heightIn, String activity, String goal) {

        double weightKg = weight * 0.453592;
        double heightCm = (heightFt * 30.48) + (heightIn * 2.54);

        double bmr; 
        if("Male".equals(sex)) {
            bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5;
        } else {
            bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161;
            } 
        double activityMultiplier;
        if(activity.equals("Lightly Active")) {
            activityMultiplier = 1.375;
        } else if(activity.equals("Moderately Active")) {
            activityMultiplier = 1.55;
        } else if(activity.equals("Very Active")) {
            activityMultiplier = 1.725;
        } else {
            activityMultiplier = 1.2;
        }
        double tdee = bmr * activityMultiplier;

        double targetCalories;
        if(goal.equals("Lose Weight")) {
            targetCalories = tdee - 500;
        } else if(goal.equals("Gain Weight")) {
            targetCalories = tdee + 500;
        } else if(goal.equals("Build Muscle")) {
            targetCalories = tdee + 250;
        } else {
            targetCalories = tdee;
        }
        double protienCals, fatCals, carbCals; 
        if(goal.equals("Build Muscle")) {
            protienCals = targetCalories * 0.35;
            fatCals = targetCalories * 0.25;
            carbCals = targetCalories * 0.40;
        } else if (goal.equals("Lose Weight")) {
            protienCals = targetCalories * 0.40;
            fatCals = targetCalories * 0.35;
            carbCals = targetCalories * 0.25;
        } else {
            protienCals = targetCalories * 0.30;
            fatCals = targetCalories * 0.30;
            carbCals = targetCalories * 0.40;
        }
        int protienGrams = (int) (protienCals / 4);
        int fatGrams = (int) (fatCals / 9);
        int carbGrams = (int) (carbCals / 4);

        return new int[]{
            (int) Math.round(targetCalories),
            protienGrams,
            fatGrams,
            carbGrams,
            (int) Math.round(tdee)
        };
    }
        
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitBtn) {
           try{
           int age = Integer.parseInt(ageField.getText().trim());
           double weight = Double.parseDouble(weightField.getText().trim());
           int heightFt = Integer.parseInt(heightFtField.getText().trim());
           int heightIn = Integer.parseInt(heightInField.getText().trim());
           String sex = (String) sexDropdown.getSelectedItem();
           String goal = (String) goalDropdown.getSelectedItem();
           String activity = (String) activityDropdown.getSelectedItem();

           int[] results = calculateTDEE(sex, age, weight, heightFt, heightIn, activity, goal);
           int targetCalories = results[0];
           int protienGrams = results[1];
           int fatGrams = results[2];
           int carbGrams = results[3];
           int tdee = results[4];
           
           db.saveUserGoals(userID, goal, activity, age, weight, heightFt, heightIn, sex, tdee, targetCalories, protienGrams, carbGrams, fatGrams);

           frame.dispose();
           new HomePage(userID, db);
           } catch(NumberFormatException ex){
            JLabel errorLabel = new JLabel("Please enter valid numbers for age, weight, and height.");
            errorLabel.setBounds(50, 430, 300, 25);
            errorLabel.setForeground(java.awt.Color.RED);
            frame.add(errorLabel);
            frame.repaint();
           }

        }
    }

    
}
