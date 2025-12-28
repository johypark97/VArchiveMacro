package com.github.johypark97.varchivemacro.macro.common.config.storage.dto;

import com.github.johypark97.varchivemacro.macro.common.config.model.ScannerConfig;
import com.google.gson.annotations.Expose;
import java.util.Set;

public class ScannerConfigDto {
    @Expose
    public ShortcutKey startKey;

    @Expose
    public ShortcutKey stopKey;

    @Expose
    public Set<String> selectedCategory;

    @Expose
    public String accountFile;

    @Expose
    public String cacheDirectory;

    @Expose
    public boolean autoAnalysis;

    @Expose
    public int analyzerThreadCount;

    @Expose
    public int captureDelay;

    @Expose
    public int keyHoldTime;

    public static ScannerConfigDto fromModel(ScannerConfig model) {
        ScannerConfigDto dto = new ScannerConfigDto();

        dto.startKey = ShortcutKey.fromModel(model.startKey().value());
        dto.stopKey = ShortcutKey.fromModel(model.stopKey().value());
        dto.selectedCategory = model.selectedCategory().value();
        dto.accountFile = model.accountFile().value();
        dto.cacheDirectory = model.cacheDirectory().value();
        dto.autoAnalysis = model.autoAnalysis().value();
        dto.analyzerThreadCount = model.analyzerThreadCount().value();
        dto.captureDelay = model.captureDelay().value();
        dto.keyHoldTime = model.keyHoldTime().value();

        return dto;
    }

    public ScannerConfig toModel() {
        return ScannerConfig.editDefault().setStartKey(startKey.toModel())
                .setStopKey(stopKey.toModel()).setSelectedCategory(selectedCategory)
                .setAccountFile(accountFile).setCacheDirectory(cacheDirectory)
                .setAutoAnalysis(autoAnalysis).setAnalyzerThreadCount(analyzerThreadCount)
                .setCaptureDelay(captureDelay).setKeyHoldTime(keyHoldTime).commit();
    }
}
