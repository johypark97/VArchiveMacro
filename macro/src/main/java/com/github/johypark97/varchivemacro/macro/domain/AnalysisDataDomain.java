package com.github.johypark97.varchivemacro.macro.domain;

import com.github.johypark97.varchivemacro.macro.model.AnalysisData;
import com.github.johypark97.varchivemacro.macro.model.SongData;
import java.util.List;

public interface AnalysisDataDomain {
    List<AnalysisData> copyAnalysisDataList();

    boolean isEmpty();

    void clear();

    AnalysisData getAnalysisData(int index);

    AnalysisData createAnalysisData(SongData songData);
}
