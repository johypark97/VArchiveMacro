package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.utility.UnicodeFilter;
import com.github.johypark97.varchivemacro.macro.integration.app.scanner.review.SongData;
import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;
import com.github.johypark97.varchivemacro.macro.ui.common.EventDebouncer;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorReview;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerReviewViewModel;
import com.github.johypark97.varchivemacro.macro.ui.stage.ScannerProcessorStage;
import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScannerProcessorReviewPresenterImpl implements ScannerProcessorReview.Presenter {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ScannerProcessorReviewPresenterImpl.class);

    private final ScannerProcessorStage scannerProcessorStage;

    private final GlobalContext globalContext;
    private final ScannerContext scannerContext;

    private final EventDebouncer updateLinkTableSelectedCountTextEventDebouncer =
            new EventDebouncer();
    private final Map<Integer, ScannerReviewViewModel.LinkTableData> linkTableDataLookup =
            new LinkedHashMap<>();

    private FilteredList<ScannerReviewViewModel.CaptureData> filteredCaptureDataList;
    private FilteredList<ScannerReviewViewModel.LinkTableData> filteredLinkTableDataList;
    private ScannerReviewViewModel.LinkTableData linkEditorSelectedLinkTableData;

    @MvpView
    public ScannerProcessorReview.View view;

    public ScannerProcessorReviewPresenterImpl(ScannerProcessorStage scannerProcessorStage,
            GlobalContext globalContext, ScannerContext scannerContext) {
        this.scannerProcessorStage = scannerProcessorStage;

        this.globalContext = globalContext;
        this.scannerContext = scannerContext;

        updateLinkTableSelectedCountTextEventDebouncer.setCallback(
                this::updateLinkTableSelectedCountText);
    }

    private void prepareLinkTableData() {
        linkTableDataLookup.clear();

        Set<String> selectedCategorySet = scannerContext.getSelectedCategorySet();
        scannerContext.scannerReviewService.getAllSongDataList().stream()
                .filter(x -> selectedCategorySet.contains(x.song.pack().category().name()))
                .map(ScannerReviewViewModel.LinkTableData::form).forEach(x -> {
                    linkTableDataLookup.put(x.songIdProperty().get(), x);
                    x.setOnSelectedChange(updateLinkTableSelectedCountTextEventDebouncer::trigger);
                });

        filteredLinkTableDataList =
                new FilteredList<>(FXCollections.observableArrayList(linkTableDataLookup.values()));

        view.setLinkTableItemList(filteredLinkTableDataList);
        updateLinkTableViewFilter();

        updateLinkTableSelectedCountTextEventDebouncer.trigger();
    }

    private void prepareLinkEditorData() {
        filteredCaptureDataList = new FilteredList<>(
                scannerContext.scannerReviewService.getAllCaptureDataList().stream()
                        .map(ScannerReviewViewModel.CaptureData::from)
                        .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        view.setLinkEditorCaptureImageItemList(filteredCaptureDataList);
    }

    private void updateLinkTableSelectedCountText() {
        int editedSelected = 0;
        int editedTotal = 0;
        int exactSelected = 0;
        int exactTotal = 0;
        int similarSelected = 0;
        int similarTotal = 0;

        for (ScannerProcessorReview.LinkTableToggleType type : ScannerProcessorReview.LinkTableToggleType.values()) {
            List<ScannerReviewViewModel.LinkTableData> list;

            switch (type) {
                case EDITED -> {
                    list = findLinkTableDataToggleTarget(type);
                    editedTotal = list.size();
                    editedSelected =
                            (int) list.stream().filter(x -> x.selectedProperty().get()).count();
                }
                case EXACT -> {
                    list = findLinkTableDataToggleTarget(type);
                    exactTotal = list.size();
                    exactSelected =
                            (int) list.stream().filter(x -> x.selectedProperty().get()).count();
                }
                case SIMILAR -> {
                    list = findLinkTableDataToggleTarget(type);
                    similarTotal = list.size();
                    similarSelected =
                            (int) list.stream().filter(x -> x.selectedProperty().get()).count();
                }
                case UNSELECT_ALL -> {
                }
            }
        }

        view.updateLinkTableSelectedCountText(exactSelected, exactTotal, similarSelected,
                similarTotal, editedSelected, editedTotal);
    }

    private List<ScannerReviewViewModel.LinkTableData> findLinkTableDataToggleTarget(
            ScannerProcessorReview.LinkTableToggleType type) {
        Predicate<ScannerReviewViewModel.LinkTableData> predicate = switch (type) {
            case EDITED -> x -> ScannerReviewViewModel.LinkTableData.Problem.EDITED.equals(
                    x.problemProperty().get());
            case EXACT -> x -> ScannerReviewViewModel.LinkTableData.Accuracy.EXACT.equals(
                    x.accuracyProperty().get())
                    && ScannerReviewViewModel.LinkTableData.Problem.NONE.equals(
                    x.problemProperty().get());
            case SIMILAR -> x -> ScannerReviewViewModel.LinkTableData.Accuracy.SIMILAR.equals(
                    x.accuracyProperty().get())
                    && ScannerReviewViewModel.LinkTableData.Problem.EDIT_NEEDED.equals(
                    x.problemProperty().get());
            case UNSELECT_ALL -> x -> true;
        };

        return linkTableDataLookup.values().stream().filter(predicate).toList();
    }

    private void updateLinkTableDataLink(ScannerReviewViewModel.LinkTableData linkTableData,
            ScannerReviewViewModel.LinkTableData.Problem problem, boolean selected) {
        SongData songData = scannerContext.scannerReviewService.getSongData(
                linkTableData.songIdProperty().get());

        linkTableData.captureImageProperty().setAll(songData.getAllLinkedCaptureData().stream()
                .map(ScannerReviewViewModel.LinkedCaptureData::from).toList());
        linkEditorSelectedLinkTableData.problemProperty().set(problem);
        linkEditorSelectedLinkTableData.selectedProperty().set(selected);

        updateLinkTableSelectedCountTextEventDebouncer.trigger();
        view.refreshLinkTable();
    }

    @Override
    public void startView() {
        runLinking();

        if (globalContext.configService.findScannerConfig().autoAnalysis()) {
            Platform.runLater(scannerProcessorStage::runAutoAnalysis);
        }
    }

    @Override
    public void runLinking() {
        scannerContext.scannerReviewService.relink();

        prepareLinkTableData();
        prepareLinkEditorData();
    }

    @Override
    public void updateLinkTableViewFilter() {
        if (filteredLinkTableDataList == null) {
            return;
        }

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
    public void toggleLinkTableSelected(ScannerProcessorReview.LinkTableToggleType type) {
        List<ScannerReviewViewModel.LinkTableData> list = findLinkTableDataToggleTarget(type);

        if (ScannerProcessorReview.LinkTableToggleType.UNSELECT_ALL.equals(type)) {
            list.forEach(x -> x.selectedProperty().set(false));
            return;
        }

        boolean value =
                list.stream().filter(x -> x.selectedProperty().get()).count() != list.size();
        list.forEach(x -> x.selectedProperty().set(value));
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
        if (linkEditorSelectedLinkTableData == null || filteredCaptureDataList == null) {
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

    @Override
    public void showAnalysisView() {
        List<Integer> selectedSongIdList =
                linkTableDataLookup.values().stream().filter(x -> x.selectedProperty().get())
                        .map(x -> x.songIdProperty().get()).toList();

        if (selectedSongIdList.isEmpty()) {
            scannerProcessorStage.showWarning(
                    Language.INSTANCE.getString("scanner.processor.review.dialog.noSongSelected"));
            return;
        }

        scannerProcessorStage.changeCenterView_analysis();
        scannerProcessorStage.runAnalysis(selectedSongIdList);
    }
}
