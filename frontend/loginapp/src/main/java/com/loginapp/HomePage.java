package com.loginapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HomePage {

    private static final Color FOREST = new Color (0x1C, 0x3A, 0x2E);
    private static final Color SAGE = new Color (0x4A, 0x7C, 0x59);
    private static final Color MUTED = new Color (0x6B, 0x72, 0x80);
    private static final Color BORDER = new Color (0xD1, 0xC9, 0xBA);
    private static final Color Cream = new Color (0xFD,0xFA,0xF5);

    JFrame frame = new JFrame();
    JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6,10));
    JLabel homeLabel = new JLabel("Hello");

    JButton mealPlanBtn = navButton("Meal Plan");
    JButton mealSwipeBtn = navButton("Meal Swiping");
    JButton viewRecipesBtn = navButton("View Recipes");
    JButton settingsBtn = navButton("Settings");
    JButton logOutBtn =navButton("Log Out");
    JButton homeBtn = navButton("Home");

    MongoDBHelper db;
    String username;

    HomePage(String UserID, MongoDBHelper db) {
        this.db = db;
        this.username = UserID;

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(820, 560);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Cream);

        
        homeLabel.setText("Hello " + UserID);
        homeLabel.setFont(new Font ("Serif",Font.BOLD,28));
        homeLabel.setForeground(new Color (0x1A,0x1A,0x1A));
        homeLabel.setHorizontalAlignment(JLabel.CENTER);
        
        navBar.setBackground(FOREST);
        navBar.setPreferredSize(new Dimension(0,52));
        navBar.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        navBar.add(mealPlanBtn);
        navBar.add(mealSwipeBtn);
        navBar.add(viewRecipesBtn);
        navBar.add(settingsBtn);
        navBar.add(logOutBtn);

        mealPlanBtn.addActionListener(e -> showPage(new MealPlanPanel(db, username, () -> {}), false));
        homeBtn.addActionListener(e -> showPage(homeLabel, true));
        mealSwipeBtn.addActionListener(e -> showPage(new MealSwipingPanel(db, username), false));
        viewRecipesBtn.addActionListener(e -> showPage(new ViewRecipesPanel(db, username), false));
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

    private static JButton navButton (String text){
        JButton b = new JButton (text){
            @Override
            protected void paintComponent (Graphics g){
                if(getModel().isRollover()){
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color (255,255,255,30));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif",Font.PLAIN,12));
        b.setForeground(new Color (255,255,255,200));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseEntered(java.awt.event.MouseEvent e){
                b.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e){
                b.setForeground(new Color (255,255,255,200));
            }
        });
        return b;
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