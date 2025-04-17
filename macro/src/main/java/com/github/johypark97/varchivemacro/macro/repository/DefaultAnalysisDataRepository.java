package com.github.johypark97.varchivemacro.macro.repository;

import com.github.johypark97.varchivemacro.macro.model.AnalysisData;
import com.github.johypark97.varchivemacro.macro.model.SongData;
import java.util.ArrayList;
import java.util.List;

public class DefaultAnalysisDataRepository implements AnalysisDataRepository {
    private final List<AnalysisData> analysisDataList = new ArrayList<>();

    @Override
    public List<AnalysisData> copyAnalysisDataList() {
        return List.copyOf(analysisDataList);
    }

    @Override
    public boolean isEmpty() {
        return analysisDataList.isEmpty();
    }

    @Override
    public void clear() {
        analysisDataList.clear();
    }

    @Override
    public AnalysisData getAnalysisData(int index) {
        return analysisDataList.get(index);
    }

    @Override
    public AnalysisData createAnalysisData(SongData songData) {
        int id = analysisDataList.size();

        AnalysisData data = new AnalysisData(id, songData);
        analysisDataList.add(data);

        return data;
    }
}
