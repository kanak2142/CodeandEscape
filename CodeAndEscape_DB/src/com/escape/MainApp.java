package com.escape;

import com.escape.view.MainLayout;
import com.escape.view.LoginScreen;
import com.escape.util.SoundManager;
import com.escape.model.GameState;
import com.escape.model.PlayerProfile;
import com.escape.model.Service.GameDatabase;
import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel. Defaulting to standard Java theme.");
        }

        SwingUtilities.invokeLater(() -> {
            // The player must identify themselves before the game session begins.
            PlayerProfile player = LoginScreen.showLogin();
            if (player == null) {
                System.out.println("Login cancelled. Game not started.");
                System.exit(0);
                return;
            }

            GameDatabase database = GameDatabase.getInstance();
            database.startSession(GameState.getInstance(), player);

            MainLayout mainLayout = new MainLayout();

            Runtime.getRuntime().addShutdownHook(new Thread(() ->
                    database.endSession(
                            GameState.getInstance(),
                            GameState.getInstance().isGameWon() ? "WON" :
                                    GameState.getInstance().isGameOver() ? "LOST" : "CLOSED"
                    )
            ));

            SoundManager.playBackgroundMusic("/assets/sounds/jazz_bg.wav");

            mainLayout.setVisible(true);
            System.out.println("Detective Noir Escape Room initialized successfully.");
            System.out.println("Player: " + player.getName() + " | Discord: " + player.getDiscordId());
            System.out.println("Unique Player ID: " + player.getPlayerId());
        });
    }
}
