package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpPresenter;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.CaptureData;
import com.github.johypark97.varchivemacro.macro.fxgui.model.manager.ScanDataManager.SongData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditor.LinkEditorPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditor.LinkEditorView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.LinkEditor.StartData;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class LinkEditorPresenterImpl
        extends AbstractMvpPresenter<LinkEditorPresenter, LinkEditorView>
        implements LinkEditorPresenter {
    private WeakReference<ScannerModel> scannerModelReference;

    private StartData startData;

    public void linkModel(ScannerModel scannerModel) {
        scannerModelReference = new WeakReference<>(scannerModel);
    }

    private ScannerModel getScannerModel() {
        return scannerModelReference.get();
    }

    @Override
    public StartData getStartData() {
        return startData;
    }

    @Override
    public void setStartData(StartData value) {
        startData = value;
    }

    @Override
    public void onViewShown() {
        SongData songData = getScannerModel().getSongData(startData.songDataId);
        LocalDlcSong song = songData.songProperty().get();

        getView().setSongText(String.format("[%s] %s - %s", song.dlc, song.title, song.composer));
    }

    @Override
    public List<CaptureData> onShowCaptureDataList(boolean findAll) {
        return findAll
                ? getScannerModel().copyCaptureDataList()
                : getScannerModel().getSongData(startData.songDataId).childListProperty();
    }

    @Override
    public Image onShowCaptureImage(int captureDataId) {
        try {
            return SwingFXUtils.toFXImage(
                    getScannerModel().getCaptureImage(startData.cacheDirectoryPath, captureDataId),
                    null);
        } catch (IOException e) {
            getView().showError("Cache image loading error", e);
            return null;
        }
    }

    @Override
    public boolean onLinkCaptureData(int captureDataId) {
        CaptureData captureData = getScannerModel().getCaptureData(captureDataId);

        String header = Language.getInstance().getString("linkEditor.dialog.link.header");
        String content = String.format("(%d) %s", captureData.idProperty().get(),
                captureData.scannedTitle.get());
        if (!getView().showConfirmation(header, content)) {
            return false;
        }

        SongData songData = getScannerModel().getSongData(startData.songDataId);
        songData.selected.set(true);

        List<CaptureData> childList = List.copyOf(songData.childListProperty());
        childList.forEach(songData::unlink);
        songData.link(captureData);

        songData.linkChanged.set(true);
        songData.linkExact.set(false);

        startData.onLinkUpdate.run();

        return true;
    }

    @Override
    public boolean onUnlinkCaptureData() {
        String header = Language.getInstance().getString("linkEditor.dialog.unlink.header");
        if (!getView().showConfirmation(header, null)) {
            return false;
        }

        SongData songData = getScannerModel().getSongData(startData.songDataId);
        songData.selected.set(false);

        List<CaptureData> childList = List.copyOf(songData.childListProperty());
        childList.forEach(songData::unlink);

        songData.linkChanged.set(false);
        songData.linkExact.set(false);

        startData.onLinkUpdate.run();

        return true;
    }

    @Override
    protected LinkEditorPresenter getInstance() {
        return this;
    }

    @Override
    protected boolean initialize() {
        Objects.requireNonNull(startData);
        Objects.requireNonNull(startData.cacheDirectoryPath);
        Objects.requireNonNull(startData.onLinkUpdate);
        Objects.requireNonNull(startData.ownerWindow);

        return true;
    }

    @Override
    protected boolean terminate() {
        getView().onStop();

        return true;
    }
}
