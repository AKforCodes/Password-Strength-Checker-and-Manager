// PasswordManagerPanel.java

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class PasswordManagerPanel extends JPanel {
    private String encryptionPassword;
    private ArrayList<PasswordEntry> passwordEntries;
    private JTable passwordTable;
    private PasswordTableModel tableModel;

    private final String DATA_FILE = "password_entries.dat";

    public PasswordManagerPanel(String encryptionPassword) {
        this.encryptionPassword = encryptionPassword;
        this.passwordEntries = new ArrayList<>();

        setBackground(new Color(50, 50, 50)); // Dark grey
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Password Manager");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Initialize table model and table
        tableModel = new PasswordTableModel(passwordEntries);
        passwordTable = new JTable(tableModel);
        passwordTable.setBackground(new Color(70, 70, 70)); // Slightly lighter grey
        passwordTable.setForeground(Color.WHITE);
        passwordTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordTable.setGridColor(new Color(90, 90, 90)); // Match the theme
        passwordTable.getTableHeader().setBackground(new Color(60, 60, 60));
        passwordTable.getTableHeader().setForeground(Color.BLACK);
        passwordTable.setRowHeight(30); // Adjust row height if you have logos

        // Set cell renderer for ImageIcon
        passwordTable.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());

        JScrollPane tableScrollPane = new JScrollPane(passwordTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Bottom panel with Add, Import, Export buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(50, 50, 50)); // Match main background
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton addPasswordButton = new JButton("Add Password");
        styleButton(addPasswordButton);
        addPasswordButton.setToolTipText("Add a new password entry");
        addPasswordButton.addActionListener(e -> addPasswordEntry());

        JButton importButton = new JButton("Import Passwords");
        styleButton(importButton);
        importButton.setToolTipText("Import password entries from a file");
        importButton.addActionListener(e -> importPasswords());

        JButton exportButton = new JButton("Export Passwords");
        styleButton(exportButton);
        exportButton.setToolTipText("Export password entries to a file");
        exportButton.addActionListener(e -> exportPasswords());

        bottomPanel.add(addPasswordButton);
        bottomPanel.add(importButton);
        bottomPanel.add(exportButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load existing password entries from file
        loadPasswordEntries();
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.BLACK); // Black text
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180)));
        button.setPreferredSize(new Dimension(160, 30));
    }

    /**
     * Adds a new password entry via a dialog.
     */
    private void addPasswordEntry() {
        JTextField websiteField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton selectLogoButton = new JButton("Select Logo");
        JLabel logoPreview = new JLabel();
        logoPreview.setPreferredSize(new Dimension(50, 50));
        logoPreview.setBackground(new Color(70, 70, 70)); // Match inner background
        logoPreview.setOpaque(true);

        // Panel to hold logo selection
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(new Color(50, 50, 50)); // Match main background
        logoPanel.add(selectLogoButton, BorderLayout.NORTH);
        logoPanel.add(logoPreview, BorderLayout.CENTER);

        final ImageIcon[] selectedLogo = {null};

        selectLogoButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Logo Image");
            int userSelection = fileChooser.showOpenDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                ImageIcon logoIcon = new ImageIcon(selectedFile.getAbsolutePath());
                // Resize the icon if necessary
                Image img = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(img);
                logoPreview.setIcon(logoIcon);
                logoPreview.setText(null);
                selectedLogo[0] = logoIcon;
            }
        });

        // Modify the prompt to indicate that Website is optional
        Object[] message = {
            "Website (optional):", websiteField,
            "Email/Username:", emailField,
            "Password:", passwordField,
            "Logo:", logoPanel
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String website = websiteField.getText().trim(); // Now optional
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            ImageIcon logo = selectedLogo[0];

            if (!email.isEmpty() && !password.isEmpty()) { // Removed website from mandatory fields
                // Encrypt the password
                String encryptedPassword = Utils.encryptAES(password, Utils.deriveAESKey(encryptionPassword));
                if (encryptedPassword == null) {
                    JOptionPane.showMessageDialog(this, "Error encrypting the password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PasswordEntry entry = new PasswordEntry(logo, website, email, encryptedPassword);
                passwordEntries.add(entry);
                tableModel.fireTableDataChanged();

                // Save to file
                savePasswordEntries();

                JOptionPane.showMessageDialog(this, "Password entry added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Email and Password fields must be filled.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Exports the password entries to a file.
     */
    public void exportPasswords() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Passwords");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                oos.writeObject(passwordEntries);
                JOptionPane.showMessageDialog(this, "Passwords exported successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting passwords: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Imports password entries from a file.
     */
    public void importPasswords() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Passwords");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToOpen))) {
                Object obj = ois.readObject();
                if (obj instanceof ArrayList<?>) {
                    ArrayList<?> importedEntries = (ArrayList<?>) obj;
                    for (Object entryObj : importedEntries) {
                        if (entryObj instanceof PasswordEntry) {
                            passwordEntries.add((PasswordEntry) entryObj);
                        }
                    }
                    tableModel.fireTableDataChanged();
                    JOptionPane.showMessageDialog(this, "Passwords imported successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid file format.", "Import Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error importing passwords: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Saves the current password entries to a file.
     */
    private void savePasswordEntries() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(passwordEntries);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving password entries.", "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Loads password entries from a file.
     */
    private void loadPasswordEntries() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return; // No existing data to load
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList<?>) {
                ArrayList<?> loadedEntries = (ArrayList<?>) obj;
                for (Object entryObj : loadedEntries) {
                    if (entryObj instanceof PasswordEntry) {
                        passwordEntries.add((PasswordEntry) entryObj);
                    }
                }
                tableModel.fireTableDataChanged();
            }
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error loading password entries.", "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Custom renderer to display ImageIcons in the table.
     */
    private static class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof ImageIcon) {
                JLabel label = new JLabel();
                label.setIcon((ImageIcon) value);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setOpaque(true);
                label.setBackground(new Color(70, 70, 70)); // Match table background
                return label;
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    /**
     * Table model for the password entries.
     */
    private static class PasswordTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Logo", "Website", "Email", "Password"};
        private List<PasswordEntry> entries;

        public PasswordTableModel(List<PasswordEntry> entries) {
            this.entries = entries;
        }

        @Override
        public int getRowCount() {
            return entries.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            PasswordEntry entry = entries.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return entry.getLogo();
                case 1:
                    return entry.getWebsite();
                case 2:
                    return entry.getEmail();
                case 3:
                    // Decrypt password if you want to display it; otherwise, keep it masked
                    // For security, it's better to keep it masked
                    return "••••••";
                default:
                    return null;
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return ImageIcon.class;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false; // Make table non-editable
        }
    }
}
