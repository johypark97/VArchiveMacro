package com.github.johypark97.varchivemacro.dbmanager.database.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;

public class TitleComparator implements Comparator<String>, Serializable {
    @Serial
    private static final long serialVersionUID = -3956120129742409963L;

    private static final int NUL = 0;

    @Override
    public int compare(String o1, String o2) {
        final Pointer pLeft = new Pointer(o1);
        final Pointer pRight = new Pointer(o2);

        while (true) {
            int left = pLeft.next();
            int right = pRight.next();

            if (isNul(left) && isNul(right)) {
                return 0;
            }
            if (isNul(left) ^ isNul(right)) {
                return left - right;
            }

            int diffPriority = getPriority(left) - getPriority(right);
            if (diffPriority != 0) {
                return diffPriority;
            }

            if (left != right) {
                return left - right;
            }
        }
    }

    private static int getPriority(int value) {
        if (isSpace(value)) {
            return 0;
        }
        if (isKorean(value)) {
            return 1;
        }
        if (isDigit(value)) {
            return 3;
        }
        if (isEnglish(value)) {
            return 4;
        }
        return 2;
    }

    private static class Pointer {
        private final String s;
        private final int l;
        private int i = 0;

        public Pointer(String str) {
            if (str == null) {
                throw new NullPointerException();
            }

            s = str;
            l = str.length();
        }

        public int next() {
            while (i < l) {
                int value = s.codePointAt(i++);
                switch (value) {
                    case 0x27: // Apostrophe (')
                    case 0x2D: // Hyphen-minus (-)
                        break; // continue;
                    default:
                        return isLowerCase(value) ? toUpperCase(value) : value;
                }
            }

            return NUL;
        }
    }

    private static boolean isNul(int value) {
        return value == NUL;
    }

    private static boolean isSpace(int value) {
        return value == 0x20;
    }

    private static boolean isDigit(int value) {
        return value >= 0x30 && value <= 0x39;
    }

    private static boolean isEnglish(int value) {
        return (value >= 0x41 && value <= 0x5A) || (value >= 0x61 && value <= 0x7A);
    }

    private static boolean isLowerCase(int value) {
        return value >= 0x61 && value <= 0x7A;
    }

    private static int toUpperCase(int value) {
        return value - 0x20;
    }

    private static boolean isKorean(int value) {
        return value >= 0xAC00 && value <= 0xD7A3;
    }
}
