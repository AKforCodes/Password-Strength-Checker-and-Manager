// PasswordStrengthCheckerFrame.java

import javax.swing.*;
import java.awt.*;

public class PasswordStrengthCheckerFrame extends JFrame {
    private PasswordCheckerPanel passwordCheckerPanel;
    private PasswordManagerPanel passwordManagerPanel;
    private JLabel statusBar;

    private String username;
    private String password; // User's password used for encryption/decryption

    public PasswordStrengthCheckerFrame(String username, String password) {
        this.username = username;
        this.password = password;

        setTitle("Password Strength Checker and Manager");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        setLayout(new BorderLayout());

        // Initialize panels
        passwordCheckerPanel = new PasswordCheckerPanel();
        passwordManagerPanel = new PasswordManagerPanel(password); // Pass user's password

        // Create Menu Bar
        createMenuBar();

        // Create Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Password Checker", passwordCheckerPanel);
        tabbedPane.addTab("Password Manager", passwordManagerPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Status Bar
        statusBar = new JLabel("Welcome, " + username + "!");
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        statusBar.setBackground(new Color(50, 50, 50)); // Dark grey
        statusBar.setForeground(Color.WHITE);
        statusBar.setOpaque(true);
        add(statusBar, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exportItem = new JMenuItem("Export Passwords");
        JMenuItem importItem = new JMenuItem("Import Passwords");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(exportItem);
        fileMenu.add(importItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Edit Menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem preferencesItem = new JMenuItem("Preferences");
        editMenu.add(preferencesItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem documentationItem = new JMenuItem("Documentation");

        helpMenu.add(aboutItem);
        helpMenu.add(documentationItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        // Set the menu bar
        setJMenuBar(menuBar);

        // Add action listeners
        exitItem.addActionListener(e -> System.exit(0));

        preferencesItem.addActionListener(e -> showPreferencesDialog());

        aboutItem.addActionListener(e -> showAboutDialog());

        documentationItem.addActionListener(e -> showDocumentation());

        exportItem.addActionListener(e -> passwordManagerPanel.exportPasswords());

        importItem.addActionListener(e -> passwordManagerPanel.importPasswords());
    }

    private void showPreferencesDialog() {
        PreferencesDialog preferencesDialog = new PreferencesDialog(this);
        preferencesDialog.setVisible(true);
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Password Strength Checker and Manager\nVersion 1.0\nDeveloped by AKforCodes",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDocumentation() {
        JOptionPane.showMessageDialog(this,
                "For documentation, please refer to the user manual or visit our website.",
                "Documentation",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void updateStatusBar(String message) {
        statusBar.setText(message);
    }
}
