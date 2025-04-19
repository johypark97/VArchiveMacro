package com.github.johypark97.varchivemacro.macro.infrastructure.license.repository;

import java.io.IOException;
import java.util.List;

public interface OpenSourceLicenseRepository {
    List<String> getLibraryList();

    String getLicenseText(String library) throws IOException;

    String getLibraryUrl(String library);
}
