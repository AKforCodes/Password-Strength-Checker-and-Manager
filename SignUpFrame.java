import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignUpFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton createAccountButton;
    private JButton cancelButton;

    private LoginFrame loginFrame;

    public SignUpFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;

        // Set up the frame
        setTitle("Sign Up");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        JLabel titleLabel = new JLabel("Create a New Account");
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
        usernameField.setToolTipText("Choose a username");
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
        passwordField.setToolTipText("Choose a strong password");
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(5, 5, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        add(passwordField, c);

        // Confirm Password Label
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(font);
        confirmPasswordLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(5, 10, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        add(confirmPasswordLabel, c);

        // Confirm Password Field
        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(font);
        confirmPasswordField.setBackground(fieldBackground);
        confirmPasswordField.setForeground(textColor);
        confirmPasswordField.setCaretColor(textColor);
        confirmPasswordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        confirmPasswordField.setToolTipText("Re-enter your password");
        c.gridx = 1;
        c.gridy = 3;
        c.insets = new Insets(5, 5, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        add(confirmPasswordField, c);

        // Create Account Button
        createAccountButton = new JButton("Create Account");
        styleButton(createAccountButton);
        createAccountButton.setToolTipText("Click to create your account");
        c.gridx = 0;
        c.gridy = 4;
        c.insets = new Insets(15, 10, 10, 5);
        c.anchor = GridBagConstraints.EAST;
        add(createAccountButton, c);

        // Cancel Button
        cancelButton = new JButton("Cancel");
        styleButton(cancelButton);
        cancelButton.setToolTipText("Click to cancel and return to login");
        c.gridx = 1;
        c.gridy = 4;
        c.insets = new Insets(15, 5, 10, 10);
        c.anchor = GridBagConstraints.WEST;
        add(cancelButton, c);

        // Add action listeners
        createAccountButton.addActionListener(e -> createAccount());
        cancelButton.addActionListener(e -> cancel());

        // Enter key triggers account creation
        confirmPasswordField.addActionListener(e -> createAccount());
    }

    private void createAccount() {
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword()).trim();
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Optionally, add password strength validation here

        // Check if username already exists
        if (loginFrame.userDatabase.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Hash the password using PBKDF2
        String hashedPassword = Utils.hashPassword(password);

        if (hashedPassword == null) {
            JOptionPane.showMessageDialog(this, "Error hashing the password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Save user to the database
        loginFrame.addUser(username, hashedPassword);
        JOptionPane.showMessageDialog(this, "Account created successfully! Redirecting to main application...", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose(); // Close the sign-up frame
        // Open the main application frame
        new PasswordStrengthCheckerFrame(username, password);
    }

    private void cancel() {
        dispose();
        loginFrame.setVisible(true);
    }

    // Apply styling to buttons
    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(100, 100, 100)); // Medium grey
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setPreferredSize(new Dimension(140, 30));
    }
}
