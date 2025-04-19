package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.scanner.database.SongDatabase.Song;
import com.github.johypark97.varchivemacro.lib.scanner.database.TitleTool;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.CaptureData;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.SongData;
import com.github.johypark97.varchivemacro.macro.provider.ServiceProvider;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import com.github.johypark97.varchivemacro.macro.service.CollectionScanService;
import com.github.johypark97.varchivemacro.macro.ui.presenter.LinkEditor.LinkEditorPresenter;
import com.github.johypark97.varchivemacro.macro.ui.presenter.LinkEditor.LinkEditorView;
import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.embed.swing.SwingFXUtils;

public class LinkEditorPresenterImpl implements LinkEditorPresenter {
    private static final Function<String, String> NORMALIZER = x -> Normalizer.normalize(
            TitleTool.normalizeTitle_recognition(x).toLowerCase(Locale.ENGLISH),
            Normalizer.Form.NFKD);

    private final ServiceProvider serviceProvider;

    private final Runnable onUpdateLink;
    private final int songDataId;

    private FilteredList<CaptureData> filteredCaptureDataList;

    @MvpView
    public LinkEditorView view;

    public LinkEditorPresenterImpl(ServiceProvider serviceProvider, int songDataId,
            Runnable onUpdateLink) {
        this.serviceProvider = serviceProvider;

        this.onUpdateLink = onUpdateLink;
        this.songDataId = songDataId;
    }

    @Override
    public void onStartView() {
        CollectionScanService collectionScanService = serviceProvider.getScannerService();

        Song song = collectionScanService.getSongData(songDataId).songProperty().get();
        view.setSongText(
                String.format("[%s] %s - %s", song.pack().name(), song.title(), song.composer()));

        showCaptureDataList(null, false);
    }

    @Override
    public void onStopView() {
        view.getWindow().hide();
    }

    @Override
    public void updateCaptureDataListFilter(String pattern) {
        if (pattern == null) {
            return;
        }

        String normalizedPattern = NORMALIZER.apply(pattern.trim());

        filteredCaptureDataList.setPredicate(
                x -> NORMALIZER.apply(x.scannedTitle.get()).contains(normalizedPattern));
    }

    @Override
    public void showCaptureDataList(String pattern, boolean findAll) {
        CollectionScanService collectionScanService = serviceProvider.getScannerService();

        ObservableList<CaptureData> list = findAll
                ? FXCollections.observableArrayList(collectionScanService.copyCaptureDataList())
                : collectionScanService.getSongData(songDataId).childListProperty();

        filteredCaptureDataList = new FilteredList<>(list);

        updateCaptureDataListFilter(pattern);

        view.setCaptureDataList(filteredCaptureDataList);
    }

    @Override
    public void showCaptureImage(int captureDataId) {
        CollectionScanService collectionScanService = serviceProvider.getScannerService();

        try {
            view.setCaptureImage(
                    SwingFXUtils.toFXImage(collectionScanService.getCaptureImage(captureDataId),
                            null));
        } catch (IOException e) {
            view.showError("Cache image loading error", e);
        }
    }

    @Override
    public void linkCaptureData(int captureDataId) {
        CollectionScanService collectionScanService = serviceProvider.getScannerService();

        CaptureData captureData = collectionScanService.getCaptureData(captureDataId);

        String header = Language.getInstance().getString("linkEditor.dialog.link.header");
        String content = String.format("(%d) %s", captureData.idProperty().get(),
                captureData.scannedTitle.get());
        if (!view.showConfirmation(header, content)) {
            return;
        }

        SongData songData = collectionScanService.getSongData(songDataId);
        songData.selected.set(true);

        List<CaptureData> childList = List.copyOf(songData.childListProperty());
        childList.forEach(songData::unlink);
        songData.link(captureData);

        songData.linkChanged.set(true);
        songData.linkExact.set(false);

        onUpdateLink.run();

        onStopView();
    }

    @Override
    public void unlinkCaptureData() {
        CollectionScanService collectionScanService = serviceProvider.getScannerService();

        String header = Language.getInstance().getString("linkEditor.dialog.unlink.header");
        if (!view.showConfirmation(header, null)) {
            return;
        }

        SongData songData = collectionScanService.getSongData(songDataId);
        songData.selected.set(false);

        List<CaptureData> childList = List.copyOf(songData.childListProperty());
        childList.forEach(songData::unlink);

        songData.linkChanged.set(false);
        songData.linkExact.set(false);

        onUpdateLink.run();

        onStopView();
    }
}
