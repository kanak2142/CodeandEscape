package com.escape.view;

import com.escape.model.PlayerProfile;
import com.escape.model.Service.GameDatabase;
import com.escape.util.NoirTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

/** Login/registration screen shown before the escape-room game starts. */
public class LoginScreen extends JDialog {
    private final JTextField nameField = new JTextField();
    private final JTextField discordField = new JTextField();
    private final JLabel playerIdLabel = new JLabel("GENERATED AFTER IDENTIFICATION");
    private PlayerProfile profile;
    private Image backgroundImage;

    public LoginScreen() {
        setTitle("Code & Escape - Case Identification");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setSize(820, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        URL bg = getClass().getResource("/assets/images/office_bg.jpg");
        if (bg != null) backgroundImage = new ImageIcon(bg).getImage();

        JPanel background = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                if (backgroundImage != null) {
                    g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                    g2.setColor(new Color(8, 8, 8, 185));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g2.setColor(NoirTheme.BG_DARK);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
            }
        };

        JPanel dossier = new JPanel(new BorderLayout(0, 14));
        dossier.setOpaque(true);
        dossier.setBackground(new Color(20, 20, 20, 245));
        dossier.setBorder(BorderFactory.createCompoundBorder(
                NoirTheme.createGoldBorder("CASE FILE // PLAYER IDENTIFICATION"),
                new EmptyBorder(24, 34, 24, 34)
        ));
        dossier.setPreferredSize(new Dimension(650, 470));

        JPanel heading = new JPanel(new BorderLayout(0, 4));
        heading.setOpaque(false);
        JLabel title = new JLabel("CODE & ESCAPE", SwingConstants.CENTER);
        title.setFont(new Font("Georgia", Font.BOLD, 34));
        title.setForeground(NoirTheme.GOLD);
        JLabel subtitle = new JLabel("THE NOIR CODING ESCAPE ROOM", SwingConstants.CENTER);
        subtitle.setFont(new Font("Courier New", Font.BOLD, 13));
        subtitle.setForeground(NoirTheme.CREAM);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        dossier.add(heading, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        JLabel instruction = new JLabel("IDENTIFY YOURSELF BEFORE ENTERING THE CASE");
        instruction.setFont(new Font("Courier New", Font.BOLD, 12));
        instruction.setForeground(NoirTheme.MUTED_GREY);
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(instruction);
        form.add(Box.createVerticalStrut(18));
        form.add(createField("DETECTIVE NAME", nameField));
        form.add(Box.createVerticalStrut(13));
        form.add(createField("DISCORD ID", discordField));
        form.add(Box.createVerticalStrut(15));

        JPanel idPanel = new JPanel(new BorderLayout(10, 0));
        idPanel.setOpaque(false);
        JLabel idTitle = new JLabel("PLAYER ID");
        idTitle.setFont(NoirTheme.HEADER_FONT);
        idTitle.setForeground(NoirTheme.GOLD);
        playerIdLabel.setFont(new Font("Courier New", Font.BOLD, 13));
        playerIdLabel.setForeground(NoirTheme.CREAM);
        idPanel.add(idTitle, BorderLayout.WEST);
        idPanel.add(playerIdLabel, BorderLayout.CENTER);
        form.add(idPanel);

        JLabel databaseNote = new JLabel("IDENTITY IS STORED IN THE CASE DATABASE VIA JDBC / MYSQL", SwingConstants.CENTER);
        databaseNote.setFont(new Font("Courier New", Font.PLAIN, 10));
        databaseNote.setForeground(new Color(145, 145, 145));
        databaseNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(Box.createVerticalStrut(12));
        form.add(databaseNote);
        dossier.add(form, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JLabel warning = new JLabel("⚠  Your unique Player ID identifies this game profile", SwingConstants.CENTER);
        warning.setFont(new Font("Georgia", Font.ITALIC, 11));
        warning.setForeground(NoirTheme.MUTED_GREY);
        footer.add(warning, BorderLayout.NORTH);
        JButton start = NoirTheme.createStyledButton("ENTER THE CASE");
        start.setFont(new Font("Courier New", Font.BOLD, 16));
        start.setPreferredSize(new Dimension(220, 46));
        start.addActionListener(e -> login());
        JPanel buttonWrap = new JPanel();
        buttonWrap.setOpaque(false);
        buttonWrap.add(start);
        footer.add(buttonWrap, BorderLayout.SOUTH);
        dossier.add(footer, BorderLayout.SOUTH);

        background.add(dossier);
        setContentPane(background);
    }

    private JPanel createField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(8, 6));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(NoirTheme.HEADER_FONT);
        label.setForeground(NoirTheme.GOLD);
        field.setFont(new Font("Courier New", Font.PLAIN, 16));
        field.setBackground(new Color(238, 233, 225));
        field.setForeground(NoirTheme.BG_DARK);
        field.setCaretColor(NoirTheme.BG_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 175, 55, 120), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void login() {
        String name = nameField.getText().trim();
        String discord = discordField.getText().trim();
        if (name.isEmpty() || discord.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both your Name and Discord ID.", "Missing Details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (name.length() > 100 || discord.length() > 100) {
            JOptionPane.showMessageDialog(this, "Name and Discord ID must be 100 characters or less.", "Invalid Details", JOptionPane.WARNING_MESSAGE);
            return;
        }
        GameDatabase db = GameDatabase.getInstance();
        if (!db.isDatabaseAvailable()) {
            JOptionPane.showMessageDialog(this, "MySQL is not available. Start MySQL Server and check db.properties before continuing.", "Database Connection Required", JOptionPane.ERROR_MESSAGE);
            return;
        }
        profile = db.registerOrFindPlayer(name, discord);
        if (profile == null) {
            JOptionPane.showMessageDialog(this, "Could not save your player profile to MySQL. Check the database connection.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        playerIdLabel.setText(profile.getPlayerId());
        JOptionPane.showMessageDialog(this,
                "Welcome, " + profile.getName() + "!\n\nYour unique Player ID is:\n" + profile.getPlayerId(),
                "Identity Confirmed", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    public PlayerProfile getProfile() { return profile; }

    public static PlayerProfile showLogin() {
        LoginScreen screen = new LoginScreen();
        screen.setVisible(true);
        return screen.getProfile();
    }
}
