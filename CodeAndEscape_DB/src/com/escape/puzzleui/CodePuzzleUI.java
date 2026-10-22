package com.escape.puzzleui;

import com.escape.model.GameState;
import com.escape.util.NoirTheme;
import com.escape.util.SoundManager;

import javax.swing.*;
import java.awt.*;

public class CodePuzzleUI extends JDialog {

    public enum PuzzleType {
        OUTPUT("EVALUATE OUTPUT"),
        COMPLETE_CODE("FILL IN THE BLANK (___)"),
        BUG_FIX("LOCATE BUG / FIX"),
        LOGIC_CHECK("BOOLEAN / CONDITIONAL");

        public final String header;
        PuzzleType(String header) { this.header = header; }
    }

    public CodePuzzleUI(JFrame parent, String puzzleId, String title, PuzzleType type, String prompt, String expectedAnswer, Runnable onSuccess) {
        super(parent, title, true);

        // Check if already solved
        if (GameState.getInstance().isPuzzleSolved(puzzleId)) {
            JOptionPane.showMessageDialog(parent, "This terminal is already decrypted.", "SOLVED", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(NoirTheme.BG_DARK);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(NoirTheme.BG_PANEL);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel("CASE FILE: " + title.toUpperCase());
        titleLabel.setFont(NoirTheme.TITLE_FONT);
        titleLabel.setForeground(NoirTheme.GOLD);

        JLabel typeBadge = new JLabel("[" + type.header + "]");
        typeBadge.setFont(NoirTheme.HEADER_FONT);
        typeBadge.setForeground(NoirTheme.CREAM);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(typeBadge, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Code Area
        JTextArea codeArea = new JTextArea(prompt);
        codeArea.setFont(NoirTheme.CODE_FONT);
        codeArea.setForeground(NoirTheme.CREAM);
        codeArea.setBackground(NoirTheme.BG_PANEL);
        codeArea.setEditable(false);
        codeArea.setBorder(NoirTheme.createGoldBorder("JAVA LOGIC ENCRYPTION"));
        codeArea.setMargin(new Insets(10, 10, 10, 10));
        add(codeArea, BorderLayout.CENTER);

        // Bottom Controls
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(NoirTheme.BG_DARK);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputRow.setBackground(NoirTheme.BG_DARK);

        JLabel inputPrompt = new JLabel(">> INPUT:");
        inputPrompt.setFont(NoirTheme.HEADER_FONT);
        inputPrompt.setForeground(NoirTheme.GOLD);

        JTextField inputField = new JTextField(15);
        inputField.setFont(NoirTheme.CODE_FONT);
        inputField.setBackground(NoirTheme.BG_PANEL);
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(NoirTheme.GOLD);
        inputField.setBorder(BorderFactory.createLineBorder(NoirTheme.GOLD, 1));
        inputField.setPreferredSize(new Dimension(180, 32));

        JButton executeBtn = NoirTheme.createStyledButton("EXECUTE");

        executeBtn.addActionListener(e -> {
            String sanitizedInput = inputField.getText().trim().replaceAll("\\s+", "");
            String sanitizedExpected = expectedAnswer.trim().replaceAll("\\s+", "");

            if (sanitizedInput.equalsIgnoreCase(sanitizedExpected)) {
                SoundManager.playSound("/assets/sounds/success.wav");
                if (!GameState.getInstance().solvePuzzle(puzzleId, inputField.getText().trim())) {
                    JOptionPane.showMessageDialog(this, "Backend validation rejected this answer.", "BACKEND VALIDATION", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(this, "Decompilation successful. Cipher solved.", "ACCESS GRANTED", JOptionPane.INFORMATION_MESSAGE);
                if (onSuccess != null) onSuccess.run();
                dispose();
            } else {
                SoundManager.playSound("/assets/sounds/click.wav");
                // 30-second penalty for wrong answer
                int curTime = GameState.getInstance().getTimeRemaining();
                GameState.getInstance().setTimeRemaining(Math.max(0, curTime - 30));

                JOptionPane.showMessageDialog(this,
                        "Compilation Error: Incorrect solution.\n[SECURITY PENALTY]: -30 Seconds Remaining.",
                        "ACCESS DENIED",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        inputRow.add(inputPrompt);
        inputRow.add(inputField);
        inputRow.add(executeBtn);

        bottomPanel.add(inputRow, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(620, 400);
        setLocationRelativeTo(parent);
    }
}