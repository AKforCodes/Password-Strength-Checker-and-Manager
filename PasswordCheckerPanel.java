import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class PasswordCheckerPanel extends JPanel {
    private JPasswordField passwordField;
    private JLabel strengthLabel;
    private JProgressBar strengthProgressBar;
    private JTextArea suggestionsArea;
    private JTextField suggestedPasswordField;
    private HashMap<String, String> commonPasswords;

    // Preferences
    private boolean lengthCriteria = true;
    private boolean uppercaseCriteria = true;
    private boolean lowercaseCriteria = true;
    private boolean numberCriteria = true;
    private boolean specialCharCriteria = true;

    public PasswordCheckerPanel() {
        // Load common passwords
        loadCommonPasswords();

        setBackground(new Color(50, 50, 50)); // Dark grey
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Initialize components
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

        // Check Button
        JButton checkButton = new JButton("Check Strength");
        styleButton(checkButton);
        checkButton.setToolTipText("Click to evaluate the strength of your password");
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

        // Suggestions Label
        JLabel suggestionsLabel = new JLabel("Suggestions:");
        suggestionsLabel.setFont(font);
        suggestionsLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(20, 20, 5, 5);
        c.anchor = GridBagConstraints.NORTHWEST;
        add(suggestionsLabel, c);

        // Suggestions Area
        suggestionsArea = new JTextArea(5, 30);
        suggestionsArea.setEditable(false);
        suggestionsArea.setLineWrap(true);
        suggestionsArea.setWrapStyleWord(true);
        suggestionsArea.setFont(font);
        suggestionsArea.setBackground(fieldBackground);
        suggestionsArea.setForeground(textColor);
        suggestionsArea.setCaretColor(textColor);
        suggestionsArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(suggestionsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.insets = new Insets(5, 20, 20, 20);
        c.fill = GridBagConstraints.BOTH;
        add(scrollPane, c);

        // Generate Stronger Password Button
        JButton generateButton = new JButton("Generate Stronger Password");
        styleButton(generateButton);
        generateButton.setToolTipText("Click to generate a stronger version of your password");
        c.gridx = 1;
        c.gridy = 5;
        c.insets = new Insets(5, 5, 20, 20);
        c.anchor = GridBagConstraints.EAST;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        add(generateButton, c);

        // Suggested Password Label
        JLabel suggestedPasswordLabel = new JLabel("Suggested Password:");
        suggestedPasswordLabel.setFont(font);
        suggestedPasswordLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 6;
        c.insets = new Insets(5, 20, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        add(suggestedPasswordLabel, c);

        // Suggested Password Field
        suggestedPasswordField = new JTextField(20);
        suggestedPasswordField.setFont(font);
        suggestedPasswordField.setEditable(false);
        suggestedPasswordField.setBackground(fieldBackground);
        suggestedPasswordField.setForeground(textColor);
        suggestedPasswordField.setCaretColor(textColor);
        suggestedPasswordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        suggestedPasswordField.setToolTipText("The generated strong password");
        c.gridx = 1;
        c.gridy = 6;
        c.insets = new Insets(5, 5, 5, 20);
        add(suggestedPasswordField, c);

        // Copy to Clipboard Button
        JButton copyButton = new JButton("Copy to Clipboard");
        styleButton(copyButton);
        copyButton.setToolTipText("Click to copy the suggested password to clipboard");
        c.gridx = 1;
        c.gridy = 7;
        c.insets = new Insets(5, 5, 20, 20);
        c.anchor = GridBagConstraints.EAST;
        add(copyButton, c);

        // Add action listeners
        checkButton.addActionListener(e -> {
            String password = String.valueOf(passwordField.getPassword());
            PasswordResult result = calculatePasswordStrength(password);
            updateStrengthDisplay(result);
        });

        generateButton.addActionListener(e -> {
            String password = String.valueOf(passwordField.getPassword());
            String betterPassword = generateBetterPassword(password);
            suggestedPasswordField.setText(betterPassword);
        });

        copyButton.addActionListener(e -> {
            String selectedPassword = suggestedPasswordField.getText();
            if (selectedPassword.isEmpty()) {
                new NotificationPopup("No password to copy!");
                return;
            }
            StringSelection stringSelection = new StringSelection(selectedPassword);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            new NotificationPopup("Password copied to clipboard!");
        });

        // Clear previous results when typing a new password
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                strengthLabel.setText("Strength: ");
                strengthProgressBar.setValue(0);
                strengthProgressBar.setForeground(Color.GREEN);
                suggestionsArea.setText("");
                suggestedPasswordField.setText("");
            }
        });
    }

    /**
     * Load common passwords and their explanations from a text file into a HashMap.
     */
    private void loadCommonPasswords() {
        commonPasswords = new HashMap<>();
        File file = new File("common_passwords.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "common_passwords.txt file not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(",", 2); // Split into password and reason
                    if (parts.length == 2) {
                        String password = parts[0].trim();
                        String reason = parts[1].trim();
                        commonPasswords.put(password, reason);
                    } else {
                        // If no reason is provided, use a default message
                        commonPasswords.put(parts[0].trim(), "This is a very common password.");
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading common_passwords.txt.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Calculate the password strength score and collect suggestions.
     */
    private PasswordResult calculatePasswordStrength(String password) {
        int score = 0;
        StringBuilder suggestions = new StringBuilder();

        int charSetSize = 0;

        // Load preferences
        loadPreferences();

        if (lengthCriteria) {
            if (password.length() >= 8) {
                score += 25;
                // Length doesn't affect character set size directly
            } else {
                score += 10;
                suggestions.append("- Make your password at least 8 characters long.\n");
            }
        }

        if (lowercaseCriteria) {
            if (password.matches(".*[a-z].*")) {
                score += 10;
                charSetSize += 26;
            } else {
                suggestions.append("- Add lowercase letters.\n");
            }
        }

        if (uppercaseCriteria) {
            if (password.matches(".*[A-Z].*")) {
                score += 10;
                charSetSize += 26;
            } else {
                suggestions.append("- Add uppercase letters.\n");
            }
        }

        if (numberCriteria) {
            if (password.matches(".*[0-9].*")) {
                score += 10;
                charSetSize += 10;
            } else {
                suggestions.append("- Add numbers.\n");
            }
        }

        if (specialCharCriteria) {
            if (password.matches(".*[!@#$%^&*()-+].*")) {
                score += 20;
                charSetSize += 32;
            } else {
                suggestions.append("- Add special characters (e.g., !, @, #, $, %, ^, &, *).\n");
            }
        }

        if (password.matches(".*[a-z].*") && password.matches(".*[A-Z].*")) {
            score += 5;
        }

        if (password.matches(".*[a-zA-Z].*") && password.matches(".*[0-9].*")) {
            score += 5;
        }

        if (password.matches(".*[a-zA-Z0-9].*") && password.matches(".*[!@#$%^&*()-+].*")) {
            score += 15;
        }

        // Check if the password is common
        if (commonPasswords.containsKey(password)) {
            score -= 30; // Subtract points if common
            // Enhanced suggestion explaining why it's common
            String reason = commonPasswords.get(password);
            suggestions.append("- ").append(reason).append(" Consider using a more unique and complex password.\n");
        }

        // Calculate entropy
        double entropy = 0;
        if (charSetSize > 0) {
            entropy = password.length() * (Math.log(charSetSize) / Math.log(2));
        }

        return new PasswordResult(score, suggestions.toString(), entropy);
    }

    /**
     * Update the strength display based on the result.
     */
    private void updateStrengthDisplay(PasswordResult result) {
        int score = Math.max(result.getScore(), 0); // Ensure score is not negative
        strengthProgressBar.setValue(score);

        String strengthText;
        Color color;

        if (result.getEntropy() < 28) {
            strengthText = "Very Weak";
            color = Color.RED;
        } else if (result.getEntropy() < 35) {
            strengthText = "Weak";
            color = Color.ORANGE;
        } else if (result.getEntropy() < 59) {
            strengthText = "Reasonable";
            color = Color.YELLOW;
        } else if (result.getEntropy() < 127) {
            strengthText = "Strong";
            color = Color.GREEN;
        } else {
            strengthText = "Very Strong";
            color = new Color(0, 128, 0); // Dark green
        }

        strengthLabel.setText("Strength: " + strengthText);
        strengthLabel.setForeground(color);
        strengthProgressBar.setForeground(color);
        suggestionsArea.setText(result.getSuggestions());
    }

    /**
     * Generate a stronger password based on the user's input.
     */
    private String generateBetterPassword(String password) {
        StringBuilder newPassword = new StringBuilder(password);
        Random random = new Random();

        // Character sets
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String digits = "0123456789";
        String special = "!@#$%^&*()-+";

        // Flags to check if character types are present
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()-+].*");

        // Add missing character types based on preferences
        if (!hasLower && lowercaseCriteria) {
            newPassword.append(lower.charAt(random.nextInt(lower.length())));
        }
        if (!hasUpper && uppercaseCriteria) {
            newPassword.append(upper.charAt(random.nextInt(upper.length())));
        }
        if (!hasDigit && numberCriteria) {
            newPassword.append(digits.charAt(random.nextInt(digits.length())));
        }
        if (!hasSpecial && specialCharCriteria) {
            newPassword.append(special.charAt(random.nextInt(special.length())));
        }

        // Increase length to at least 8 characters if length criteria is enabled
        if (lengthCriteria) {
            while (newPassword.length() < 8) {
                String allChars = lower + upper + digits + special;
                newPassword.append(allChars.charAt(random.nextInt(allChars.length())));
            }
        }

        // Shuffle the characters in the password
        char[] passwordChars = newPassword.toString().toCharArray();
        for (int i = 0; i < passwordChars.length; i++) {
            int randomIndex = random.nextInt(passwordChars.length);
            // Swap characters
            char temp = passwordChars[i];
            passwordChars[i] = passwordChars[randomIndex];
            passwordChars[randomIndex] = temp;
        }

        return new String(passwordChars);
    }

    /**
     * Apply minimalistic styling to buttons.
     */
    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(100, 100, 100)); // Medium grey
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setPreferredSize(new Dimension(200, 30));
    }

    /**
     * Load user preferences.
     */
    private void loadPreferences() {
        // TODO: Load preferences from a file or application settings
        // For now, we'll assume they are all true
        lengthCriteria = true;
        uppercaseCriteria = true;
        lowercaseCriteria = true;
        numberCriteria = true;
        specialCharCriteria = true;
    }
}
