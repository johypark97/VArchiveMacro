package com.github.johypark97.varchivemacro.dbmanager.test;

import static com.github.johypark97.varchivemacro.lib.common.resource.ResourceUtil.readAllLines;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.github.johypark97.varchivemacro.dbmanager.database.util.TitleComparator;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class TitleSortingTest {
    private static final String SAMPLE_PATH = "/sortingTestSample.txt";

    @Test
    void SortingTest() {
        URL url = getClass().getResource(SAMPLE_PATH);
        if (url == null) {
            fail("sample file not found");
            return;
        }

        List<String> expected;
        try (InputStream stream = url.openStream()) {
            expected = readAllLines(stream);
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