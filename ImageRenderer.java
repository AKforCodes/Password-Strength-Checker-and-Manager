import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class ImageRenderer extends DefaultTableCellRenderer {
    @Override
    protected void setValue(Object value) {
        if (value instanceof ImageIcon) {
            setIcon((ImageIcon) value);
            setText("");
        } else {
            setIcon(null);
            super.setValue(value);
        }
    }
}
