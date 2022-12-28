package com.github.johypark97.varchivemacro.lib.common.gui.util;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class TableUtil {
    public static void resizeColumnWidth(JTable table, int min, int max, int margin) {
        TableColumnModel tableColumnModel = table.getColumnModel();
        int columnCount = table.getColumnCount();
        int rowCount = table.getRowCount();

        for (int column = 0; column < columnCount; ++column) {
            int width = 0;

            for (int row = 0; row < rowCount; ++row) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component component = table.prepareRenderer(renderer, row, column);
                width = Math.max(width, component.getPreferredSize().width + margin);
            }

            width = Math.min(Math.max(min, width), max);
            tableColumnModel.getColumn(column).setPreferredWidth(width);
        }
    }
}
