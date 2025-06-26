package com.github.johypark97.varchivemacro.macro.integration.context;

import com.github.johypark97.varchivemacro.macro.common.license.app.OpenSourceLicenseService;
import com.github.johypark97.varchivemacro.macro.common.license.app.OpenSourceLicenseStorageService;
import com.github.johypark97.varchivemacro.macro.common.license.domain.repository.OpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.common.license.infra.repository.DefaultOpenSourceLicenseRepository;

public class OpenSourceLicenseContext implements Context {
    // repositories
    final OpenSourceLicenseRepository openSourceLicenseRepository =
            new DefaultOpenSourceLicenseRepository();

    // services
    public final OpenSourceLicenseService openSourceLicenseService =
            new OpenSourceLicenseService(openSourceLicenseRepository);
    public final OpenSourceLicenseStorageService openSourceLicenseStorageService =
            new OpenSourceLicenseStorageService(openSourceLicenseRepository);
}
