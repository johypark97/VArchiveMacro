package com.github.johypark97.varchivemacro.macro.integration.context;

import com.github.johypark97.varchivemacro.macro.integration.app.macro.service.DefaultMacroService;
import com.github.johypark97.varchivemacro.macro.integration.app.macro.service.MacroService;

public class MacroContext implements Context {
    public final MacroService macroService;

    public MacroContext(GlobalContext globalContext) {
        macroService = new DefaultMacroService(globalContext.configRepository);
    }
}
