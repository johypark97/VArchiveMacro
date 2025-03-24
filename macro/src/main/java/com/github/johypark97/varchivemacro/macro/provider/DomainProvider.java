package com.github.johypark97.varchivemacro.macro.provider;

import com.github.johypark97.varchivemacro.macro.domain.AnalysisDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.NewRecordDataDomain;
import com.github.johypark97.varchivemacro.macro.domain.ScanDataDomain;

public interface DomainProvider {
    AnalysisDataDomain getAnalysisDataDomain();

    NewRecordDataDomain getNewRecordDataDomain();

    ScanDataDomain getScanDataDomain();
}
