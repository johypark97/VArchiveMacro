package com.github.johypark97.varchivemacro.macro.ui.common;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import java.util.Optional;
import javax.net.ssl.SSLHandshakeException;

public record ExceptionTranslator(String header, String content, Throwable throwable) {
    public static Optional<ExceptionTranslator> translate(Throwable throwable) {
        Language language = Language.INSTANCE;

        if (throwable instanceof SSLHandshakeException e) {
            if (e.getMessage().contains("PKIX path building failed")) {
                String header = language.getString(
                        "exceptionTranslator.SSLHandshakeException.invalidCert.header");
                String content = language.getFormatString(
                        "exceptionTranslator.SSLHandshakeException.invalidCert.content");
                return Optional.of(new ExceptionTranslator(header, content, e));
            }
        }

        return Optional.empty();
    }
}
