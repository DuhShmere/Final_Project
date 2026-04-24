package com.loginapp;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login implements ActionListener {
    JFrame frame = new JFrame();
    JButton loginButton = new JButton("Login");
    JButton registerButton = new JButton("Register");
    JTextField userIDField = new JTextField();
    JPasswordField userPasswordField = new JPasswordField();
    JLabel userIDLabel = new JLabel("User:");
    JLabel userPasswordLabel = new JLabel("Password:");
    JLabel messageLabel = new JLabel();
    HashMap<String, String> logininfo;
    MongoDBHelper db;

    Login(HashMap<String, String> OGlogingfo, MongoDBHelper db) {
        this.db = db;
        logininfo = OGlogingfo;

        userIDLabel.setBounds(50, 100, 75, 25);
        userPasswordLabel.setBounds(50, 150, 75, 25);
        messageLabel.setBounds(125, 250, 250, 35);
        messageLabel.setFont(new Font(null, Font.ITALIC, 25));

        userIDField.setBounds(125, 100, 200, 25);
        userPasswordField.setBounds(125, 150, 200, 25);

        loginButton.setBounds(125, 200, 100, 25);
        loginButton.addActionListener(this);

        registerButton.setBounds(125, 230, 100, 25);
        registerButton.addActionListener(this);

        frame.add(userIDLabel);
        frame.add(userPasswordLabel);
        frame.add(messageLabel);
        frame.add(userIDField);
        frame.add(userPasswordField);
        frame.add(loginButton);
        frame.add(registerButton);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 420);
        frame.setLayout(null);
        frame.setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
    String userID = userIDField.getText();
    String password = String.valueOf(userPasswordField.getPassword());

    if (e.getSource() == loginButton) {
        // Use your friend's new hashing verification
        if (db.verifyPassword(userID, password)) {
            messageLabel.setForeground(Color.GREEN);
            messageLabel.setText("Success");
            frame.dispose();
            new HomePage(userID, db);
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Invalid username or password!");
        }
    } 
    else if (e.getSource() == registerButton) {
        if (userID.isEmpty() || password.isEmpty()) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Fill both fields!");
            return;
        }

        if (password.length() < 6) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Min 6 characters!");
            return;
        }

        // saveUser handles the check for existing users automatically
        boolean success = db.saveUser(userID, password);
        
        if (success) {
            messageLabel.setForeground(Color.GREEN);
            messageLabel.setText("Registered!");
            frame.dispose();
            // This triggers your new Questionnaire
            new QuestionairePage(userID, db);
        } else {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("User already exists!");
        }
    }
}
}