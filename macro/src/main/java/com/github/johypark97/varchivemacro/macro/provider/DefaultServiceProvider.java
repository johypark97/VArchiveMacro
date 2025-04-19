package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.application.macro.service.DefaultMacroService;
import com.github.johypark97.varchivemacro.macro.application.macro.service.MacroService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.AnalysisService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.CollectionScanService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.DefaultAnalysisService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.DefaultCollectionScanService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.DefaultUploadService;
import com.github.johypark97.varchivemacro.macro.application.scanner.service.UploadService;

public class DefaultServiceProvider implements ServiceProvider {
    private final InstanceManager<Object> instanceManager = new LazyInstanceManager<>();

    public DefaultServiceProvider(RepositoryProvider repositoryProvider) {
        instanceManager.setConstructor(AnalysisService.class,
                () -> new DefaultAnalysisService(repositoryProvider.getAnalysisDataRepository(),
                        repositoryProvider.getConfigRepository(),
                        repositoryProvider.getNewRecordDataRepository(),
                        repositoryProvider.getScanDataRepository()));

        instanceManager.setConstructor(CollectionScanService.class,
                () -> new DefaultCollectionScanService(repositoryProvider.getConfigRepository(),
                        repositoryProvider.getDatabaseRepository(),
                        repositoryProvider.getScanDataRepository()));

        instanceManager.setConstructor(MacroService.class,
                () -> new DefaultMacroService(repositoryProvider.getConfigRepository()));

        instanceManager.setConstructor(UploadService.class,
                () -> new DefaultUploadService(repositoryProvider.getAnalysisDataRepository(),
                        repositoryProvider.getConfigRepository(),
                        repositoryProvider.getDatabaseRepository(),
                        repositoryProvider.getNewRecordDataRepository(),
                        repositoryProvider.getRecordRepository()));
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
