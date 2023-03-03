package com.github.johypark97.varchivemacro.lib.common.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.johypark97.varchivemacro.lib.common.api.Api.Button;
import com.github.johypark97.varchivemacro.lib.common.api.Api.Pattern;
import com.github.johypark97.varchivemacro.lib.common.api.RecordUploader.RequestJson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RecordUploaderTest {
    public static final String TITLE_STRING = "<>";

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void test_RequestData_constructor_score(int score) {
        assertThrows(IllegalArgumentException.class,
                () -> new RequestJson(TITLE_STRING, Button._4, Pattern.NM, score, 0));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void test_RequestData_constructor_maxCombo_notPerfect(int maxCombo) {
        int score = 99;

        RequestJson data = new RequestJson(TITLE_STRING, Button._4, Pattern.NM, score, maxCombo);

        int expected = maxCombo;
        int actual = data.maxCombo;
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void test_RequestData_constructor_maxCombo_perfect(int maxCombo) {
        int score = 100;

        RequestJson data = new RequestJson(TITLE_STRING, Button._4, Pattern.NM, score, maxCombo);

        int expected = 1;
        int actual = data.maxCombo;
        assertEquals(expected, actual);
    }
}
