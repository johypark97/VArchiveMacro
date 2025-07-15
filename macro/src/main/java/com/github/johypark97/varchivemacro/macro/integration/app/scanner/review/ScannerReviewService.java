package com.github.johypark97.varchivemacro.macro.integration.app.scanner.review;

import com.github.johypark97.varchivemacro.macro.common.converter.PngImageConverter;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.app.CaptureService;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app.CaptureImageService;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.model.PngImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.app.SongCaptureLinkService;
import com.github.johypark97.varchivemacro.macro.core.scanner.link.domain.model.SongCaptureLink;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.app.SongService;
import com.github.johypark97.varchivemacro.macro.core.scanner.song.domain.model.Song;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.service.SongCaptureLinkingService;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ScannerReviewService {
    private final CaptureImageService captureImageService;
    private final CaptureService captureService;
    private final SongCaptureLinkService songCaptureLinkService;
    private final SongCaptureLinkingService songCaptureLinkingService;
    private final SongService songService;

    public ScannerReviewService(CaptureImageService captureImageService,
            CaptureService captureService, SongCaptureLinkService songCaptureLinkService,
            SongCaptureLinkingService songCaptureLinkingService, SongService songService) {
        this.captureImageService = captureImageService;
        this.captureService = captureService;
        this.songCaptureLinkService = songCaptureLinkService;
        this.songCaptureLinkingService = songCaptureLinkingService;
        this.songService = songService;
    }

    public void relink() {
        songCaptureLinkService.deleteAll();
        songCaptureLinkingService.link();
    }

    public SongData getSongData(int songId) {
        Song song = songService.findSongById(songId);
        if (song == null) {
            return null;
        }

        SongData data = new SongData(song);

        Map<CaptureEntry, SongCaptureLink> linkedCaptrueMap =
                songCaptureLinkService.groupBySong().get(song);
        if (linkedCaptrueMap != null) {
            linkedCaptrueMap.values().forEach(
                    x -> data.addLinkedCaptureData(CaptureData.from(x.captureEntry()),
                            x.distance()));
        }

        return data;
    }

    public List<SongData> getAllSongDataList() {
        return songService.groupSongByCategory().values().stream().flatMap(Collection::stream)
                .map(song -> getSongData(song.songId())).toList();
    }

    public List<CaptureData> getAllCaptureDataList() {
        return captureService.findAll().stream().map(CaptureData::from).toList();
    }

    public Image getCaptureImage(int captureEntryId) throws IOException {
        PngImage pngImage = captureImageService.findById(captureEntryId);
        BufferedImage bufferedImage = PngImageConverter.toBufferedImage(pngImage);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public void updateLink_link(int songId, int captureEntryId) {
        CaptureEntry captureEntry = captureService.findById(captureEntryId);
        if (captureEntry == null) {
            throw new IllegalArgumentException(
                    "Cannot find captureEntry with id: " + captureEntryId);
        }

        updateLink_unlinkAll(songId);

        Song song = songService.findSongById(songId);
        songCaptureLinkService.save(new SongCaptureLink(song, captureEntry, 0, 0));
    }

    public void updateLink_unlinkAll(int songId) {
        Song song = songService.findSongById(songId);
        if (song == null) {
            throw new IllegalArgumentException("Cannot find song with id: " + songId);
        }

        Map<CaptureEntry, SongCaptureLink> linkedCaptureEntryMap =
                songCaptureLinkService.groupBySong().get(song);
        if (linkedCaptureEntryMap == null) {
            return;
        }

        // fix ConcurrentModificationException
        List<SongCaptureLink> linkList = List.copyOf(linkedCaptureEntryMap.values());

        for (SongCaptureLink link : linkList) {
            songCaptureLinkService.remove(link.song(), link.captureEntry());
        }
    }
}
