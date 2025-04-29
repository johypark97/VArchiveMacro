package com.github.johypark97.varchivemacro.macro.infrastructure.license.repository;

import com.github.johypark97.varchivemacro.macro.infrastructure.license.model.License;
import java.io.IOException;
import java.util.List;

public interface OpenSourceLicenseRepository {
    void load();

    List<String> findAllLibrary();

    License findLicense(String library) throws IOException;
}
