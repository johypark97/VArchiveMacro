package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.macro.service.AnalysisService;
import com.github.johypark97.varchivemacro.macro.service.CollectionScanService;
import com.github.johypark97.varchivemacro.macro.service.MacroService;
import com.github.johypark97.varchivemacro.macro.service.UploadService;

public interface ServiceProvider {
    AnalysisService getAnalysisService();

    MacroService getMacroService();

    CollectionScanService getScannerService();

    UploadService getUploadService();
}
