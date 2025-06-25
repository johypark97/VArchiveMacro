package com.github.johypark97.varchivemacro.macro.ui.common;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class Treeable<T extends Treeable<T>> {
    final Set<T> childSet = new HashSet<>();

    T parent;

    protected abstract T self();

    protected T getParent() {
        return parent;
    }

    protected List<T> getChildList() {
        return childSet.stream().toList();
    }

    protected boolean addChild(T child) {
        Objects.requireNonNull(child);

        // prevent loop
        if (parent != null) {
            T root = parent;

            while (root.parent != null) {
                root = root.parent;
            }

            if (root.equals(child)) {
                throw new IllegalArgumentException("Loop linking is not allowed.");
            }
        } else if (equals(child)) {
            throw new IllegalArgumentException("Self linking is not allowed.");
        }

        // link
        if (child.parent != null || !childSet.add(child)) {
            return false;
        }

        child.parent = Objects.requireNonNull(self());

        return true;
    }

    protected boolean removeChild(T child) {
        Objects.requireNonNull(child);

        // unlink
        if (child.parent == null || !childSet.remove(child)) {
            return false;
        }

        child.parent = null; // NOPMD

        return true;
    }
}
