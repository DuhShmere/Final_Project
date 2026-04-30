package com.loginapp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;

public class QuestionairePage implements ActionListener {

    // ── Palette (matches Login + HomePage exactly) ──────────────────────
    private static final Color FOREST = new Color(0x1C, 0x3A, 0x2E);
    private static final Color SAGE   = new Color(0x4A, 0x7C, 0x59);
    private static final Color BG     = new Color(0xF3, 0xF1, 0xEB);
    private static final Color CARD   = Color.WHITE;
    private static final Color MUTED  = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDER = new Color(0xD1, 0xC9, 0xBA);

    JFrame frame = new JFrame();
    JButton submitBtn;

    JComboBox<String> goalDropdown;
    JComboBox<String> activityDropdown;
    JComboBox<String> sexDropdown;

    JTextField ageField;
    JTextField weightField;
    JTextField heightFtField;
    JTextField heightInField;

    JLabel errorLabel;

    String userID;
    MongoDBHelper db;

    public QuestionairePage(String userID, MongoDBHelper db) {
        this.userID = userID;
        this.db = db;

        frame.setTitle("HealthOS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.getContentPane().setBackground(BG);
        frame.setLayout(new GridBagLayout());

        frame.add(buildCard());
        frame.setVisible(true);
    }

    private JPanel buildCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        card.setPreferredSize(new Dimension(360, 540));

        // Title
        JLabel title = new JLabel("Complete Your Profile");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(FOREST);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Answer the questions below to finish your profile");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // question 1
        String[] goals = {"Lose Weight", "Maintain Weight", "Gain Weight", "Build Muscle"};
        goalDropdown = styledCombo(goals);

        //question 2
        String[] activityLevels = {"Sedentary", "Lightly Active", "Moderately Active", "Very Active"};
        activityDropdown = styledCombo(activityLevels);

        // question 3
        String[] sexes = {"Male", "Female"};
        sexDropdown = styledCombo(sexes);
        // question 4-6
        ageField      = makeField("Enter your age");
        weightField   = makeField("Enter your weight in pounds");
        heightFtField = makeField("ft");
        heightInField = makeField("in");

        // Height row — ft and in side by side
        JPanel heightRow = new JPanel(new GridLayout(1, 2, 8, 0));
        heightRow.setOpaque(false);
        heightRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        heightRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        heightRow.add(heightFtField);
        heightRow.add(heightInField);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        errorLabel.setForeground(new Color(0x9B, 0x2B, 0x2B));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        submitBtn = makePrimaryButton("Submit");
        submitBtn.addActionListener(this);


        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(20));

        card.add(fieldLabel("What is your fitness goal"));
        card.add(Box.createVerticalStrut(5));
        card.add(goalDropdown);
        card.add(Box.createVerticalStrut(12));

        card.add(fieldLabel("What is your activity level"));
        card.add(Box.createVerticalStrut(5));
        card.add(activityDropdown);
        card.add(Box.createVerticalStrut(12));

        card.add(fieldLabel("What is your sex"));
        card.add(Box.createVerticalStrut(5));
        card.add(sexDropdown);
        card.add(Box.createVerticalStrut(12));

        card.add(fieldLabel("What is your age"));
        card.add(Box.createVerticalStrut(5));
        card.add(ageField);
        card.add(Box.createVerticalStrut(12));

        card.add(fieldLabel("What is your weight (lbs)"));
        card.add(Box.createVerticalStrut(5));
        card.add(weightField);
        card.add(Box.createVerticalStrut(12));

        card.add(fieldLabel("What is your height (ft / in)"));
        card.add(Box.createVerticalStrut(5));
        card.add(heightRow);
        card.add(Box.createVerticalStrut(8));


        card.add(errorLabel);
        card.add(Box.createVerticalStrut(8));


        card.add(submitBtn);

        return card;
    }


    private int[] calculateTDEE(String sex, int age, double weight,
                                 int heightFt, int heightIn,
                                 String activity, String goal) {
        double weightKg = weight * 0.453592;
        double heightCm = (heightFt * 30.48) + (heightIn * 2.54);

        double bmr;
        if ("Male".equals(sex)) {
            bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5;
        } else {
            bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161;
        }

        double activityMultiplier;
        if (activity.equals("Lightly Active"))        activityMultiplier = 1.375;
        else if (activity.equals("Moderately Active")) activityMultiplier = 1.55;
        else if (activity.equals("Very Active"))       activityMultiplier = 1.725;
        else                                           activityMultiplier = 1.2;

        double tdee = bmr * activityMultiplier;

        double targetCalories;
        if (goal.equals("Lose Weight"))       targetCalories = tdee - 500;
        else if (goal.equals("Gain Weight"))  targetCalories = tdee + 500;
        else if (goal.equals("Build Muscle")) targetCalories = tdee + 250;
        else                                  targetCalories = tdee;

        double protienCals, fatCals, carbCals;
        if (goal.equals("Build Muscle")) {
            protienCals = targetCalories * 0.35;
            fatCals     = targetCalories * 0.25;
            carbCals    = targetCalories * 0.40;
        } else if (goal.equals("Lose Weight")) {
            protienCals = targetCalories * 0.40;
            fatCals     = targetCalories * 0.35;
            carbCals    = targetCalories * 0.25;
        } else {
            protienCals = targetCalories * 0.30;
            fatCals     = targetCalories * 0.30;
            carbCals    = targetCalories * 0.40;
        }

        int protienGrams = (int) (protienCals / 4);
        int fatGrams     = (int) (fatCals     / 9);
        int carbGrams    = (int) (carbCals    / 4);

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
            try {
                int    age      = Integer.parseInt(ageField.getText().trim());
                double weight   = Double.parseDouble(weightField.getText().trim());
                int    heightFt = Integer.parseInt(heightFtField.getText().trim());
                int    heightIn = Integer.parseInt(heightInField.getText().trim());
                String sex      = (String) sexDropdown.getSelectedItem();
                String goal     = (String) goalDropdown.getSelectedItem();
                String activity = (String) activityDropdown.getSelectedItem();

                int[] results        = calculateTDEE(sex, age, weight, heightFt, heightIn, activity, goal);
                int targetCalories   = results[0];
                int protienGrams     = results[1];
                int fatGrams         = results[2];
                int carbGrams        = results[3];
                int tdee             = results[4];

                db.saveUserGoals(userID, goal, activity, age, weight,
                                 heightFt, heightIn, sex,
                                 tdee, targetCalories, protienGrams, carbGrams, fatGrams);

                frame.dispose();
                new HomePage(userID, db);

            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter valid numbers for age, weight, and height.");
            }
        }
    }


    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JTextField makeField(String placeholder) {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(new Color(160, 160, 160));
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    g2.drawString(placeholder, 10, getHeight() / 2 + 4);
                    g2.dispose();
                }
            }
        };
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.setBackground(BG);
        f.setForeground(new Color(0x1A, 0x1A, 0x1A));
        f.setCaretColor(FOREST);
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER, 8, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new RoundBorder(SAGE, 8, 2),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)));
                f.repaint();
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                    new RoundBorder(BORDER, 8, 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)));
                f.repaint();
            }
        });
    }

    private <T> JComboBox<T> styledCombo(T[] items) {
        JComboBox<T> c = new JComboBox<>(items);
        c.setFont(new Font("SansSerif", Font.PLAIN, 13));
        c.setBackground(BG);
        c.setForeground(new Color(0x1A, 0x1A, 0x1A));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER, 8, 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        return c;
    }

    private JButton makePrimaryButton(String text) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? SAGE : FOREST);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setForeground(Color.WHITE);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        return b;
    }


    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius, thickness;

        RoundBorder(Color color, int radius, int thickness) {
            this.color     = color;
            this.radius    = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }
    }
}