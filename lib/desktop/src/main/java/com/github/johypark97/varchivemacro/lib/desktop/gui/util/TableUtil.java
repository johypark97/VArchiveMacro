package com.github.johypark97.varchivemacro.lib.desktop.gui.util;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TableUtil {
    public static void resizeColumnWidth(JTable table, int min, int max, int margin) {
        int columnCount = table.getColumnCount();
        int rowCount = table.getRowCount();

        for (int column = 0; column < columnCount; ++column) {
            int width = 0;

            for (int row = 0; row < rowCount; ++row) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component component = table.prepareRenderer(renderer, row, column);
                width = Math.max(width, component.getPreferredSize().width);
            }

            width = Math.min(Math.max(min, width + margin), max);
            table.getColumnModel().getColumn(column).setPreferredWidth(width);
        }
    }
}
