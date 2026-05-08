package com.healthos.frontend.view;

import com.healthos.backend.database.MongoDBHelper;
import com.healthos.frontend.controller.LoginController;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
//import com.mongodb.internal.logging.LogMessage.Component;

public class Login implements ActionListener {
    private static final Color FOREST = new Color(0x1C, 0x3A, 0x2E);
    private static final Color SAGE = new Color(0x4A, 0x7C, 0x59);
    private static final Color BG = new Color(0xF3, 0xF1, 0xEB);
    private static final Color CARD = Color.WHITE;
    private static final Color MUTED = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDER = new Color(0xD1, 0xC9, 0xBA);
    private static final Color SUCCESS = new Color(0x1C, 0x66, 0x42);
    private static final Color ERROR = new Color(0x9B, 0x2B, 0x2B);

    JFrame frame = new JFrame();
    JTextField userIDField;
    JPasswordField userPasswordField;
    JButton loginButton;
    JButton registerButton;
    JLabel messageLabel;

    MongoDBHelper db;
    LoginController loginController;

    public Login(HashMap<String, String> OGlogingfo, MongoDBHelper db) {
        this.db = db;
        this.loginController = new LoginController(db);

        frame.setTitle("HealthOS");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(380, 420);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
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
        card.setPreferredSize(new Dimension(300, 340));

        JLabel title = new JLabel("HealthOS");
        title.setFont(new Font("Serif", Font.BOLD, 20));
        title.setForeground(FOREST);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to continue");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        userIDField = makeField("Username");
        userPasswordField = makePasswordField("Password");

        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        loginButton = makePrimaryButton("Login");
        registerButton = makeGhostButton("Register");
        loginButton.addActionListener(this);
        registerButton.addActionListener(this);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(20));
        card.add(fieldLabel("User"));
        card.add(Box.createVerticalStrut(5));
        card.add(userIDField);
        card.add(Box.createVerticalStrut(12));
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(5));
        card.add(userPasswordField);
        card.add(Box.createVerticalStrut(8));
        card.add(messageLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(8));
        card.add(registerButton);

        return card;

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

    private JPasswordField makePasswordField(String placeholder) {
        JPasswordField f = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
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
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));

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

    private JButton makeGhostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setForeground(FOREST);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(BORDER, 8, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(SAGE, 8, 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)));
                b.setForeground(SAGE);
            }

            public void mouseExited(MouseEvent e) {
                b.setBorder(BorderFactory.createCompoundBorder(
                        new RoundBorder(BORDER, 8, 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)));
                b.setForeground(FOREST);
            }
        });
        return b;
    }

    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius, thickness;

        RoundBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        String userID = userIDField.getText();
        String password = String.valueOf(userPasswordField.getPassword());

        if (e.getSource() == loginButton) {
            LoginController.LoginResult result = loginController.login(userID, password);
            if (result == LoginController.LoginResult.SUCCESS) {
                messageLabel.setForeground(Color.GREEN);
                messageLabel.setText("Success");
                frame.dispose();
                new HomePage(userID, db);
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid username or password!");
            }
        } else if (e.getSource() == registerButton) {
            LoginController.RegisterResult result = loginController.register(userID, password);
            switch (result) {
                case EMPTY_FIELDS:
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Fill both fields!");
                    break;
                case PASSWORD_TOO_SHORT:
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Min 6 characters!");
                    break;
                case SUCCESS:
                    messageLabel.setForeground(Color.GREEN);
                    messageLabel.setText("Registered!");
                    frame.dispose();
                    new QuestionairePage(userID, db);
                    break;
                case USER_EXISTS:
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("User already exists!");
                    break;
            }
        }
    }
}