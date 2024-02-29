package com.github.johypark97.varchivemacro.lib.scanner.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.johypark97.varchivemacro.lib.scanner.Enums.Button;
import com.github.johypark97.varchivemacro.lib.scanner.Enums.Pattern;
import com.github.johypark97.varchivemacro.lib.scanner.api.RecordUploader.RequestJson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RecordUploaderTest {
    public static final String TITLE_STRING = "<>";

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    void test_RequestJson_constructor_rate(int rate) {
        assertThrows(IllegalArgumentException.class,
                () -> new RequestJson(TITLE_STRING, Button._4, Pattern.NM, rate, false));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void test_RequestJson_constructor_maxCombo_notPerfect(boolean maxCombo) {
        int rate = 99;

        RequestJson data = new RequestJson(TITLE_STRING, Button._4, Pattern.NM, rate, maxCombo);

        int expected = maxCombo ? 1 : 0;
        int actual = data.maxCombo;
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void test_RequestJson_constructor_maxCombo_perfect(boolean maxCombo) {
        int rate = 100;

        RequestJson data = new RequestJson(TITLE_STRING, Button._4, Pattern.NM, rate, maxCombo);

        int expected = 1;
        int actual = data.maxCombo;
        assertEquals(expected, actual);
    }
}
