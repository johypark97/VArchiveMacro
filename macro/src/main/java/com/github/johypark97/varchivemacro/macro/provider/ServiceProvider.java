package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.macro.application.macro.service.MacroService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.AnalysisService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.CollectionScanService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.UploadService;

public interface ServiceProvider {
    AnalysisService getAnalysisService();

    MacroService getMacroService();

    CollectionScanService getScannerService();

    UploadService getUploadService();
}
