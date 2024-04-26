package com.github.johypark97.varchivemacro.macro.spi;

import com.github.johypark97.varchivemacro.lib.common.service.AbstractXmlResourceBundleProvider;
import java.io.InputStream;

public class LanguageProviderImpl extends AbstractXmlResourceBundleProvider
        implements LanguageProvider {
    public static final String BASE_NAME = "strings/Language";

    @Override
    protected String getBaseName(String baseName) {
        return '/' + BASE_NAME;
    }

    @Override
    protected InputStream openResourceStream(String resourceName) {
        return LanguageProviderImpl.class.getResourceAsStream(resourceName);
    }
}
