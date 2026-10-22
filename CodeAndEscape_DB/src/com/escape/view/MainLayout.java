package com.escape.view;

import com.escape.model.GameState;
import com.escape.model.Item;
import com.escape.puzzleui.CodePuzzleUI;
import com.escape.util.NoirTheme;
import com.escape.view.dialog.ExamineDialog;
import com.escape.view.dialog.ToastNotification;

import javax.swing.*;
import java.awt.*;

public class MainLayout extends JFrame {

    private JLabel timerLabel;

    public MainLayout() {
        setTitle("Code and Escape");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(1024, 768));
        getContentPane().setBackground(Color.BLACK);

        // --- Top Timer Panel ---
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.BLACK);
        timerLabel = new JLabel("TIME REMAINING - 10:00");
        timerLabel.setForeground(NoirTheme.GOLD);
        timerLabel.setFont(new Font("Courier New", Font.BOLD, 24));
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Room Container ---
        RoomManager rm = RoomManager.getInstance();
        add(rm.getContainer(), BorderLayout.CENTER);

        // --- Bottom Interactive Inventory Bar ---
        InventoryBar inventoryBar = new InventoryBar(this);
        add(inventoryBar, BorderLayout.SOUTH);

        setupRooms();
        pack();
        setLocationRelativeTo(null);

        startTimer();
    }

    private void setupRooms() {
        RoomManager rm = RoomManager.getInstance();

        // ==========================================
        // 1. OFFICE ROOM (3 Puzzles)
        // ==========================================
        RoomPanel office = new RoomPanel("Office", "/assets/images/office_bg.jpg");

        // Puzzle 1: Output calculation
        office.addHotspot(new Hotspot("Desk Terminal", 370, 420, 60, 60, Hotspot.Action.INTERACT, null, () -> {
            new CodePuzzleUI(this, "puzzle_desk", "Desk Terminal",
                    CodePuzzleUI.PuzzleType.OUTPUT,
                    "int sum = 0;\nfor (int i = 1; i <= 10; i++) {\n    sum += i;\n}\nSystem.out.println(sum);",
                    "55",
                    () -> new ToastNotification(this, "Terminal 1/6 Solved!")
            ).setVisible(true);
        }));

        // Puzzle 2: Bug Fix
        office.addHotspot(new Hotspot("Bookshelf Safe", 820, 270, 70, 80, Hotspot.Action.INTERACT, null, () -> {
            new CodePuzzleUI(this, "puzzle_bookshelf", "Encrypted Index",
                    CodePuzzleUI.PuzzleType.BUG_FIX,
                    "String[] files = {\"Clue1\", \"Clue2\", \"Clue3\"};\n// Throws ArrayIndexOutOfBoundsException:\nString target = files[___];\n// What index retrieves 'Clue3' safely?",
                    "2",
                    () -> new ToastNotification(this, "Cipher 2/6 Solved!")
            ).setVisible(true);
        }));

        // Puzzle 3: Code Completion
        office.addHotspot(new Hotspot("Painting", 620, 220, 60, 50, Hotspot.Action.INTERACT, null, () -> {
            new CodePuzzleUI(this, "puzzle_painting", "Ciphered Canvas",
                    CodePuzzleUI.PuzzleType.COMPLETE_CODE,
                    "String a = \"Hello \";\nString b = \"World\";\n// Complete method to join strings without '+':\nString result = a.___(b);\n// (Provide method name)",
                    "concat",
                    () -> new ToastNotification(this, "Painting 3/6 Solved!")
            ).setVisible(true);
        }));

        office.addHotspot(new Hotspot("Plant", 170, 520, 50, 50, Hotspot.Action.EXAMINE, null, () -> {
            new ExamineDialog(this, "Plant Pot", "A dying ficus. The soil is dry, but no clues are hidden here.").setVisible(true);
        }));

        office.addHotspot(new Hotspot("Waste Bin", 880, 490, 40, 40, Hotspot.Action.PICKUP, "Letter", () -> {
            GameState.getInstance().getInventory().addItem(new Item(
                    "letter", "Crumbled Letter", "A discarded note.",
                    "/assets/images/letter_icon.png",
                    "Clue: String methods like concat() join values together."
            ));
            new ToastNotification(this, "Picked up: Crumbled Letter");
        }));

        office.addHotspot(new Hotspot("Door to Vault", 900, 370, 60, 100, Hotspot.Action.ROOM_SWITCH, "Vault", () -> {
            rm.switchRoom("Vault");
            new ToastNotification(this, "Entering the Vault...");
        }));

        // ==========================================
        // 2. VAULT ROOM (2 Puzzles)
        // ==========================================
        RoomPanel vault = new RoomPanel("Vault", "/assets/images/vault_bg.jpg");

        // Puzzle 4: Operator Precedence
        vault.addHotspot(new Hotspot("Vault Door Lock", 560, 360, 80, 90, Hotspot.Action.INTERACT, null, () -> {
            new CodePuzzleUI(this, "puzzle_vault_door", "Vault Mechanism",
                    CodePuzzleUI.PuzzleType.OUTPUT,
                    "int a = 5;\nint b = 3;\nint result = a * b + 2;\nSystem.out.println(result);",
                    "17",
                    () -> new ToastNotification(this, "Vault Mechanism 4/6 Solved!")
            ).setVisible(true);
        }));

        // Puzzle 5: Complete parsing method
        vault.addHotspot(new Hotspot("Radio Terminal", 270, 320, 45, 50, Hotspot.Action.INTERACT, null, () -> {
            new CodePuzzleUI(this, "puzzle_radio", "Frequency Decoder",
                    CodePuzzleUI.PuzzleType.COMPLETE_CODE,
                    "String binary = \"1010\";\n// Convert binary string to decimal:\nint val = Integer.___(binary, 2);\n// What method converts string to primitive int?",
                    "parseInt",
                    () -> new ToastNotification(this, "Radio Signal 5/6 Decoded!")
            ).setVisible(true);
        }));

        vault.addHotspot(new Hotspot("Table", 780, 420, 60, 40, Hotspot.Action.EXAMINE, null, () -> {
            new ExamineDialog(this, "Sturdy Table", "Scratch marks form standard Java operator precedence tables.").setVisible(true);
        }));

        vault.addHotspot(new Hotspot("Wall Safe", 120, 270, 40, 50, Hotspot.Action.PICKUP, "Journal", () -> {
            GameState.getInstance().getInventory().addItem(new Item(
                    "journal", "Old Journal", "A leather-bound journal.",
                    "/assets/images/journal_icon.png",
                    "Integer.parseInt(str, radix) converts strings in custom bases (like base 2)."
            ));
            new ToastNotification(this, "Picked up: Old Journal");
        }));

        vault.addHotspot(new Hotspot("Candle", 680, 180, 30, 40, Hotspot.Action.PICKUP, "Candle", () -> {
            GameState.getInstance().getInventory().addItem(new Item(
                    "candle", "Wax Candle", "An unlit wax candle.",
                    "/assets/images/candle_icon.png",
                    "Combine with lighter to create a light source."
            ));
            new ToastNotification(this, "Picked up: Wax Candle");
        }));

        vault.addHotspot(new Hotspot("Stairs to Rooftop", 100, 500, 60, 90, Hotspot.Action.ROOM_SWITCH, "Rooftop", () -> {
            rm.switchRoom("Rooftop");
            new ToastNotification(this, "Climbing to the Rooftop...");
        }));

        // ==========================================
        // 3. ROOFTOP ROOM (Final Escape Gate)
        // ==========================================
        RoomPanel rooftop = new RoomPanel("Rooftop", "/assets/images/rooftop_bg.jpg");

        // Final Puzzle 6: Gated Escape Door
        rooftop.addHotspot(new Hotspot("Master Exit Door", 570, 370, 70, 80, Hotspot.Action.INTERACT, null, () -> {
            int solved = GameState.getInstance().getSolvedPuzzleCount();

            // STRICT ESCAPE REQUIREMENT: Must have solved the 5 earlier puzzles first
            if (solved < 5) {
                JOptionPane.showMessageDialog(this,
                        "THE DOOR IS LOCKED DEAD!\n\nSecurity locks remaining: " + (5 - solved) + "\nYou must decrypt all terminals across the Office and Vault before attempting the escape latch.",
                        "SECURITY INTERLOCK ACTIVE",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Final Master Puzzle
            new CodePuzzleUI(this, "puzzle_master", "Master Escape Latch",
                    CodePuzzleUI.PuzzleType.LOGIC_CHECK,
                    "// FINAL SECURITY OVERRIDE:\nboolean isPrime(int n) {\n    if (n <= 1) return false;\n    for (int i = 2; i * i <= n; i++) {\n        if (n % i == 0) return false;\n    }\n    return true;\n}\n// Output of isPrime(17)?",
                    "true",
                    () -> {
                        JOptionPane.showMessageDialog(this,
                                "ALL LOCKS DECRYPTED.\n\nDetective, you successfully escaped with " +
                                        (GameState.getInstance().getTimeRemaining() / 60) + " minutes to spare!",
                                "VICTORY",
                                JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }
            ).setVisible(true);
        }));

        rooftop.addHotspot(new Hotspot("Rope", 520, 350, 30, 40, Hotspot.Action.EXAMINE, null, () -> {
            new ExamineDialog(this, "Rope", "Too frayed to hold your weight. The master door is the only exit.").setVisible(true);
        }));

        rooftop.addHotspot(new Hotspot("Crate", 170, 470, 50, 40, Hotspot.Action.PICKUP, "Lighter", () -> {
            GameState.getInstance().getInventory().addItem(new Item(
                    "lighter", "Zippo Lighter", "A vintage lighter.",
                    "/assets/images/lighter_icon.png",
                    "Can light candles."
            ));
            new ToastNotification(this, "Picked up: Zippo Lighter");
        }));

        rooftop.addHotspot(new Hotspot("Edge", 270, 570, 50, 30, Hotspot.Action.EXAMINE, null, () -> {
            new ExamineDialog(this, "Building Edge", "Fog rolls through the city alleys below.").setVisible(true);
        }));

        rooftop.addHotspot(new Hotspot("Antenna", 930, 180, 35, 35, Hotspot.Action.EXAMINE, null, () -> {
            new ExamineDialog(this, "Antenna", "Broadcasting encrypted radio chatter.").setVisible(true);
        }));

        rm.addRoom("Office", office);
        rm.addRoom("Vault", vault);
        rm.addRoom("Rooftop", rooftop);

        rm.switchRoom("Office");
    }

    private void startTimer() {
        Timer timer = new Timer(1000, e -> {
            int time = GameState.getInstance().getTimeRemaining();

            if (time > 0) {
                time--;
                GameState.getInstance().setTimeRemaining(time);

                int minutes = time / 60;
                int seconds = time % 60;
                timerLabel.setText(String.format("TIME REMAINING - %02d:%02d", minutes, seconds));
            } else {
                ((Timer)e.getSource()).stop();
                JOptionPane.showMessageDialog(this, "TIME EXPIRED! The detectives caught you.", "CASE CLOSED", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        });
        timer.start();
    }
}