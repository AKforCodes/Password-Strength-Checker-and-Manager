// PasswordCheckerPanel.java

import javax.swing.*;
import java.awt.*;
import java.security.SecureRandom;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

public class PasswordCheckerPanel extends JPanel {
    private JPasswordField passwordField;
    private JLabel strengthLabel;
    private JProgressBar strengthProgressBar;
    private JTextArea suggestionsArea;
    private JTextField suggestedPasswordField;

    public PasswordCheckerPanel() {
        setBackground(new Color(50, 50, 50)); // Dark grey
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        initializeComponents(c);
    }

    private void initializeComponents(GridBagConstraints c) {
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        Color textColor = Color.WHITE;
        Color fieldBackground = new Color(70, 70, 70); // Slightly lighter grey

        // Password Label
        JLabel passwordLabel = new JLabel("Enter Password:");
        passwordLabel.setFont(font);
        passwordLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(20, 20, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        add(passwordLabel, c);

        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(font);
        passwordField.setBackground(fieldBackground);
        passwordField.setForeground(textColor);
        passwordField.setCaretColor(textColor);
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        passwordField.setToolTipText("Enter the password you want to check");
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(20, 5, 5, 20);
        c.anchor = GridBagConstraints.WEST;
        add(passwordField, c);

        // Check Strength Button
        JButton checkButton = new JButton("Check Strength");
        styleButton(checkButton); // Styled button
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 20, 20);
        c.anchor = GridBagConstraints.EAST;
        add(checkButton, c);

        // Strength Label
        strengthLabel = new JLabel("Strength: ");
        strengthLabel.setFont(font);
        strengthLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(5, 20, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        add(strengthLabel, c);

        // Strength Progress Bar
        strengthProgressBar = new JProgressBar(0, 100);
        strengthProgressBar.setValue(0);
        strengthProgressBar.setStringPainted(true);
        strengthProgressBar.setFont(font);
        strengthProgressBar.setForeground(Color.GREEN);
        strengthProgressBar.setBackground(fieldBackground);
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(5, 5, 5, 20);
        add(strengthProgressBar, c);

        // Suggestions Area
        JLabel suggestionsLabel = new JLabel("Suggestions:");
        suggestionsLabel.setFont(font);
        suggestionsLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(20, 20, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        add(suggestionsLabel, c);

        suggestionsArea = new JTextArea(5, 30);
        suggestionsArea.setEditable(false);
        suggestionsArea.setLineWrap(true);
        suggestionsArea.setWrapStyleWord(true);
        suggestionsArea.setFont(font);
        suggestionsArea.setBackground(fieldBackground);
        suggestionsArea.setForeground(textColor);
        suggestionsArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(suggestionsArea);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.insets = new Insets(5, 20, 20, 20);
        c.fill = GridBagConstraints.BOTH;
        add(scrollPane, c);

        // Generate Stronger Password Button
        JButton generateButton = new JButton("Generate Stronger Password");
        styleButton(generateButton); // Styled button
        c.gridx = 1;
        c.gridy = 5;
        c.insets = new Insets(5, 5, 20, 20);
        c.anchor = GridBagConstraints.EAST;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        add(generateButton, c);

        // Suggested Password Field
        JLabel suggestedPasswordLabel = new JLabel("Suggested Password:");
        suggestedPasswordLabel.setFont(font);
        suggestedPasswordLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 6;
        c.insets = new Insets(5, 20, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        add(suggestedPasswordLabel, c);

        suggestedPasswordField = new JTextField(20);
        suggestedPasswordField.setFont(font);
        suggestedPasswordField.setEditable(false);
        suggestedPasswordField.setBackground(fieldBackground);
        suggestedPasswordField.setForeground(textColor);
        suggestedPasswordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        c.gridx = 1;
        c.gridy = 6;
        c.insets = new Insets(5, 5, 5, 20);
        add(suggestedPasswordField, c);

        // Copy to Clipboard Button
        JButton copyButton = new JButton("Copy to Clipboard");
        styleButton(copyButton); // Styled button
        c.gridx = 1;
        c.gridy = 7;
        c.insets = new Insets(5, 5, 20, 20);
        c.anchor = GridBagConstraints.EAST;
        add(copyButton, c);

        // Add functionality to buttons
        checkButton.addActionListener(e -> checkPasswordStrength());
        generateButton.addActionListener(e -> generateStrongPassword());
        copyButton.addActionListener(e -> copyToClipboard());
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.BLACK); // White text
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180)));
        button.setPreferredSize(new Dimension(200, 30));
    }

    private void checkPasswordStrength() {
        String password = new String(passwordField.getPassword());
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password to check.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate strength
        PasswordResult result = calculatePasswordStrength(password);
        strengthLabel.setText("Strength: " + getStrengthLabel(result.getScore()));
        strengthProgressBar.setValue(result.getScore());

        // Set progress bar color
        if (result.getScore() < 30) {
            strengthProgressBar.setForeground(Color.RED);
        } else if (result.getScore() < 70) {
            strengthProgressBar.setForeground(Color.ORANGE);
        } else {
            strengthProgressBar.setForeground(Color.GREEN);
        }

        // Display suggestions
        suggestionsArea.setText(result.getSuggestions());
    }

    private PasswordResult calculatePasswordStrength(String password) {
        int score = 0;
        StringBuilder suggestions = new StringBuilder();

        // Length
        if (password.length() >= 8) {
            score += 30;
        } else {
            score += password.length() * 3; // Up to 24
            suggestions.append("- Increase password length to at least 8 characters.\n");
        }

        // Uppercase letters
        if (password.matches(".*[A-Z].*")) {
            score += 20;
        } else {
            suggestions.append("- Add uppercase letters.\n");
        }

        // Lowercase letters
        if (password.matches(".*[a-z].*")) {
            score += 20;
        } else {
            suggestions.append("- Add lowercase letters.\n");
        }

        // Numbers
        if (password.matches(".*\\d.*")) {
            score += 15;
        } else {
            suggestions.append("- Include numbers.\n");
        }

        // Special characters
        if (password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            score += 15;
        } else {
            suggestions.append("- Incorporate special characters (e.g., !, @, #, $).\n");
        }

        // Entropy calculation can be added here for more detailed analysis

        return new PasswordResult(score, suggestions.toString(), 0.0);
    }

    private String getStrengthLabel(int score) {
        if (score < 30) {
            return "Weak";
        } else if (score < 70) {
            return "Medium";
        } else {
            return "Strong";
        }
    }

    private void generateStrongPassword() {
        String strongPassword = generateRandomPassword(12);
        suggestedPasswordField.setText(strongPassword);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private void copyToClipboard() {
        String password = suggestedPasswordField.getText();
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No password to copy.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        StringSelection stringSelection = new StringSelection(password);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        JOptionPane.showMessageDialog(this, "Password copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Inner class to hold password strength results
    private static class PasswordResult {
        private int score;
        private String suggestions;
        private double entropy;

        public PasswordResult(int score, String suggestions, double entropy) {
            this.score = score;
            this.suggestions = suggestions;
            this.entropy = entropy;
        }

        public int getScore() {
            return score;
        }

        public String getSuggestions() {
            return suggestions;
        }

        public double getEntropy() {
            return entropy;
        }
    }
}
