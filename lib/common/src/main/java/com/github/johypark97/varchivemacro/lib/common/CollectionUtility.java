package com.github.johypark97.varchivemacro.lib.common;

import java.util.Collection;

public class CollectionUtility {
    public static boolean hasOne(Collection<?> collection) {
        return collection.size() == 1;
    }
}
