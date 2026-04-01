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

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String UserID = userIDField.getText();
            String password = String.valueOf(userPasswordField.getPassword());
            if (logininfo.containsKey(UserID)) {
                if (logininfo.get(UserID).equals(password)) {
                    messageLabel.setForeground(Color.GREEN);
                    frame.dispose();
                    messageLabel.setText("Success");
                    HomePage homepage = new HomePage(UserID);
                } else {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Invalid");
                }
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Username not found!!!!");
            }

        } else if (e.getSource() == registerButton) {
            String UserID = userIDField.getText();
            String password = String.valueOf(userPasswordField.getPassword());

            if (UserID.isEmpty() || password.isEmpty()) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Fill both fields!");
                return;
            }
            boolean success = db.saveUser(UserID, password);
            if (success) {
                logininfo.put(UserID, password);
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Registered!");
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("User exists!");
            }
        }
    }
}