package com.escape.view.dialog;

import javax.swing.*;
import java.awt.*;

public class ExamineDialog extends JDialog {
    public ExamineDialog(JFrame parent, String title, String description) {
        super(parent, title, true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(42, 42, 42));

        JTextArea text = new JTextArea(description);
        text.setFont(new Font("Georgia", Font.PLAIN, 16));
        text.setForeground(new Color(245, 240, 235)); // Cream
        text.setBackground(new Color(42, 42, 42));
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setMargin(new Insets(20, 20, 20, 20));

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(212, 175, 55));
        closeBtn.setForeground(Color.BLACK);
        closeBtn.setFont(new Font("Courier New", Font.BOLD, 14));
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dispose());

        add(text, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(42, 42, 42));
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);

        setSize(400, 250);
        setLocationRelativeTo(parent);
    }
}