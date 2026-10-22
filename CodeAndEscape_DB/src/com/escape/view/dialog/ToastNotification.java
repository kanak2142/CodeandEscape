package com.escape.view.dialog;

import javax.swing.*;
import java.awt.*;

public class ToastNotification extends JWindow {
    public ToastNotification(JFrame parent, String message) {
        super(parent);
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setForeground(new Color(212, 175, 55)); // Gold
        label.setFont(new Font("Courier New", Font.BOLD, 14));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(26, 26, 26)); // Dark
        panel.setBorder(BorderFactory.createLineBorder(new Color(212, 175, 55), 2));
        panel.add(label, BorderLayout.CENTER);

        add(panel);
        setSize(300, 50);
        setLocationRelativeTo(parent);

        // Position at the bottom center of the parent window
        int x = parent.getX() + (parent.getWidth() - getWidth()) / 2;
        int y = parent.getY() + parent.getHeight() - 100;
        setLocation(x, y);

        Timer timer = new Timer(2000, e -> dispose());
        timer.setRepeats(false);
        timer.start();
        setVisible(true);
    }
}