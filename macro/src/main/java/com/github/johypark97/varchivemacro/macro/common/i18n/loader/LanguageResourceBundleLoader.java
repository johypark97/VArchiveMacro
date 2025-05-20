package com.github.johypark97.varchivemacro.macro.common.i18n.loader;

import java.util.Locale;
import java.util.ResourceBundle;

public interface LanguageResourceBundleLoader {
    ResourceBundle load(Locale locale);
}
