import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;

public class PasswordManagerPanel extends JPanel {
    private JTextField pmWebsiteField;
    private JTextField pmEmailField;
    private JPasswordField pmPasswordField;
    private JTable pmPasswordTable;
    private PasswordTableModel passwordTableModel;
    private JTextField searchField;

    private String encryptionKey; // User's password used for encryption/decryption

    // File to store password entries
    private final String PASSWORD_DATA_FILE = "password_entries.dat";
    private ArrayList<PasswordEntry> passwordEntries;

    // Preferences (Assuming default values for now)
    private boolean lengthCriteria = true;
    private boolean uppercaseCriteria = true;
    private boolean lowercaseCriteria = true;
    private boolean numberCriteria = true;
    private boolean specialCharCriteria = true;

    public PasswordManagerPanel(String userPassword) {
        // Derive encryption key from user's password
        encryptionKey = Utils.deriveAESKey(userPassword);

        // Initialize password entries
        passwordEntries = loadPasswordEntries();

        // Initialize components
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Initialize components
        initializeComponents();

        // Load existing passwords if any
        loadPasswords();
    }

    private void initializeComponents() {
        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();

        // Custom font and colors
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        Color textColor = Color.DARK_GRAY;
        Color fieldBackground = new Color(245, 245, 245); // Light grey

        // Website Field
        JLabel websiteLabel = new JLabel("Website URL:");
        websiteLabel.setFont(font);
        websiteLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(websiteLabel, c);

        pmWebsiteField = new JTextField(20);
        pmWebsiteField.setFont(font);
        pmWebsiteField.setBackground(fieldBackground);
        pmWebsiteField.setForeground(textColor);
        pmWebsiteField.setCaretColor(textColor);
        pmWebsiteField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        pmWebsiteField.setToolTipText("Enter the website URL");
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(10, 5, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        formPanel.add(pmWebsiteField, c);

        // Email/Username Field
        JLabel emailLabel = new JLabel("Email/Username:");
        emailLabel.setFont(font);
        emailLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 10, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(emailLabel, c);

        pmEmailField = new JTextField(20);
        pmEmailField.setFont(font);
        pmEmailField.setBackground(fieldBackground);
        pmEmailField.setForeground(textColor);
        pmEmailField.setCaretColor(textColor);
        pmEmailField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        pmEmailField.setToolTipText("Enter your email or username");
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        formPanel.add(pmEmailField, c);

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(font);
        passwordLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(5, 10, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(passwordLabel, c);

        pmPasswordField = new JPasswordField(20);
        pmPasswordField.setFont(font);
        pmPasswordField.setBackground(fieldBackground);
        pmPasswordField.setForeground(textColor);
        pmPasswordField.setCaretColor(textColor);
        pmPasswordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        pmPasswordField.setToolTipText("Enter the password");
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(5, 5, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        formPanel.add(pmPasswordField, c);

        // Save Button
        JButton saveButton = new JButton("Save Password");
        styleButton(saveButton);
        saveButton.setToolTipText("Click to save the password entry");
        c.gridx = 1;
        c.gridy = 3;
        c.insets = new Insets(10, 5, 10, 10);
        c.anchor = GridBagConstraints.EAST;
        formPanel.add(saveButton, c);

        // Search Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        searchField = new JTextField();
        searchField.setFont(font);
        searchField.setBackground(fieldBackground);
        searchField.setForeground(textColor);
        searchField.setCaretColor(textColor);
        searchField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setToolTipText("Search by website or email");

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().trim();
                passwordTableModel.filter(query);
            }
        });

        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(font);
        searchLabel.setForeground(Color.DARK_GRAY);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(formPanel, BorderLayout.SOUTH);

        // Password Table
        passwordTableModel = new PasswordTableModel();
        pmPasswordTable = new JTable(passwordTableModel) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 0) {
                    return new ImageRenderer();
                } else {
                    return super.getCellRenderer(row, column);
                }
            }
        };
        pmPasswordTable.setRowHeight(32);
        pmPasswordTable.setBackground(Color.WHITE);
        pmPasswordTable.setForeground(Color.DARK_GRAY);
        pmPasswordTable.setSelectionBackground(new Color(230, 230, 250)); // Lavender
        pmPasswordTable.setSelectionForeground(Color.DARK_GRAY);
        pmPasswordTable.setGridColor(new Color(211, 211, 211)); // Light Gray
        pmPasswordTable.setFont(font);
        pmPasswordTable.getTableHeader().setBackground(new Color(245, 245, 245));
        pmPasswordTable.getTableHeader().setForeground(Color.DARK_GRAY);
        pmPasswordTable.getTableHeader().setFont(font);
        JScrollPane tableScrollPane = new JScrollPane(pmPasswordTable);
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Add components to the manager panel
        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Add action listener to the Save Password button
        saveButton.addActionListener(e -> {
            String website = pmWebsiteField.getText().trim();
            String email = pmEmailField.getText().trim();
            String password = String.valueOf(pmPasswordField.getPassword()).trim();

            if (website.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Fetch or upload the website logo
            ImageIcon logo = fetchWebsiteLogo(website);

            // Encrypt the password
            String encryptedPassword = Utils.encryptAES(password, encryptionKey);

            if (encryptedPassword == null) {
                JOptionPane.showMessageDialog(this, "Error encrypting the password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add the entry to the table model
            PasswordEntry entry = new PasswordEntry(logo, website, email, encryptedPassword);
            passwordTableModel.addEntry(entry);

            // Add to password entries list
            passwordEntries.add(entry);
            savePasswordEntries();

            // Clear input fields
            pmWebsiteField.setText("");
            pmEmailField.setText("");
            pmPasswordField.setText("");
        });

        // Add mouse listener for table row interactions
        pmPasswordTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = pmPasswordTable.rowAtPoint(e.getPoint());
                if (row >= 0 && e.getClickCount() == 2) {
                    // Show options when a row is double-clicked
                    showPasswordOptions(row);
                }
            }
        });
    }

    /**
     * Derive a secure AES key from the user's password using PBKDF2.
     */
    private String deriveKey(String userPassword) {
        return Utils.deriveAESKey(userPassword);
    }

    /**
     * Fetch the website logo using the Clearbit Logo API.
     */
    private ImageIcon fetchWebsiteLogo(String websiteUrl) {
        try {
            // Extract the domain from the URL
            String domain = extractDomain(websiteUrl);

            if (domain == null) {
                throw new Exception("Invalid URL");
            }

            // Construct the Clearbit Logo API URL
            String logoUrl = "https://logo.clearbit.com/" + domain;

            // Fetch the image
            Image image = ImageIO.read(new java.net.URL(logoUrl));
            if (image != null) {
                // Resize the image to fit in the table cell
                Image scaledImage = image.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Error fetching logo for: " + websiteUrl);
        }
        // Return a default icon if fetching fails
        return new ImageIcon("default.png"); // Ensure this path is correct or replace with your default icon path
    }

    /**
     * Extract the domain from a URL.
     */
    private String extractDomain(String url) throws Exception {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        java.net.URL netUrl = new java.net.URL(url);
        String host = netUrl.getHost();
        // Remove www. if present
        if (host.startsWith("www.")) {
            host = host.substring(4);
        }
        return host;
    }

    /**
     * Show options to copy or edit the password.
     */
    private void showPasswordOptions(int row) {
        PasswordEntry entry = passwordTableModel.getEntryAt(row);

        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem copyItem = new JMenuItem("Copy Password");
        copyItem.addActionListener(e -> {
            String decryptedPassword = Utils.decryptAES(entry.getPassword(), encryptionKey);
            StringSelection stringSelection = new StringSelection(decryptedPassword);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            new NotificationPopup("Password copied to clipboard!");
        });

        JMenuItem editItem = new JMenuItem("Edit Entry");
        editItem.addActionListener(e -> {
            // Open a dialog to edit the entry
            editPasswordEntry(entry);
        });

        popupMenu.add(copyItem);
        popupMenu.add(editItem);

        // Show the popup menu at the mouse location
        popupMenu.show(pmPasswordTable, pmPasswordTable.getMousePosition().x, pmPasswordTable.getMousePosition().y);
    }

    /**
     * Edit an existing password entry.
     */
    private void editPasswordEntry(PasswordEntry entry) {
        // Create a dialog to edit the entry
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Password Entry", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Custom font
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        Color textColor = Color.DARK_GRAY;
        Color fieldBackground = new Color(245, 245, 245); // Light grey

        // Website Field
        JLabel websiteLabel = new JLabel("Website URL:");
        websiteLabel.setFont(font);
        websiteLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        dialog.add(websiteLabel, c);

        JTextField websiteField = new JTextField(entry.getWebsite(), 20);
        websiteField.setFont(font);
        websiteField.setBackground(fieldBackground);
        websiteField.setForeground(textColor);
        websiteField.setCaretColor(textColor);
        websiteField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        websiteField.setToolTipText("Enter the website URL");
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(10, 5, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        dialog.add(websiteField, c);

        // Email/Username Field
        JLabel emailLabel = new JLabel("Email/Username:");
        emailLabel.setFont(font);
        emailLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(5, 10, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        dialog.add(emailLabel, c);

        JTextField emailField = new JTextField(entry.getEmail(), 20);
        emailField.setFont(font);
        emailField.setBackground(fieldBackground);
        emailField.setForeground(textColor);
        emailField.setCaretColor(textColor);
        emailField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        emailField.setToolTipText("Enter your email or username");
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        dialog.add(emailField, c);

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(font);
        passwordLabel.setForeground(textColor);
        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(5, 10, 5, 5);
        c.anchor = GridBagConstraints.EAST;
        dialog.add(passwordLabel, c);

        JPasswordField passwordField = new JPasswordField(Utils.decryptAES(entry.getPassword(), encryptionKey), 20);
        passwordField.setFont(font);
        passwordField.setBackground(fieldBackground);
        passwordField.setForeground(textColor);
        passwordField.setCaretColor(textColor);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        passwordField.setToolTipText("Enter the password");
        c.gridx = 1;
        c.gridy = 2;
        c.insets = new Insets(5, 5, 5, 10);
        c.anchor = GridBagConstraints.WEST;
        dialog.add(passwordField, c);

        // Save Button
        JButton saveButton = new JButton("Save Changes");
        styleButton(saveButton);
        c.gridx = 1;
        c.gridy = 3;
        c.insets = new Insets(10, 5, 10, 10);
        c.anchor = GridBagConstraints.EAST;
        dialog.add(saveButton, c);

        // Action listener for Save Button
        saveButton.addActionListener(e -> {
            String website = websiteField.getText().trim();
            String email = emailField.getText().trim();
            String password = String.valueOf(passwordField.getPassword()).trim();

            if (website.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update the entry
            entry.setWebsite(website);
            entry.setEmail(email);
            String encryptedPassword = Utils.encryptAES(password, encryptionKey);
            entry.setPassword(encryptedPassword);

            // Update the logo
            entry.setLogo(fetchWebsiteLogo(website));

            // Refresh the table
            passwordTableModel.fireTableDataChanged();

            // Save the updated password entries
            savePasswordEntries();

            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    /**
     * Apply minimalistic styling to buttons.
     */
    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180)));
        button.setPreferredSize(new Dimension(160, 30));
    }

    /**
     * Encrypt the password entries before exporting.
     */
    private String encryptPasswords() throws Exception {
        // Serialize password data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(passwordEntries);
        oos.close();

        // Encrypt using AES
        return Utils.encryptAES(Base64.getEncoder().encodeToString(baos.toByteArray()), encryptionKey);
    }

    /**
     * Decrypt and load password entries from imported data.
     */
    @SuppressWarnings("unchecked")
    private void decryptAndLoadPasswords(String encryptedData) throws Exception {
        // Decrypt using AES
        String decryptedData = Utils.decryptAES(encryptedData, encryptionKey);

        // Deserialize
        byte[] dataBytes = Base64.getDecoder().decode(decryptedData);
        ByteArrayInputStream bais = new ByteArrayInputStream(dataBytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        ArrayList<PasswordEntry> importedEntries = (ArrayList<PasswordEntry>) ois.readObject();
        ois.close();

        // Add to password entries list and update table
        for (PasswordEntry entry : importedEntries) {
            passwordEntries.add(entry);
            passwordTableModel.addEntry(entry);
        }

        // Save the updated password entries
        savePasswordEntries();
    }

    /**
     * Export passwords to an encrypted file.
     */
    public void exportPasswords() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Passwords");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try {
                // Serialize and encrypt the password data
                String encryptedData = encryptPasswords();

                // Write to file
                FileWriter fileWriter = new FileWriter(fileToSave);
                fileWriter.write(encryptedData);
                fileWriter.close();

                new NotificationPopup("Passwords exported successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting passwords.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Import passwords from an encrypted file.
     */
    public void importPasswords() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Passwords");
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();

            try {
                // Read and decrypt the password data
                BufferedReader reader = new BufferedReader(new FileReader(fileToOpen));
                String encryptedData = reader.readLine();
                reader.close();

                decryptAndLoadPasswords(encryptedData);

                new NotificationPopup("Passwords imported successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error importing passwords.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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

        // Load preferences
        loadPreferences();

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

    /**
     * Load password entries from file.
     */
    @SuppressWarnings("unchecked")
    private ArrayList<PasswordEntry> loadPasswordEntries() {
        File file = new File(PASSWORD_DATA_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<PasswordEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading password entries.", "Error", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
    }

    /**
     * Save password entries to file.
     */
    private void savePasswordEntries() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PASSWORD_DATA_FILE))) {
            oos.writeObject(passwordEntries);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving password entries.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Load existing passwords from the entries list.
     */
    private void loadPasswords() {
        for (PasswordEntry entry : passwordEntries) {
            passwordTableModel.addEntry(entry);
        }
    }
}
