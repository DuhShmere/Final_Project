package com.loginapp;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font; 
import java.awt.Graphics;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;   


public class MealPlanDetailPanel extends JPanel {
    

    private static final Color BG_BREAKFAST_COLOR = new Color(255, 248, 220);
    private static final Color BG_LUNCH_COLOR = new Color(220, 245, 255);
    private static final Color BG_DINNER_COLOR = new Color(240, 220, 255);

 public MealPlanDetailPanel(MealPlanData.MealPlan plan, MongoDBHelper db,
                                String username, Runnable onPlanSelected)  {
      setLayout(new BorderLayout());
      setBackground(Color.WHITE);

      //header section

      JPanel headerPanel = new JPanel(); 
      headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
      headerPanel.setBackground(Color.WHITE);
      headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

      //back btn
      JButton backBtn = new JButton("← Back to Plans");
      backBtn.setFont(new Font(null, Font.BOLD, 12)); 
      backBtn.setBackground(new Color(220, 220, 220)); 
      backBtn.setForeground(Color.DARK_GRAY);
      backBtn.setFocusPainted(false);
      backBtn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
      backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      backBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
      backBtn.addActionListener(e -> {
             Container topLevel = SwingUtilities.getAncestorOfClass(JFrame.class, this);
            
                if(topLevel instanceof JFrame) {
                    JFrame frame = (JFrame) topLevel; 
                    BorderLayout layout = (BorderLayout) frame.getContentPane().getLayout(); 
                    
                    Component current = layout.getLayoutComponent(BorderLayout.CENTER);
                    if (current != null) frame.remove(current);
                    frame.add(new MealPlanPanel(db, username, onPlanSelected), BorderLayout.CENTER);
                    frame.revalidate();
                    frame.repaint();
                    
                }
      });
      


      JLabel titleLabel = new JLabel(plan.name, JLabel.CENTER);
      titleLabel.setFont(new Font(null, Font.BOLD, 20));
      titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

      JLabel descLabel = new JLabel(plan.description, JLabel.CENTER);
      descLabel.setFont(new Font(null, Font.ITALIC, 14));
      descLabel.setForeground(Color.GRAY); 
      descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

      JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 4)); 
      legendPanel.setBackground(Color.WHITE); 
      legendPanel.add(makeLegenedDot(BG_BREAKFAST_COLOR, "Breakfast")); 
      legendPanel.add(makeLegenedDot(BG_LUNCH_COLOR, "Lunch"));
      legendPanel.add(makeLegenedDot(BG_DINNER_COLOR, "Dinner"));

      headerPanel.add(backBtn);
      headerPanel.add(Box.createVerticalStrut(10));
      headerPanel.add(titleLabel); 
      headerPanel.add(Box.createVerticalStrut(4)); 
      headerPanel.add(descLabel); 
      headerPanel.add(Box.createVerticalStrut(6)); 
      headerPanel.add(legendPanel); 

      add(headerPanel, BorderLayout.NORTH); 

      //adding a scroll 
      JPanel daysPanel = new JPanel(); 
      daysPanel.setLayout(new BoxLayout(daysPanel, BoxLayout.Y_AXIS)); 
      daysPanel.setBackground(Color.WHITE); 
      daysPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20)); 
      
      for (MealPlanData.DayPlan day : plan.days) { 
        daysPanel.add(makeDaySection(day, db, username)); 
        daysPanel.add(Box.createVerticalStrut(12)); 
      }
      JScrollPane scrollPane = new JScrollPane(daysPanel);
      scrollPane.setBorder(null);
      scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
      add(scrollPane, BorderLayout.CENTER);
    }
    private JPanel makeLegenedDot(Color color, String label) {
        JPanel dot = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0)); 
        dot.setBackground(Color.WHITE); 
        JPanel circle = new JPanel() {
          @Override  
          protected void paintComponent(Graphics g) {
            super.paintComponent(g); 
            g.setColor(color.darker()); 
            g.fillOval(0, 2, 12, 12); 
          }

        };
        circle.setPreferredSize(new Dimension(12, 16)); 
        circle.setBackground(Color.WHITE); 
        JLabel lbl = new JLabel(label); 
        lbl.setFont(new Font(null, Font.PLAIN, 11)); 
        dot.add(circle); 
        dot.add(lbl);
        return dot;
    }

    private JPanel makeDaySection(MealPlanData.DayPlan day, MongoDBHelper db, String username) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS)); 
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 220, 220, 220), 1, true), BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel dayLabel = new JLabel("Day " + day.dayNumber); 
        dayLabel.setFont(new Font(null, Font.BOLD, 15));
        dayLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0)); 
        section.add(dayLabel);

        section.add(makeMealRow(day.breakfast, BG_BREAKFAST_COLOR, "Breakfast", db, username)); 
        section.add(Box.createVerticalStrut(6)); 
        section.add(makeMealRow(day.lunch, BG_LUNCH_COLOR, "Lunch", db, username)); 
        section.add(Box.createVerticalStrut(6)); 
        section.add(makeMealRow(day.dinner, BG_DINNER_COLOR, "Dinner", db, username)); 

        return section;
    }

    private JPanel makeMealRow(MealPlanData.Recipe recipe, Color bg, String mealType, MongoDBHelper db, String username) {
        JPanel row = new JPanel(new BorderLayout(10, 0)); 
        row.setBackground(bg);
        row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(bg.darker(), 1, true), BorderFactory.createEmptyBorder(8, 12, 8, 12))); 
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54)); 
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 

        JPanel textPanel = new JPanel(); 
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(bg);

        JLabel typeLabel = new JLabel(mealType.toUpperCase()); 
        typeLabel.setFont(new Font(null, Font.BOLD, 11));
        typeLabel.setForeground(Color.DARK_GRAY);

        JLabel nameLabel = new JLabel(recipe.name); 
        nameLabel.setFont(new Font(null, Font.BOLD, 13)); 

        textPanel.add(typeLabel);
        textPanel.add(nameLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); 
        rightPanel.setBackground(bg);

        JLabel calLabel = new JLabel(recipe.calories + " kcal"); 
        calLabel.setFont(new Font(null, Font.PLAIN, 12)); 
        calLabel.setForeground(Color.DARK_GRAY);

        JLabel arrowLabel = new JLabel(",");
        arrowLabel.setFont(new Font(null, Font.BOLD, 20)); 
        arrowLabel.setForeground(Color.GRAY); 

        rightPanel.add(calLabel);
        rightPanel.add(arrowLabel);

        row.add(textPanel, BorderLayout.CENTER);
        row.add(rightPanel, BorderLayout.EAST);

        row.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showRecipeDetail(recipe, mealType, db, username);
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                row.setBackground(bg.darker()); 
                textPanel.setBackground(bg.darker());
                rightPanel.setBackground(bg.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                row.setBackground(bg);
                textPanel.setBackground(bg); 
                rightPanel.setBackground(bg); 
            }

        });
        return row; 
    }
    private void showRecipeDetail(MealPlanData.Recipe recipe, String mealType, MongoDBHelper db, String username) {
        List<String> deselected = db.loadDeselectedIngredients(username);
        JDialog dialog = new JDialog( 
            (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this), recipe.name, true); 
        dialog.setSize(440, 500);
        dialog.setLocationRelativeTo(this); 
        dialog.setLayout(new BorderLayout()); 

        JPanel contentPanel = new JPanel(new CardLayout()); 
        contentPanel.setPreferredSize(new Dimension(420, 480)); 

        JPanel frontPanel = new JPanel(new BorderLayout(5, 5)); 
        frontPanel.setBackground(new Color(255, 248, 220)); 
        
        JLabel emojiLabel = new JLabel("🍽", JLabel.CENTER); 
        emojiLabel.setFont(new Font(null, Font.PLAIN, 80));
        emojiLabel.setPreferredSize(new Dimension(400, 200)); 

        JLabel nameLabel = new JLabel(recipe.name, JLabel.CENTER); 
        nameLabel.setFont(new Font(null, Font.BOLD, 17)); 
        nameLabel.setBorder(BorderFactory.createEmptyBorder(12, 10, 4, 10)); 

        JLabel typeLabel = new JLabel(mealType + " . " + recipe.category, JLabel.CENTER); 
        typeLabel.setFont(new Font(null, Font.ITALIC, 13)); 
        typeLabel.setForeground(Color.GRAY);

        JPanel infoPanel = new JPanel(); 
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS)); 
        infoPanel.setBackground(new Color(255, 248, 220)); 
        infoPanel.setBorder(new EmptyBorder(0, 10, 12, 10)); 
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(nameLabel);
        infoPanel.add(typeLabel);

        frontPanel.add(emojiLabel, BorderLayout.CENTER);
        frontPanel.add(infoPanel, BorderLayout.SOUTH);

        JPanel backPanel = new JPanel(new BorderLayout(5, 5)); 
        backPanel.setBackground(new Color(220, 240, 255)); 
        backPanel.setBorder(new EmptyBorder(10, 15, 10, 15)); 

        JLabel statsTitle = new JLabel("Nutrition Facts", JLabel.CENTER); 
        statsTitle.setFont(new Font(null, Font.BOLD, 16)); 
        backPanel.add(statsTitle, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(new Color(220, 240, 255)); 

        statsPanel.add(makeStatRow("Calories", recipe.calories + " kcal")); 
        statsPanel.add(makeStatRow("Protein", recipe.protein + " g"));
        statsPanel.add(makeStatRow("Carbs", recipe.carbs + " g"));
        statsPanel.add(makeStatRow("Fat", recipe.fat + " g"));

        JLabel ingrTitle = new JLabel("Ingredients"); 
        ingrTitle.setFont(new Font(null, Font.BOLD, 12));
        ingrTitle.setFont(new Font(null, Font.BOLD, 12)); 
        statsPanel.add(ingrTitle); 
        statsPanel.add(Box.createVerticalStrut(3));

        JTextArea ingrArea = new JTextArea(recipe.ingredients);
        ingrArea.setEditable(false); 
        ingrArea.setLineWrap(true);
        ingrArea.setWrapStyleWord(true);
        ingrArea.setBackground(new Color(220, 240, 255));
        ingrArea.setFont(new Font(null, Font.PLAIN, 11));
        ingrArea.setAlignmentX(Component.LEFT_ALIGNMENT); 
        statsPanel.add(ingrArea);
        statsPanel.add(Box.createVerticalStrut(8));

        JLabel instrTitle = new JLabel("Instructions");
        instrTitle.setFont(new Font(null, Font.BOLD, 12));
        instrTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(instrTitle);
        statsPanel.add(Box.createVerticalStrut(3));

        JTextArea instrArea = new JTextArea(recipe.instructions);
        instrArea.setEditable(false);
        instrArea.setLineWrap(true);
        instrArea.setWrapStyleWord(true);
        instrArea.setBackground(new Color(220, 240, 255));
        instrArea.setFont(new Font(null, Font.PLAIN, 11));
        instrArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(instrArea);

        JScrollPane statsScroll = new JScrollPane(statsPanel);
        statsScroll.setBorder(null);
        statsScroll.setBackground(new Color(220, 240, 255));
        backPanel.add(statsScroll, BorderLayout.CENTER);
        contentPanel.add(frontPanel, "front");
        contentPanel.add(backPanel, "back");

        final boolean[] showingFront = {true};
        java.awt.event.MouseAdapter flipListener = new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showingFront[0] = !showingFront[0];
                CardLayout cl = (CardLayout) (contentPanel.getLayout());
                cl.show(contentPanel, showingFront[0] ? "front" : "back");
            }
        };
        frontPanel.addMouseListener(flipListener);
        backPanel.addMouseListener(flipListener);
        emojiLabel.addMouseListener(flipListener);
        infoPanel.addMouseListener(flipListener);

        JPanel bottomPanel = new JPanel(new BorderLayout()); 
        bottomPanel.setBorder(new EmptyBorder(6, 10, 10, 10)); 

        JLabel flipHint = new JLabel("Click on card to flip for nutrition info", JLabel.CENTER);
        flipHint.setFont(new Font(null, Font.ITALIC, 11));
        flipHint.setForeground(Color.DARK_GRAY);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4)); 

        JButton likeBtn = new JButton("👍 Like");
        likeBtn.setBackground(new Color(100, 200, 100)); 
        likeBtn.setForeground(Color.BLACK); 
        likeBtn.setFocusPainted(false); 
        likeBtn.setFont(new Font(null, Font.BOLD, 13)); 
        likeBtn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16)); 

        JButton closeBtn = new JButton("Close"); 
        closeBtn.setBackground(new Color(180, 180, 180));
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setFocusPainted(false);
        closeBtn.setFont(new Font(null, Font.BOLD, 13));
        closeBtn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));

        JLabel statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font(null, Font.BOLD, 12));

        likeBtn.addActionListener(e -> {
    db.saveLikedMeal(username, recipe.name, recipe.category, recipe.calories,
                     recipe.fat, recipe.protein, recipe.carbs, recipe.ingredients, "");
    statusLabel.setForeground(new Color(0, 150, 0));
    statusLabel.setText("Liked: " + recipe.name + " saved!");
});

        closeBtn.addActionListener(e -> dialog.dispose());

        btnRow.add(likeBtn); 
        btnRow.add(closeBtn); 

        bottomPanel.add(flipHint, BorderLayout.NORTH);
        bottomPanel.add(btnRow, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);

        
    }
     private JPanel makeStatRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        row.setBackground(new Color(220, 240, 255));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font(null, Font.BOLD, 12));
        JLabel val = new JLabel(value);
        val.setFont(new Font(null, Font.PLAIN, 12));
        row.add(lbl);
        row.add(val);
        return row;
    }
}

    

