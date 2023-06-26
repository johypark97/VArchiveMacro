package com.github.johypark97.varchivemacro.dbmanager.gui.presenter.viewmodel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;

public class CaseInsensitiveRegexRowFilter extends RowFilter<TableModel, Integer> {
    private final int column;

    private Matcher matcher;

    public CaseInsensitiveRegexRowFilter(String pattern, int column) {
        if (pattern != null && !pattern.isBlank()) {
            try {
                matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher("");
            } catch (PatternSyntaxException ignored) {
            }
        }

        this.column = column;
    }

    @Override
    public boolean include(Entry<? extends TableModel, ? extends Integer> entry) {
        if (matcher == null || column < 0 || column >= entry.getValueCount()) {
            return true;
        }

        return matcher.reset(entry.getStringValue(column)).find();
    }
}
