package com.github.johypark97.varchivemacro.lib.scanner.database;

import com.github.johypark97.varchivemacro.lib.scanner.database.datastruct.TitleData;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DefaultTitleTool implements TitleTool {
    private final Map<Integer, String> clippedTitleMap = new HashMap<>();
    private final Map<String, String> scannedTitleRemap = new HashMap<>();

    public DefaultTitleTool(Path path) throws IOException {
        TitleData titleData = TitleData.loadJson(path);

        titleData.clipped.forEach((x) -> clippedTitleMap.put(x.id(), x.value()));
        titleData.remap.forEach((x) -> x.from.forEach((y) -> scannedTitleRemap.put(y, x.to)));
    }

    @Override
    public boolean hasClippedTitle(int id) {
        return clippedTitleMap.containsKey(id);
    }

    @Override
    public String getClippedTitleOrDefault(int id, String defaultValue) {
        return clippedTitleMap.getOrDefault(id, defaultValue);
    }

    @Override
    public String remapScannedTitle(String scannedTitle) {
        return scannedTitleRemap.getOrDefault(scannedTitle, scannedTitle);
    }
}
