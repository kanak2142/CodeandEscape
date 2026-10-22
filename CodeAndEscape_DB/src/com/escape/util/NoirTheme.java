package com.escape.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class NoirTheme {
    // Color Palette
    public static final Color BG_DARK = new Color(18, 18, 18);
    public static final Color BG_PANEL = new Color(28, 28, 28);
    public static final Color GOLD = new Color(212, 175, 55);
    public static final Color GOLD_HOVER = new Color(240, 200, 80);
    public static final Color CREAM = new Color(245, 240, 235);
    public static final Color MUTED_GREY = new Color(110, 110, 110);
    public static final Color ERROR_RED = new Color(180, 50, 50);

    // Fonts
    public static final Font TITLE_FONT = new Font("Georgia", Font.BOLD, 22);
    public static final Font BODY_FONT = new Font("Georgia", Font.PLAIN, 15);
    public static final Font CODE_FONT = new Font("Courier New", Font.PLAIN, 15);
    public static final Font HEADER_FONT = new Font("Courier New", Font.BOLD, 16);

    // Custom Gold Border
    public static Border createGoldBorder(String title) {
        Border line = BorderFactory.createLineBorder(GOLD, 1);
        Border margin = BorderFactory.createEmptyBorder(6, 10, 6, 10);
        if (title == null || title.isEmpty()) {
            return BorderFactory.createCompoundBorder(line, margin);
        }
        return BorderFactory.createTitledBorder(
                line, title, 0, 0, HEADER_FONT, GOLD
        );
    }

    // Custom Button Factory
    public static JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(HEADER_FONT);
        btn.setForeground(BG_DARK);
        btn.setBackground(GOLD);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(GOLD_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(GOLD);
            }
        });
        return btn;
    }
}