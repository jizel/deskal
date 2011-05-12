package cz.muni.fi.pb138.deskal.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Drak 
 */
public class DaysTableCellRenderer extends DefaultTableCellRenderer {

     public DaysTableCellRenderer() {
        setOpaque(true);
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        setBackground(new Color(154, 205, 50));
        return this;
    }
}
