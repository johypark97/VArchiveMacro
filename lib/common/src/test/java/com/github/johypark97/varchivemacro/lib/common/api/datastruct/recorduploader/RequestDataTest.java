package com.github.johypark97.varchivemacro.lib.common.api.datastruct.recorduploader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.johypark97.varchivemacro.lib.common.api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.Pattern;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestDataTest {
    public static final String TITLE_STRING = "<>";

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void test_constructor_score(int score) {
        assertThrows(IllegalArgumentException.class,
                () -> new RequestData(TITLE_STRING, Button._4, Pattern.NM, score, 0));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void test_constructor_maxCombo_notPerfect(int maxCombo) {
        int score = 99;

        RequestData data = new RequestData(TITLE_STRING, Button._4, Pattern.NM, score, maxCombo);

        int expected = maxCombo;
        int actual = data.maxCombo;
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void test_constructor_maxCombo_perfect(int maxCombo) {
        int score = 100;

        RequestData data = new RequestData(TITLE_STRING, Button._4, Pattern.NM, score, maxCombo);

        int expected = 1;
        int actual = data.maxCombo;
        assertEquals(expected, actual);
    }
}
