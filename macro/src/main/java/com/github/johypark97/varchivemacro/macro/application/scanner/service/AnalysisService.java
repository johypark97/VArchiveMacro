package com.github.johypark97.varchivemacro.macro.application.scanner.service;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.AnalysisData;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.AnalyzedRecordData;
import java.util.List;
import javafx.concurrent.Task;

public interface AnalysisService {
    boolean isReady_analysis();

    Task<Void> createTask_analysis(Runnable onDataReady);

    void stopTask_analysis();

    void clearAnalysisData(Runnable onClear);

    List<AnalysisData> copyAnalysisDataList();

    AnalyzedRecordData getAnalyzedRecordData(int id) throws Exception;
}
