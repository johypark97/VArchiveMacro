package com.github.johypark97.varchivemacro.dbmanager.test;

import static com.github.johypark97.varchivemacro.lib.common.resource.ResourceUtil.readAllLines;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.github.johypark97.varchivemacro.dbmanager.database.util.TitleComparator;

public class TitleSortingTest {
    private static final String SAMPLE_PATH = "/sortingTestSample.txt";

    @Test
    public void SortingTest() {
        List<String> expected;

        try {
            expected = readAllLines(getClass().getResource(SAMPLE_PATH));
            if (expected == null) {
                fail("sample file not found");
                return;
            }
        } catch (IOException e) {
            fail("resource io error");
            return;
        }

        List<String> actual = new ArrayList<>(expected);
        Collections.reverse(actual);
        actual.sort(new TitleComparator());

        assertIterableEquals(expected, actual);
    }
}
