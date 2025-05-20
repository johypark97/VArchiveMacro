package com.github.johypark97.varchivemacro.macro.common.i18n.loader;

import java.io.IOException;
import java.util.Locale;

public interface LanguageConfigManager {
    Locale load() throws IOException;

    void save(Locale locale) throws IOException;
}
