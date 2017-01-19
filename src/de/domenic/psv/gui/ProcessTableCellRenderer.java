package de.domenic.psv.gui;

import de.domenic.psv.process.Process;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * This renderer is used to color the cells (background) in the table to display the current state of a process.
 *
 * Created by Domenic on 25.12.2016.
 */
public class ProcessTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == 0) {
            c.setForeground(Color.BLACK);
            c.setBackground(Color.LIGHT_GRAY);
            return c;
        }
        if (value instanceof Process.State) {
            Process.State state =  (Process.State) value;
            Color color = getColorForState(state);
            c.setBackground(color);
         //   c.setForeground(color);
        } else {
           // c.setForeground(Color.WHITE);
            c.setBackground(Color.WHITE);
        }

        c.setForeground(Color.BLACK);

        return c;
    }

    private Color getColorForState(Process.State state) {
        switch(state) {
            case Ready: return Color.ORANGE;
            case Running: return Color.GREEN;
            case Terminated: return Color.BLACK;
            case Waiting: return Color.RED;
            case NotStarted: return Color.DARK_GRAY;
            default: return Color.WHITE;
        }
    }

}
