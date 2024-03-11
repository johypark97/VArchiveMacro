package com.github.johypark97.varchivemacro.lib.desktop.gui.viewmodel;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

public interface TableColumnLookup<T> {
    int getCount();

    T getKey(int index);

    int getIndex(T key);

    String getName(T key);

    default int getIndexInView(JTable table, T key) {
        return table.convertColumnIndexToView(getIndex(key));
    }

    default TableColumn getColumn(JTable table, T key) {
        return table.getColumnModel().getColumn(getIndexInView(table, key));
    }

    final class IndexMapBuilder<T> {
        private final List<T> list = new LinkedList<>();

        public void add(T key) {
            list.add(key);
        }

        public BiMap<T, Integer> build() {
            ImmutableBiMap.Builder<T, Integer> builder = ImmutableBiMap.builder();
            int indexCount = list.size();
            for (int i = 0; i < indexCount; ++i) {
                builder.put(list.get(i), i);
            }

            return builder.build();
        }
    }


    final class NameMapBuilder<T extends Enum<T>> {
        private Class<T> enumClass;
        private Function<T, String> converter;

        public void setEnumClass(Class<T> enumClass) {
            this.enumClass = enumClass;
        }

        public void setConverter(Function<T, String> converter) {
            this.converter = converter;
        }

        public Map<T, String> build() {
            ImmutableMap.Builder<T, String> builder = ImmutableMap.builder();

            for (T key : enumClass.getEnumConstants()) {
                String name = converter.apply(key);
                builder.put(key, name);
            }

            return builder.build();
        }
    }
}
