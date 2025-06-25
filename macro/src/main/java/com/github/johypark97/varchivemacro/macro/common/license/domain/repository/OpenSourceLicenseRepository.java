package com.github.johypark97.varchivemacro.macro.common.license.domain.repository;

import com.github.johypark97.varchivemacro.macro.common.license.domain.model.License;
import java.util.List;

public interface OpenSourceLicenseRepository {
    void save(License value);

    List<String> findAllLibrary();

    License findLicense(String name);
}
