package com.github.johypark97.varchivemacro.macro.common.i18n.loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.stream.Stream;

public class DefaultLanguageConfigManager implements LanguageConfigManager {
    private final Path path;

    public DefaultLanguageConfigManager(Path path) {
        this.path = path;
    }

    @Override
    public Locale load() throws IOException {
        try (Stream<String> stream = Files.lines(path)) {
            return stream.findFirst().map(Locale::forLanguageTag).orElse(Locale.ROOT);
        }
    }

    @Override
    public void save(Locale locale) throws IOException {
        Files.writeString(path, locale.toLanguageTag());
    }
}
