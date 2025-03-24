package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.service.DefaultMacroService;
import com.github.johypark97.varchivemacro.macro.service.DefaultScannerService;
import com.github.johypark97.varchivemacro.macro.service.MacroService;
import com.github.johypark97.varchivemacro.macro.service.ScannerService;

public class DefaultServiceProvider implements ServiceProvider {
    private final InstanceManager<Object> instanceManager = new LazyInstanceManager<>();

    public DefaultServiceProvider(RepositoryProvider repositoryProvider,
            DomainProvider domainProvider) {
        instanceManager.setConstructor(MacroService.class, DefaultMacroService::new);

        instanceManager.setConstructor(ScannerService.class,
                () -> new DefaultScannerService(repositoryProvider.getDatabaseRepository(),
                        repositoryProvider.getRecordRepository(),
                        domainProvider.getAnalysisDataDomain(),
                        domainProvider.getNewRecordDataDomain(),
                        domainProvider.getScanDataDomain()));
    }

    @Override
    public MacroService getMacroService() {
        return instanceManager.getInstance(MacroService.class);
    }

    @Override
    public ScannerService getScannerService() {
        return instanceManager.getInstance(ScannerService.class);
    }
}
