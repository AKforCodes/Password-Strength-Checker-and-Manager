// LoginFrame.java

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;

public class LoginFrame extends JFrame {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;
    JButton signUpButton;

    // File to store user data
    private final String USER_DATA_FILE = "user_database.dat";
    HashMap<String, String> userDatabase;

    public LoginFrame() {
        // Initialize the user database
        userDatabase = loadUserDatabase();

        // Set up the frame
        setTitle("Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Set grey background
        getContentPane().setBackground(new Color(50, 50, 50)); // Dark grey
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Initialize components
        initializeComponents(c);
    }

    private void initializeComponents(GridBagConstraints c) {
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        Color textColor = Color.WHITE;
        Color fieldBackground = new Color(70, 70, 70); // Slightly lighter grey

        // Title Label
        JLabel titleLabel = new JLabel("Welcome to Password Manager");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(20, 10, 20, 10);
        c.anchor = GridBagConstraints.CENTER;
        add(titleLabel, c);

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(font);
        usernameLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(5, 10, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        add(usernameLabel, c);

        // Username Field
        usernameField = new JTextField(15);
        usernameField.setFont(font);
        usernameField.setBackground(fieldBackground);
        usernameField.setForeground(textColor);
        usernameField.setCaretColor(textColor);
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        usernameField.setToolTipText("Enter your username");
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        add(usernameField, c);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(font);
        passwordLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(5, 10, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        add(passwordLabel, c);

        // Password Field
        passwordField = new JPasswordField(15);
        passwordField.setFont(font);
        passwordField.setBackground(fieldBackground);
        passwordField.setForeground(textColor);
        passwordField.setCaretColor(textColor);
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        passwordField.setToolTipText("Enter your password");
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(5, 5, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        add(passwordField, c);

        // Login Button
        loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.setToolTipText("Click to log in");
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(15, 10, 10, 5);
        c.anchor = GridBagConstraints.EAST;
        add(loginButton, c);

        // Sign-Up Button
        signUpButton = new JButton("Sign Up");
        styleButton(signUpButton);
        signUpButton.setToolTipText("Click to create a new account");
        c.gridx = 1;
        c.gridy = 3;
        c.insets = new Insets(15, 5, 10, 10);
        c.anchor = GridBagConstraints.WEST;
        add(signUpButton, c);

        // Add action listeners
        loginButton.addActionListener(e -> login());
        signUpButton.addActionListener(e -> openSignUpFrame());

        // Enter key triggers login
        passwordField.addActionListener(e -> login());
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userDatabase.containsKey(username)) {
            String storedHashedPassword = userDatabase.get(username);
            if (Utils.verifyPassword(password, storedHashedPassword)) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the login frame
                // Open the main application frame
                new PasswordStrengthCheckerFrame(username, password);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSignUpFrame() {
        SignUpFrame signUpFrame = new SignUpFrame(this);
        signUpFrame.setVisible(true);
        this.setVisible(false); // Hide the login frame while signing up
    }

    // Load user database from file
    @SuppressWarnings("unchecked")
    private HashMap<String, String> loadUserDatabase() {
        File file = new File(USER_DATA_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (HashMap<String, String>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading user data.", "Error", JOptionPane.ERROR_MESSAGE);
            return new HashMap<>();
        }
    }

    // Save user database to file
    public void saveUserDatabase() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DATA_FILE))) {
            oos.writeObject(userDatabase);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving user data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add a new user to the database
    public void addUser(String username, String hashedPassword) {
        userDatabase.put(username, hashedPassword);
        saveUserDatabase();
    }

    // Apply styling to buttons
    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(100, 100, 100)); // Medium grey
        button.setForeground(Color.BLACK);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setPreferredSize(new Dimension(100, 30));
    }
}
