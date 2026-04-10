package com.loginapp;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class MealSwipingPanel extends JPanel{
    
    private int currentINDEX=0;
    private boolean showingFront = true;

    private JPanel cardPanel = new JPanel();
    private JLabel cardLabel = new JLabel ("", JLabel.CENTER);
    
    private JButton LikeButton = new JButton("👍 Like"); 
    private JButton dislikeButton = new JButton("👎 Dislike");
    private JButton backButton = new JButton ("Prev.");
    private JButton nextButton = new JButton("Next");
    
    MealSwipingPanel(){

       setLayout(new BorderLayout(10, 10));
       setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        //Title 
       JLabel label = new JLabel ("Meal Swiping", JLabel.CENTER);
       label.setFont(new Font(null, Font.BOLD,25));
       label.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));
       add(label,BorderLayout.NORTH);
        //card 
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(new Color(255,248,220));
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(200,180,120)));
        cardPanel.setPreferredSize(new Dimension(300,200));

        cardLabel.setFont(new Font(null, Font.BOLD, 20));
        cardLabel.setHorizontalAlignment(JLabel.CENTER);
        cardLabel.setVerticalAlignment(JLabel.CENTER);
        cardPanel.add(cardLabel, BorderLayout.CENTER);
        
        JLabel flip = new JLabel("Click card to Flip", JLabel.CENTER);
        flip.setFont(new Font(null, Font.ITALIC,11));
        flip.setForeground(Color.DARK_GRAY);
        cardPanel.add(flip, BorderLayout.SOUTH);

        updateCard();

        cardPanel.addMouseListener(new MouseAdapter(){
            public void mouseClicked (MouseEvent e){
                flipCard();
            }
        });
        cardPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(cardPanel, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15,10));

        styleButton(backButton, new Color(180,180,180));
        styleButton(nextButton, new Color(180,180,180));
        styleButton(LikeButton, new Color(180,180,180));
        styleButton(dislikeButton, new Color(180,180,180));

        backButton.addActionListener(e->System.err.println("Back Clicked"));
        nextButton.addActionListener(e->System.err.println("Next Clicked"));
        LikeButton.addActionListener(e->System.err.println("Liked"));
        dislikeButton.addActionListener(e->System.err.println("Dislike"));

        bottomPanel.add(dislikeButton);
        bottomPanel.add(LikeButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }
    private void flipCard(){
        showingFront = !showingFront;
        updateCard();
    }

    private void updateCard(){
        if (showingFront){
            cardLabel.setText("");
            cardPanel.setBackground(new Color(255,248,220));
        }
        else{
            cardLabel.setText("");
            cardPanel.setBackground(new Color(220,240,255));
        }
        cardPanel.revalidate();
        cardPanel.repaint();
    }
    private void styleButton(JButton button, Color color){
button.setBackground(color);
button.setForeground(Color.black);
button.setFocusPainted(false);
button.setFont(new Font (null, Font.BOLD,14));
button.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
    }
}