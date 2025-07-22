package com.github.johypark97.varchivemacro.macro.integration.app.scanner.analysis;

import com.github.johypark97.varchivemacro.lib.jfx.TaskManager;
import com.github.johypark97.varchivemacro.macro.common.config.app.ConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.app.SongCaptureLinkService;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.model.SongCaptureLink;
import com.github.johypark97.varchivemacro.macro.core.scanner.ocr.app.OcrServiceFactory;
import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.app.PixImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.concurrent.Task;

public class ScannerAnalysisService {
    private final CaptureImageService captureImageService;
    private final CaptureService captureService;
    private final ConfigService configService;
    private final PixImageService pixImageService;
    private final SongCaptureLinkService songCaptureLinkService;
    private final SongService songService;

    private final OcrServiceFactory commonOcrServiceFactory;

    public ScannerAnalysisService(CaptureImageService captureImageService,
            CaptureService captureService, ConfigService configService,
            PixImageService pixImageService, SongCaptureLinkService songCaptureLinkService,
            SongService songService, OcrServiceFactory commonOcrServiceFactory) {
        this.captureImageService = captureImageService;
        this.captureService = captureService;
        this.configService = configService;
        this.pixImageService = pixImageService;
        this.songCaptureLinkService = songCaptureLinkService;
        this.songService = songService;

        this.commonOcrServiceFactory = commonOcrServiceFactory;
    }

    public Task<Map<Integer, CaptureAnalysisTaskResult>> createTask(
            Set<Integer> captureEntryIdSet) {
        if (TaskManager.getInstance().isRunningAny()) {
            return null;
        }

        ScannerConfig config = configService.findScannerConfig();

        List<CaptureEntry> captureEntryList = captureService.findAll().stream()
                .filter(x -> captureEntryIdSet.contains(x.entryId())).toList();

        return TaskManager.getInstance().register(CaptureAnalysisTask.class,
                new CaptureAnalysisTask(captureImageService, pixImageService,
                        commonOcrServiceFactory, config, captureEntryList));
    }

    public boolean stopTask() {
        return TaskManager.Helper.cancel(CaptureAnalysisTask.class);
    }

    public Set<Integer> getAllCaptureEntryIdSet() {
        return captureService.findAll().stream().map(CaptureEntry::entryId)
                .collect(Collectors.toSet());
    }

    public int getFirstLinkedCaptureEntryId(int songId) {
        Song song = songService.findSongById(songId);
        if (song == null) {
            return -1;
        }

        Map<CaptureEntry, SongCaptureLink> linkedCaptureEntryMap =
                songCaptureLinkService.groupBySong().get(song);
        if (linkedCaptureEntryMap == null) {
            return -1;
        }

        return linkedCaptureEntryMap.keySet().stream().findFirst().map(CaptureEntry::entryId)
                .orElse(-1);
    }

    public Set<Integer> getCaptureEntryIdSetFromSongIdList(List<Integer> songIdList) {
        return songIdList.stream().map(this::getFirstLinkedCaptureEntryId)
                .collect(Collectors.toSet());
    }
}
