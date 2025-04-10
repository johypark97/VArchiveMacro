package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.service.AnalysisService;
import com.github.johypark97.varchivemacro.macro.service.CollectionScanService;
import com.github.johypark97.varchivemacro.macro.service.DefaultAnalysisService;
import com.github.johypark97.varchivemacro.macro.service.DefaultCollectionScanService;
import com.github.johypark97.varchivemacro.macro.service.DefaultMacroService;
import com.github.johypark97.varchivemacro.macro.service.DefaultUploadService;
import com.github.johypark97.varchivemacro.macro.service.MacroService;
import com.github.johypark97.varchivemacro.macro.service.UploadService;

public class DefaultServiceProvider implements ServiceProvider {
    private final InstanceManager<Object> instanceManager = new LazyInstanceManager<>();

    public DefaultServiceProvider(RepositoryProvider repositoryProvider,
            DomainProvider domainProvider) {
        instanceManager.setConstructor(AnalysisService.class,
                () -> new DefaultAnalysisService(repositoryProvider.getConfigRepository(),
                        domainProvider.getAnalysisDataDomain(),
                        domainProvider.getNewRecordDataDomain(),
                        domainProvider.getScanDataDomain()));

        instanceManager.setConstructor(CollectionScanService.class,
                () -> new DefaultCollectionScanService(repositoryProvider.getConfigRepository(),
                        repositoryProvider.getDatabaseRepository(),
                        domainProvider.getScanDataDomain()));

        instanceManager.setConstructor(MacroService.class,
                () -> new DefaultMacroService(repositoryProvider.getConfigRepository()));

        instanceManager.setConstructor(UploadService.class,
                () -> new DefaultUploadService(repositoryProvider.getConfigRepository(),
                        repositoryProvider.getDatabaseRepository(),
                        repositoryProvider.getRecordRepository(),
                        domainProvider.getAnalysisDataDomain(),
                        domainProvider.getNewRecordDataDomain()));
    }

    @Override
    public AnalysisService getAnalysisService() {
        return instanceManager.getInstance(AnalysisService.class);
    }

    @Override
    public MacroService getMacroService() {
        return instanceManager.getInstance(MacroService.class);
    }

    @Override
    public CollectionScanService getScannerService() {
        return instanceManager.getInstance(CollectionScanService.class);
    }

    @Override
    public UploadService getUploadService() {
        return instanceManager.getInstance(UploadService.class);
    }
}
