package com.github.johypark97.varchivemacro.macro.common.license.app;

import com.github.johypark97.varchivemacro.macro.common.license.domain.model.License;
import com.github.johypark97.varchivemacro.macro.common.license.domain.repository.OpenSourceLicenseRepository;
import java.util.List;

public class OpenSourceLicenseService implements OpenSourceLicenseRepository {
    private final OpenSourceLicenseRepository openSourceLicenseRepository;

    public OpenSourceLicenseService(OpenSourceLicenseRepository openSourceLicenseRepository) {
        this.openSourceLicenseRepository = openSourceLicenseRepository;
    }

    @Override
    public void save(License value) {
        openSourceLicenseRepository.save(value);
    }

    @Override
    public List<String> findAllLibrary() {
        return openSourceLicenseRepository.findAllLibrary();
    }

    @Override
    public License findLicense(String name) {
        return openSourceLicenseRepository.findLicense(name);
    }
}
