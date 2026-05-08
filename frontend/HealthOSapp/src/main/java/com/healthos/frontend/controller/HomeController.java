package com.healthos.frontend.controller;

import javax.swing.JFrame;

public class HomeController {

    public void logout(JFrame frame) {
        frame.dispose();
        System.out.println("Logged out successfully");
    }
}
