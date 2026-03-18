package com.github.johypark97.varchivemacro.lib.scanner;

public interface StringUtils {
    class StringDiff {
        private static final int COAST = 1;

        private final double similarity;
        private final int distance;

        public StringDiff(String s1, String s2) {
            int l1 = s1.length() + 1;
            int l2 = s2.length() + 1;

            int[][] array = new int[l1][l2];
            for (int i = 0; i < l1; ++i) {
                array[i][0] = i;
            }
            for (int j = 0; j < l2; ++j) {
                array[0][j] = j;
            }

            for (int i = 1; i < l1; ++i) {
                for (int j = 1; j < l2; ++j) {
                    int xy = array[i - 1][j - 1];

                    if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                        array[i][j] = xy;
                    } else {
                        int x = array[i][j - 1];
                        int y = array[i - 1][j];
                        array[i][j] = Math.min(Math.min(x, y), xy) + COAST;
                    }
                }
            }

            distance = array[l1 - 1][l2 - 1];

            int maximumLength = Math.max(l1, l2) - 1;
            similarity = (double) (maximumLength - distance) / maximumLength;
        }

        public int getDistance() {
            return distance;
        }

        public double getSimilarity() {
            return similarity;
        }
    }
}
