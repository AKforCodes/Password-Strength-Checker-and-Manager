import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom table cell renderer that displays ImageIcon objects in a JTable cell.
 */
public class ImageRenderer extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        // If the cell value is an ImageIcon, display the icon and remove any text.
        if (value instanceof ImageIcon) {
            setIcon((ImageIcon) value);
            setText("");
        } else {
            // If it's not an ImageIcon, remove any icon and let the superclass handle the value.
            setIcon(null);
            super.setValue(value);
        }
    }
}
