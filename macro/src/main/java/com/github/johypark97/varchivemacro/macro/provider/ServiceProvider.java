package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.macro.service.MacroService;
import com.github.johypark97.varchivemacro.macro.service.ScannerService;

public interface ServiceProvider {
    MacroService getMacroService();

    ScannerService getScannerService();
}
