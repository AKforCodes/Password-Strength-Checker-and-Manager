import javax.swing.table.AbstractTableModel;
import javax.swing.ImageIcon;
import java.util.ArrayList;

public class PasswordTableModel extends AbstractTableModel {
    private String[] columnNames = {"Logo", "Website", "Email/Username", "Password"};
    private ArrayList<PasswordEntry> data = new ArrayList<>();
    private ArrayList<PasswordEntry> filteredData = new ArrayList<>();
    private String searchQuery = "";

    public PasswordTableModel() {
        filteredData.addAll(data);
    }

    public void addEntry(PasswordEntry entry) {
        data.add(entry);
        filter(searchQuery);
    }

    public PasswordEntry getEntryAt(int row) {
        return filteredData.get(row);
    }

    @Override
    public int getRowCount() {
        return filteredData.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PasswordEntry entry = filteredData.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return entry.getLogo();
            case 1:
                return entry.getWebsite();
            case 2:
                return entry.getEmail();
            case 3:
                // Return masked password
                return "••••••";
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        if (col == 0) {
            return ImageIcon.class;
        } else {
            return String.class;
        }
    }

    public void filter(String query) {
        searchQuery = query;
        filteredData.clear();
        if (query.isEmpty()) {
            filteredData.addAll(data);
        } else {
            for (PasswordEntry entry : data) {
                if (entry.getWebsite().toLowerCase().contains(query.toLowerCase()) ||
                        entry.getEmail().toLowerCase().contains(query.toLowerCase())) {
                    filteredData.add(entry);
                }
            }
        }
        fireTableDataChanged();
    }

    public void setData(ArrayList<PasswordEntry> data) {
        this.data = data;
        filter(searchQuery); // Apply current search query
    }

    public ArrayList<PasswordEntry> getData() {
        return data;
    }
}
