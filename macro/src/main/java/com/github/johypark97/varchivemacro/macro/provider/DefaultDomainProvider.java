package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.domain.AnalysisDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.DefaultAnalysisDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.DefaultNewRecordDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.DefaultScanDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.NewRecordDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.ScanDataDomain;

public class DefaultDomainProvider implements DomainProvider {
    private final InstanceManager<Object> instanceManager = new LazyInstanceManager<>();

    public DefaultDomainProvider() {
        instanceManager.setConstructor(AnalysisDataDomain.class, DefaultAnalysisDataDomain::new);
        instanceManager.setConstructor(NewRecordDataDomain.class, DefaultNewRecordDataDomain::new);
        instanceManager.setConstructor(ScanDataDomain.class, DefaultScanDataDomain::new);
    }

    @Override
    public AnalysisDataDomain getAnalysisDataDomain() {
        return instanceManager.getInstance(AnalysisDataDomain.class);
    }

    @Override
    public NewRecordDataDomain getNewRecordDataDomain() {
        return instanceManager.getInstance(NewRecordDataDomain.class);
    }

    @Override
    public ScanDataDomain getScanDataDomain() {
        return instanceManager.getInstance(ScanDataDomain.class);
    }
}
