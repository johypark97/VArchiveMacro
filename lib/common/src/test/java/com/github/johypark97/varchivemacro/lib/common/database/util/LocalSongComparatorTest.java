package com.github.johypark97.varchivemacro.lib.common.database.util;

import static com.github.johypark97.varchivemacro.lib.common.json.GsonWrapper.newGsonBuilder_dump;
import static com.github.johypark97.varchivemacro.lib.common.resource.ResourceUtil.readAllLines;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong;
import com.github.johypark97.varchivemacro.lib.common.database.datastruct.LocalSong.GsonListTypeToken;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class LocalSongComparatorTest {
    private static final String SAMPLE_PATH = "/localSongComparatorTestSample.json";

    private static final Gson gson = newGsonBuilder_dump().create();

    @Test
    void test_compare() {
        URL url = getClass().getResource(SAMPLE_PATH);
        if (url == null) {
            fail("sample file not found");
            return;
        }

        String sample;
        try (InputStream stream = url.openStream()) {
            sample = String.join("", readAllLines(stream));
        } catch (IOException e) {
            fail("resource io error");
            return;
        }

        List<LocalSong> expected = gson.fromJson(sample, new GsonListTypeToken());

        List<LocalSong> actual = new ArrayList<>(expected);
        Collections.reverse(actual);
        actual.sort(new LocalSongComparator());

        assertIterableEquals(expected, actual);
    }
}
