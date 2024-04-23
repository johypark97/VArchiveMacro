package com.github.johypark97.varchivemacro.macro.spi;

import com.github.johypark97.varchivemacro.lib.common.service.BaseXmlResourceBundleProvider;
import java.io.InputStream;

public class LanguageProviderImpl extends BaseXmlResourceBundleProvider
        implements LanguageProvider {
    @Override
    protected String getBaseName(String baseName) {
        return "/strings/Language";
    }

    @Override
    protected InputStream openResourceStream(String resourceName) {
        return LanguageProviderImpl.class.getResourceAsStream(resourceName);
    }
}
