package com.github.johypark97.varchivemacro.macro.domain.scanner.repository;

import com.github.johypark97.varchivemacro.macro.domain.scanner.model.AnalysisData;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongData;
import java.util.List;

public interface AnalysisDataRepository {
    List<AnalysisData> copyAnalysisDataList();

    boolean isEmpty();

    void clear();

    AnalysisData getAnalysisData(int index);

    AnalysisData createAnalysisData(SongData songData);
}
