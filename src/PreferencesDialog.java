// PreferencesDialog.java

import javax.swing.*;
import java.awt.*;

public class PreferencesDialog extends JDialog {
    private JCheckBox lengthCriteriaCheckBox;
    private JCheckBox uppercaseCriteriaCheckBox;
    private JCheckBox lowercaseCriteriaCheckBox;
    private JCheckBox numberCriteriaCheckBox;
    private JCheckBox specialCharCriteriaCheckBox;
    private JButton saveButton;

    // Preferences storage (could be enhanced to read/write from a file)
    private boolean lengthCriteria = true;
    private boolean uppercaseCriteria = true;
    private boolean lowercaseCriteria = true;
    private boolean numberCriteria = true;
    private boolean specialCharCriteria = true;

    public PreferencesDialog(JFrame parent) {
        super(parent, "Preferences", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Initialize components
        initializeComponents(c);
    }

    private void initializeComponents(GridBagConstraints c) {
        JLabel criteriaLabel = new JLabel("Password Strength Criteria:");
        criteriaLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        criteriaLabel.setForeground(Color.DARK_GRAY);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(10, 10, 10, 10);
        c.anchor = GridBagConstraints.CENTER;
        add(criteriaLabel, c);

        lengthCriteriaCheckBox = new JCheckBox("Minimum Length 8");
        lengthCriteriaCheckBox.setSelected(lengthCriteria);
        lengthCriteriaCheckBox.setForeground(Color.DARK_GRAY);
        lengthCriteriaCheckBox.setBackground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.insets = new Insets(5, 20, 5, 20);
        c.anchor = GridBagConstraints.WEST;
        add(lengthCriteriaCheckBox, c);

        uppercaseCriteriaCheckBox = new JCheckBox("Include Uppercase Letters");
        uppercaseCriteriaCheckBox.setSelected(uppercaseCriteria);
        uppercaseCriteriaCheckBox.setForeground(Color.DARK_GRAY);
        uppercaseCriteriaCheckBox.setBackground(Color.WHITE);
        c.gridy = 2;
        add(uppercaseCriteriaCheckBox, c);

        lowercaseCriteriaCheckBox = new JCheckBox("Include Lowercase Letters");
        lowercaseCriteriaCheckBox.setSelected(lowercaseCriteria);
        lowercaseCriteriaCheckBox.setForeground(Color.DARK_GRAY);
        lowercaseCriteriaCheckBox.setBackground(Color.WHITE);
        c.gridy = 3;
        add(lowercaseCriteriaCheckBox, c);

        numberCriteriaCheckBox = new JCheckBox("Include Numbers");
        numberCriteriaCheckBox.setSelected(numberCriteria);
        numberCriteriaCheckBox.setForeground(Color.DARK_GRAY);
        numberCriteriaCheckBox.setBackground(Color.WHITE);
        c.gridy = 4;
        add(numberCriteriaCheckBox, c);

        specialCharCriteriaCheckBox = new JCheckBox("Include Special Characters");
        specialCharCriteriaCheckBox.setSelected(specialCharCriteria);
        specialCharCriteriaCheckBox.setForeground(Color.DARK_GRAY);
        specialCharCriteriaCheckBox.setBackground(Color.WHITE);
        c.gridy = 5;
        add(specialCharCriteriaCheckBox, c);

        // Save Button
        saveButton = new JButton("Save");
        styleButton(saveButton);
        c.gridy = 6;
        c.insets = new Insets(15, 5, 10, 10);
        c.anchor = GridBagConstraints.EAST;
        add(saveButton, c);

        saveButton.addActionListener(e -> savePreferences());
    }

    private void savePreferences() {
        lengthCriteria = lengthCriteriaCheckBox.isSelected();
        uppercaseCriteria = uppercaseCriteriaCheckBox.isSelected();
        lowercaseCriteria = lowercaseCriteriaCheckBox.isSelected();
        numberCriteria = numberCriteriaCheckBox.isSelected();
        specialCharCriteria = specialCharCriteriaCheckBox.isSelected();

        // TODO: Save these preferences to a file or application settings

        // Optionally, notify the PasswordCheckerPanel of the changes
        // This requires passing a reference or using a listener pattern
        // For simplicity, this example does not implement it

        dispose();
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180)));
        button.setPreferredSize(new Dimension(100, 30));
    }
}
