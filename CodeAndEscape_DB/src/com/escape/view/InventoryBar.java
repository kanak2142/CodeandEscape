package com.escape.view;

import com.escape.model.GameState;
import com.escape.model.Item;
import com.escape.util.NoirTheme;
import com.escape.view.dialog.ExamineDialog;
import com.escape.view.dialog.ToastNotification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;

public class InventoryBar extends JPanel {
    private static final int TOTAL_SLOTS = 6;
    private final JFrame parentFrame;

    public InventoryBar(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        setLayout(new FlowLayout(FlowLayout.CENTER, 14, 10));
        setBackground(new Color(14, 14, 14));
        setPreferredSize(new Dimension(1024, 85));
        setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, NoirTheme.GOLD));

        // Listen for inventory changes and auto-redraw
        GameState.getInstance().getInventory().addChangeListener(this::refreshSlots);
        refreshSlots();
    }

    public void refreshSlots() {
        removeAll();
        List<Item> items = GameState.getInstance().getInventory().getAllItems();
        Item selected = GameState.getInstance().getInventory().getSelectedItem();

        for (int i = 0; i < TOTAL_SLOTS; i++) {
            JPanel slot = new JPanel(new BorderLayout());
            slot.setPreferredSize(new Dimension(64, 64));
            slot.setBackground(new Color(24, 24, 24));

            if (i < items.size()) {
                Item item = items.get(i);
                boolean isSelected = (selected != null && selected.getId().equals(item.getId()));

                // Slot Border: Bright gold if active/selected, dark gold otherwise
                slot.setBorder(BorderFactory.createLineBorder(
                        isSelected ? NoirTheme.GOLD_HOVER : new Color(212, 175, 55, 120),
                        isSelected ? 2 : 1
                ));

                // Render Icon or Styled Fallback Label
                JLabel contentLabel;
                URL imgURL = getClass().getResource(item.getIconPath());
                if (imgURL != null) {
                    ImageIcon icon = new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH));
                    contentLabel = new JLabel(icon, SwingConstants.CENTER);
                } else {
                    contentLabel = new JLabel("<html><center><font color='#d4af37'><b>" + item.getName() + "</b></font></center></html>", SwingConstants.CENTER);
                    contentLabel.setFont(new Font("Courier New", Font.PLAIN, 10));
                }

                contentLabel.setToolTipText("<html><b>" + item.getName() + "</b><br><i>Left-click: Select | Right-click: Inspect</i></html>");

                // Interaction Handling
                contentLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e) || e.getClickCount() == 2) {
                            // Examine detailed clue
                            new ExamineDialog(parentFrame, item.getName(), item.getDescription() + "\n\n[DETECTIVE NOTES]:\n" + item.getDetailedClue()).setVisible(true);
                        } else if (SwingUtilities.isLeftMouseButton(e)) {
                            // Combine with current item or select
                            Item currentSelected = GameState.getInstance().getInventory().getSelectedItem();
                            if (currentSelected != null && currentSelected != item) {
                                boolean combined = GameState.getInstance().getInventory().combineItems(currentSelected, item);
                                if (combined) {
                                    new ToastNotification(parentFrame, "Crafted new item!");
                                    return;
                                }
                            }
                            GameState.getInstance().getInventory().setSelectedItem(item);
                            if (GameState.getInstance().getInventory().getSelectedItem() != null) {
                                new ToastNotification(parentFrame, "Equipped: " + item.getName());
                            }
                        }
                    }
                });

                slot.add(contentLabel, BorderLayout.CENTER);
            } else {
                // Empty Slot
                slot.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
            }

            add(slot);
        }

        revalidate();
        repaint();
    }
}