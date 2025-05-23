package com.github.johypark97.varchivemacro.macro.application.license.repository;

import com.github.johypark97.varchivemacro.macro.application.license.model.License;
import java.util.List;

public interface OpenSourceLicenseRepository {
    void save(License value);

    List<String> findAllLibrary();

    License findLicense(String name);
}
