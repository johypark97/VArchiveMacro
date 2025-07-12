package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.macro.common.converter.PngImageConverter;
import com.github.johypark97.varchivemacro.macro.common.utility.UnicodeFilter;
import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.Capture;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureregion.domain.model.CaptureRegion;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordPattern;
import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.SongRecord;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerCaptureImageViewerStage;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.CaptureImageViewerViewModel;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class ScannerCaptureImageViewerPresenterImpl implements ScannerCaptureImageViewer.Presenter {
    private final ScannerCaptureImageViewerStage scannerCaptureImageViewerStage;

    private final ScannerContext scannerContext;

    private FilteredList<CaptureImageViewerViewModel.CaptureImage> captureImageFilteredList;

    @MvpView
    public ScannerCaptureImageViewer.View view;

    public ScannerCaptureImageViewerPresenterImpl(
            ScannerCaptureImageViewerStage scannerCaptureImageViewerStage,
            ScannerContext scannerContext) {
        this.scannerCaptureImageViewerStage = scannerCaptureImageViewerStage;

        this.scannerContext = scannerContext;
    }

    private Image cropAndConvert(BufferedImage image, Rectangle rectangle) {
        return SwingFXUtils.toFXImage(
                image.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height),
                null);
    }

    @Override
    public void startView() {
        ObservableList<CaptureImageViewerViewModel.CaptureImage> observableList =
                FXCollections.observableList(scannerContext.captureService.findAll().stream()
                        .map(CaptureImageViewerViewModel.CaptureImage::from).toList());

        captureImageFilteredList = new FilteredList<>(observableList);

        view.setCaptureImageList(captureImageFilteredList);
    }

    @Override
    public void requestStopStage() {
        scannerCaptureImageViewerStage.stopStage();
    }

    @Override
    public void updateFilter(String text) {
        if (text.isBlank()) {
            captureImageFilteredList.setPredicate(null);
            return;
        }

        String trimmedText = text.trim();

        String normalizedText = scannerContext.songTitleService.normalizeTitle(trimmedText);
        UnicodeFilter filter = new UnicodeFilter(normalizedText);
        Predicate<CaptureImageViewerViewModel.CaptureImage> predicate =
                x -> filter.apply(x.scannedTitle());

        try {
            int value = Integer.parseInt(trimmedText);
            predicate = predicate.or(x -> x.entryId() == value);
        } catch (NumberFormatException ignored) {
        }

        captureImageFilteredList.setPredicate(predicate);
    }

    @Override
    public void showCaptureImage(int entryId) {
        Capture capture = scannerContext.captureService.findById(entryId).capture();

        BufferedImage captureImage;
        try {
            captureImage = PngImageConverter.toBufferedImage(
                    scannerContext.captureImageService.findById(entryId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CaptureImageViewerViewModel.CaptureImageDetail detail =
                new CaptureImageViewerViewModel.CaptureImageDetail();

        detail.analyzed = capture.isAnalyzed();
        detail.captureImage = SwingFXUtils.toFXImage(captureImage, null);

        detail.titleImage = cropAndConvert(captureImage, capture.region.getTitle());
        detail.titleText = capture.scannedTitle;

        for (RecordButton button : RecordButton.values()) {
            for (RecordPattern pattern : RecordPattern.values()) {
                CaptureRegion region = capture.region;
                Image rateImage = cropAndConvert(captureImage, region.getRate(button, pattern));
                Image maxComboImage =
                        cropAndConvert(captureImage, region.getMaxCombo(button, pattern));

                SongRecord record = capture.getSongRecord(button, pattern);
                String rateText = record == null ? "" : String.format("%.2f", record.rate());
                boolean maxCombo = record != null && record.maxCombo();

                detail.cellDataArray[pattern.getWeight()][button.getWeight()] =
                        new CaptureImageViewerViewModel.CaptureImageDetail.CellData(rateImage,
                                maxComboImage, rateText, maxCombo);
            }
        }

        view.showCaptureImage(detail);
    }
}
