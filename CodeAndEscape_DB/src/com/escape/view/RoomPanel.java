package com.escape.view;

import com.escape.util.NoirTheme;
import com.escape.util.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RoomPanel extends JPanel {
    private String roomName;
    private List<Hotspot> hotspots = new ArrayList<>();
    private Point mousePoint = new Point(-1, -1);
    private Image backgroundImage;
    private float pulseAlpha = 0.3f;
    private boolean pulseGrowing = true;

    public RoomPanel(String roomName, String imagePath) {
        this.roomName = roomName;

        try {
            URL imageURL = getClass().getResource(imagePath);
            if (imageURL != null) {
                backgroundImage = new ImageIcon(imageURL).getImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Hotspot h : hotspots) {
                    if (h.bounds.contains(e.getPoint())) {
                        SoundManager.playSound("/assets/sounds/click.wav");
                        if (h.onClick != null) h.onClick.run();
                        break;
                    }
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
                repaint();
            }
        });

        // Background hotspot pulse animation timer (30 FPS)
        Timer pulseTimer = new Timer(33, e -> {
            if (pulseGrowing) {
                pulseAlpha += 0.015f;
                if (pulseAlpha >= 0.6f) pulseGrowing = false;
            } else {
                pulseAlpha -= 0.015f;
                if (pulseAlpha <= 0.2f) pulseGrowing = true;
            }
            repaint();
        });
        pulseTimer.start();
    }

    public void addHotspot(Hotspot h) {
        hotspots.add(h);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Draw Background Image or Fallback Gradient
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            GradientPaint gp = new GradientPaint(0, 0, NoirTheme.BG_PANEL, 0, getHeight(), NoirTheme.BG_DARK);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // 2. Draw Room Name Card (Top-Left)
        g2.setColor(new Color(18, 18, 18, 200));
        g2.fillRoundRect(20, 15, 220, 45, 8, 8);
        g2.setColor(NoirTheme.GOLD);
        g2.drawRoundRect(20, 15, 220, 45, 8, 8);
        g2.setFont(NoirTheme.TITLE_FONT);
        g2.drawString(roomName.toUpperCase(), 35, 46);

        // 3. Draw Hotspots
        for (Hotspot h : hotspots) {
            boolean isHovered = h.bounds.contains(mousePoint);

            if (isHovered) {
                // Bright gold highlight on hover
                g2.setColor(new Color(212, 175, 55, 160));
                g2.fillOval(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);
                g2.setColor(NoirTheme.GOLD_HOVER);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);

                // Draw tool-tip label above hotspot
                g2.setFont(NoirTheme.HEADER_FONT);
                g2.setColor(new Color(18, 18, 18, 230));
                g2.fillRoundRect(h.bounds.x - 10, h.bounds.y - 30, h.id.length() * 11 + 16, 24, 6, 6);
                g2.setColor(NoirTheme.GOLD);
                g2.drawString(h.id, h.bounds.x - 2, h.bounds.y - 13);
            } else {
                // Subtle breathing pulse effect
                int alpha = Math.min(255, Math.max(0, (int) (pulseAlpha * 255)));
                g2.setColor(new Color(212, 175, 55, alpha));
                g2.fillOval(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);
                g2.setColor(new Color(212, 175, 55, 100));
                g2.drawOval(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);
            }
        }
    }
}