package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.macro.common.utility.UnicodeFilter;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerCaptureImageViewerStage;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.CaptureImageViewerViewModel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;

public class ScannerCaptureImageViewerPresenterImpl
        implements ScannerCaptureImageViewer.ScannerCaptureImageViewerPresenter {
    private final ScannerCaptureImageViewerStage scannerCaptureImageViewerStage;

    private final ScannerContext scannerContext;

    private FilteredList<CaptureImageViewerViewModel.CaptureImage> filteredCaptureImageList;

    @MvpView
    public ScannerCaptureImageViewer.ScannerCaptureImageViewerView view;

    public ScannerCaptureImageViewerPresenterImpl(
            ScannerCaptureImageViewerStage scannerCaptureImageViewerStage,
            ScannerContext scannerContext) {
        this.scannerCaptureImageViewerStage = scannerCaptureImageViewerStage;

        this.scannerContext = scannerContext;
    }

    @Override
    public void startView() {
        ObservableList<CaptureImageViewerViewModel.CaptureImage> observableList =
                FXCollections.observableList(scannerContext.captureService.findAll().stream()
                        .map(CaptureImageViewerViewModel.CaptureImage::from).toList());

        filteredCaptureImageList = new FilteredList<>(observableList);

        view.setCaptureImageList(filteredCaptureImageList);
    }

    @Override
    public void requestStopStage() {
        scannerCaptureImageViewerStage.stopStage();
    }

    @Override
    public void updateFilter(String text) {
        if (text.isBlank()) {
            filteredCaptureImageList.setPredicate(null);
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

        filteredCaptureImageList.setPredicate(predicate);
    }

    @Override
    public void showCaptureImage(int entryId) {
        BufferedImage image;
        try {
            image = scannerContext.captureImageService.findById(entryId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        view.showCaptureImage(SwingFXUtils.toFXImage(image, null));
    }
}
