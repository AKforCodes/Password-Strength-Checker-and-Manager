// PasswordStrengthCheckerApp.java

import javax.swing.*;

public class PasswordStrengthCheckerApp {
    public static void main(String[] args) {
        // Set the look and feel to system default for better UI integration
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Initialize the login frame
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
