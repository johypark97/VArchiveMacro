package com.github.johypark97.varchivemacro.macro.common.license.app;

import com.github.johypark97.varchivemacro.macro.common.license.domain.repository.OpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.common.license.infra.service.OpenSourceLicenseLoadService;
import java.io.IOException;

public class OpenSourceLicenseStorageService {
    private final OpenSourceLicenseRepository openSourceLicenseRepository;

    public OpenSourceLicenseStorageService(
            OpenSourceLicenseRepository openSourceLicenseRepository) {
        this.openSourceLicenseRepository = openSourceLicenseRepository;
    }

    public void load() throws IOException {
        new OpenSourceLicenseLoadService().load().forEach(openSourceLicenseRepository::save);
    }
}
