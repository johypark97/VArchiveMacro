package com.github.johypark97.varchivemacro.lib.common.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ResourceUtil {
    public static List<String> readAllLines(InputStream in, Charset encoding) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding))) {
            return reader.lines().toList();
        }
    }

    public static List<String> readAllLines(InputStream in) throws IOException {
        return readAllLines(in, StandardCharsets.UTF_8);
    }
}
