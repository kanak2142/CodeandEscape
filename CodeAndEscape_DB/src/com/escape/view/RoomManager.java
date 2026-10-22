package com.escape.view;

import com.escape.model.GameState;
import com.escape.model.Controller.GameFlowController;
import javax.swing.*;
import java.awt.*;

public class RoomManager {
    private static RoomManager instance;
    private JPanel container;
    private CardLayout cardLayout;

    private RoomManager() {
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
    }

    public static RoomManager getInstance() {
        if (instance == null) {
            instance = new RoomManager();
        }
        return instance;
    }

    public JPanel getContainer() {
        return container;
    }

    public void addRoom(String name, RoomPanel room) {
        container.add(room, name);
    }

    public void switchRoom(String name) {
        GameState state = GameState.getInstance();
        if (!GameFlowController.getInstance().canMoveToRoom(name.toLowerCase(), state)) {
            javax.swing.JOptionPane.showMessageDialog(
                    container,
                    "You must solve the required puzzles before entering " + name + ".",
                    "ROOM LOCKED",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        state.setCurrentRoom(name);
        cardLayout.show(container, name);
    }
}