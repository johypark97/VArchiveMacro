package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.utility.UnicodeFilter;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.review.SongData;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorReview;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerReviewViewModel;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerProcessorReviewPresenterImpl implements ScannerProcessorReview.Presenter {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ScannerProcessorReviewPresenterImpl.class);

    private final ScannerProcessorStage scannerProcessorStage;

    private final ScannerContext scannerContext;

    private FilteredList<ScannerReviewViewModel.CaptureData> filteredCaptureDataList;
    private FilteredList<ScannerReviewViewModel.LinkTableData> filteredLinkTableDataList;
    private Map<Integer, ScannerReviewViewModel.LinkTableData> linkTableDataLookup;
    private ScannerReviewViewModel.LinkTableData linkEditorSelectedLinkTableData;

    @MvpView
    public ScannerProcessorReview.View view;

    public ScannerProcessorReviewPresenterImpl(ScannerProcessorStage scannerProcessorStage,
            ScannerContext scannerContext) {
        this.scannerProcessorStage = scannerProcessorStage;

        this.scannerContext = scannerContext;
    }

    private void prepareLinkTableData() {
        List<ScannerReviewViewModel.LinkTableData> list =
                scannerContext.scannerReviewService.getAllSongDataList().stream()
                        .map(ScannerReviewViewModel.LinkTableData::form).toList();

        linkTableDataLookup = list.stream()
                .collect(Collectors.toMap(x -> x.songIdProperty().get(), Function.identity()));

        filteredLinkTableDataList = new FilteredList<>(FXCollections.observableList(list));
        updateLinkTableViewFilter();

        view.setLinkTableItemList(filteredLinkTableDataList);
    }

    private void prepareLinkEditorData() {
        List<ScannerReviewViewModel.CaptureData> list =
                scannerContext.scannerReviewService.getAllCaptureDataList().stream()
                        .map(ScannerReviewViewModel.CaptureData::from).toList();

        filteredCaptureDataList = new FilteredList<>(FXCollections.observableList(list));

        view.setLinkEditorCaptureImageItemList(filteredCaptureDataList);
    }

    private void updateLinkTableDataLink(ScannerReviewViewModel.LinkTableData linkTableData,
            ScannerReviewViewModel.LinkTableData.Problem problem, boolean selected) {
        SongData songData = scannerContext.scannerReviewService.getSongData(
                linkTableData.songIdProperty().get());

        linkTableData.captureImageProperty().setAll(songData.getAllLinkedCaptureData().stream()
                .map(ScannerReviewViewModel.LinkedCaptureData::from).toList());

        linkEditorSelectedLinkTableData.problemProperty().set(problem);

        linkEditorSelectedLinkTableData.selectedProperty().set(selected);

        view.refreshLinkTable();
    }

    @Override
    public void runLinking() {
        scannerContext.scannerReviewService.relink();

        prepareLinkTableData();
        prepareLinkEditorData();
    }

    @Override
    public void updateLinkTableViewFilter() {
        EnumSet<ScannerReviewViewModel.LinkTableData.Accuracy> filterSet =
                EnumSet.noneOf(ScannerReviewViewModel.LinkTableData.Accuracy.class);

        if (view.getLinkTableFilter_exact()) {
            filterSet.add(ScannerReviewViewModel.LinkTableData.Accuracy.EXACT);
        }

        if (view.getLinkTableFilter_duplicated()) {
            filterSet.add(ScannerReviewViewModel.LinkTableData.Accuracy.DUPLICATED);
        }

        if (view.getLinkTableFilter_similar()) {
            filterSet.add(ScannerReviewViewModel.LinkTableData.Accuracy.SIMILAR);
        }

        if (view.getLinkTableFilter_conflict()) {
            filterSet.add(ScannerReviewViewModel.LinkTableData.Accuracy.CONFLICT);
        }

        if (view.getLinkTableFilter_notDetected()) {
            filterSet.add(ScannerReviewViewModel.LinkTableData.Accuracy.NOT_DETECTED);
        }

        filteredLinkTableDataList.setPredicate(x -> filterSet.contains(x.accuracyProperty().get()));
    }

    @Override
    public void showLinkEditor(int songId) {
        linkEditorSelectedLinkTableData = linkTableDataLookup.get(songId);

        view.resetLinkEditorImageAndFilter();

        view.setLinkEditorSelectedSongText(String.format("(%d) [%s] %s - %s",
                linkEditorSelectedLinkTableData.songIdProperty().get(),
                linkEditorSelectedLinkTableData.songPackProperty().get(),
                linkEditorSelectedLinkTableData.songComposerProperty().get(),
                linkEditorSelectedLinkTableData.songTitleProperty().get()));

        updateLinkEditorCaptureImageFilter();

        view.showLinkEditor();
    }

    @Override
    public void hideLinkEditor() {
        view.hideLinkEditor();
    }

    @Override
    public void showLinkEditorImage(int captureEntryId) {
        Image captureImage;

        try {
            captureImage = scannerContext.scannerReviewService.getCaptureImage(captureEntryId);
        } catch (IOException e) {
            LOGGER.atError().setCause(e).log("Capture image loading exception");
            scannerProcessorStage.showError(Language.INSTANCE.getString(
                    "scanner.processor.review.dialog.exception.captureImageLoading"), e);
            return;
        } catch (Exception e) {
            String message = "Unexpected exception";
            LOGGER.atError().setCause(e).log(message);
            scannerProcessorStage.showError(message, e);
            return;
        }

        view.setLinkEditorImage(captureImage);
    }

    @Override
    public void updateLinkEditorCaptureImageFilter() {
        if (linkEditorSelectedLinkTableData == null) {
            return;
        }

        Set<Integer> linkedCaptureSet =
                linkEditorSelectedLinkTableData.captureImageProperty().stream()
                        .map(x -> x.captureData().entryId()).collect(Collectors.toSet());

        Predicate<ScannerReviewViewModel.CaptureData> predicate = x -> true;

        if (!view.isLinkEditorFindAllChecked()) {
            predicate = predicate.and(x -> linkedCaptureSet.contains(x.entryId()));
        }

        String text = view.getLinkEditorFilterText();
        if (!text.isBlank()) {
            String trimmedText = text.trim();

            String normalizedText = scannerContext.songTitleService.normalizeTitle(trimmedText);
            UnicodeFilter filter = new UnicodeFilter(normalizedText);
            Predicate<ScannerReviewViewModel.CaptureData> textPredicate =
                    x -> filter.apply(x.scannedTitle());

            try {
                int value = Integer.parseInt(trimmedText);
                textPredicate = textPredicate.or(x -> x.entryId() == value);
            } catch (NumberFormatException ignored) {
            }

            predicate = predicate.and(textPredicate);
        }

        filteredCaptureDataList.setPredicate(predicate);
    }

    @Override
    public void linkSongAndCapture(int captureEntryId) {
        if (linkEditorSelectedLinkTableData == null) {
            return;
        }

        scannerContext.scannerReviewService.updateLink_link(
                linkEditorSelectedLinkTableData.songIdProperty().get(), captureEntryId);

        updateLinkTableDataLink(linkEditorSelectedLinkTableData,
                ScannerReviewViewModel.LinkTableData.Problem.EDITED, true);

        hideLinkEditor();
    }

    @Override
    public void unlinkSongAndCapture() {
        if (linkEditorSelectedLinkTableData == null) {
            return;
        }

        scannerContext.scannerReviewService.updateLink_unlinkAll(
                linkEditorSelectedLinkTableData.songIdProperty().get());

        updateLinkTableDataLink(linkEditorSelectedLinkTableData,
                ScannerReviewViewModel.LinkTableData.Problem.DELETED, false);

        hideLinkEditor();
    }
}
