package com.github.johypark97.varchivemacro.macro.service;

import com.github.johypark97.varchivemacro.macro.model.AnalysisData;
import com.github.johypark97.varchivemacro.macro.model.AnalyzedRecordData;
import java.util.List;
import javafx.concurrent.Task;

public interface AnalysisService {
    Task<Void> createTask_analysis(Runnable onDataReady);

    void stopTask_analysis();

    boolean isAnalysisDataEmpty();

    void clearAnalysisData(Runnable onClear);

    List<AnalysisData> copyAnalysisDataList();

    AnalyzedRecordData getAnalyzedRecordData(int id) throws Exception;
}
