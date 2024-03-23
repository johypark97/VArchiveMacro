package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpPresenter;
import com.github.johypark97.varchivemacro.lib.scanner.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerRecordData;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.ViewerTreeData;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomePresenterImpl extends AbstractMvpPresenter<HomePresenter, HomeView>
        implements HomePresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePresenterImpl.class);

    private final Function<String, String> VIEWER_TITLE_NORMALIZER =
            x -> Normalizer.normalize(x.toLowerCase(Locale.ENGLISH), Form.NFKD);

    private WeakReference<ConfigModel> configModelReference;
    private WeakReference<DatabaseModel> databaseModelReference;
    private WeakReference<RecordModel> recordModelReference;

    public void linkModel(ConfigModel configModel, DatabaseModel databaseModel,
            RecordModel recordModel) {
        configModelReference = new WeakReference<>(configModel);
        databaseModelReference = new WeakReference<>(databaseModel);
        recordModelReference = new WeakReference<>(recordModel);
    }

    private ConfigModel getConfigModel() {
        return configModelReference.get();
    }

    private DatabaseModel getDatabaseModel() {
        return databaseModelReference.get();
    }

    private RecordModel getRecordModel() {
        return recordModelReference.get();
    }

    @Override
    public boolean onViewShow_setup() {
        try {
            getDatabaseModel().load();
        } catch (IOException e) {
            getView().getScannerFrontController().showForbiddenMark();
            getView().showError("Database loading error", e);
            LOGGER.atError().log("DatabaseModel loading exception", e);
            return false;
        } catch (Exception e) {
            getView().getScannerFrontController().showForbiddenMark();
            getView().showError("Critical database loading error", e);
            LOGGER.atError().log("Critical DatabaseModel loading exception", e);
            throw e;
        }

        getView().scanner_scanner_setTabList(getDatabaseModel().getDlcTabList());

        Set<String> selectedTabSet = getConfigModel().getScannerConfig().selectedTabSet;
        getView().scanner_scanner_setSelectedTabSet(selectedTabSet);

        return true;
    }

    @Override
    public void onViewShow_loadRecord() {
        try {
            if (!getRecordModel().loadLocal()) {
                getView().getScannerFrontController().showDjNameInput();
                return;
            }
        } catch (IOException e) {
            getView().getScannerFrontController().showDjNameInput();
            getView().showError("Local records loading error", e);
            LOGGER.atError().log("RecordModel loading exception", e);
            return;
        } catch (Exception e) {
            getView().getScannerFrontController().showDjNameInput();
            getView().showError("Critical local records loading error", e);
            LOGGER.atError().log("Critical RecordModel loading exception", e);
            throw e;
        }

        getView().getScannerFrontController().showScanner();
    }

    @Override
    public void scanner_front_onLoadRemoteRecord(String djName) {
        getView().getScannerFrontController().hideDjNameInputError();

        if (djName.isBlank()) {
            getView().getScannerFrontController().showDjNameInputError("DJ Name is blank.");
            return;
        }

        getView().getScannerFrontController().showLoadingMark(djName);

        BiConsumer<String, Exception> onThrow = getView()::showError;
        Consumer<Boolean> onDone = x -> {
            getView().getScannerFrontController().hideLoadingMark();

            if (Boolean.FALSE.equals(x)) {
                getView().getScannerFrontController().showDjNameInput();
                return;
            }

            getView().getScannerFrontController().hideDjNameInput();
            getView().getScannerFrontController().showScanner();
        };

        getRecordModel().loadRemote(djName, onDone, onThrow);
    }

    @Override
    public void scanner_viewer_onShowSongTree(TreeView<ViewerTreeData> treeView, String filter) {
        String normalizedFilter;
        if (filter == null || filter.isBlank()) {
            normalizedFilter = null; // NOPMD
        } else {
            normalizedFilter = VIEWER_TITLE_NORMALIZER.apply(filter.trim());
        }

        TreeItem<ViewerTreeData> rootNode = new TreeItem<>();
        getDatabaseModel().getDlcTapSongMap().forEach((tab, songList) -> {
            TreeItem<ViewerTreeData> dlcNode = new TreeItem<>(new ViewerTreeData(tab));
            dlcNode.setExpanded(normalizedFilter != null);
            rootNode.getChildren().add(dlcNode);

            Stream<LocalDlcSong> stream = songList.stream();
            if (normalizedFilter != null) {
                stream = stream.filter(
                        x -> VIEWER_TITLE_NORMALIZER.apply(x.title).contains(normalizedFilter));
            }
            stream.forEach(
                    song -> dlcNode.getChildren().add(new TreeItem<>(new ViewerTreeData(song))));
        });

        treeView.setRoot(rootNode);
    }

    @Override
    public ViewerRecordData scanner_viewer_onShowRecord(int id) {
        ViewerRecordData data = new ViewerRecordData();

        LocalDlcSong song = getDatabaseModel().getDlcSong(id);
        data.composer = song.composer;
        data.title = song.title;

        getRecordModel().getRecordList(id).forEach(x -> {
            int column = x.pattern.getWeight();
            int row = x.button.getWeight();

            data.maxCombo[row][column] = x.maxCombo;
            data.rate[row][column] = x.rate;
        });

        return data;
    }

    @Override
    protected HomePresenter getInstance() {
        return this;
    }

    @Override
    protected boolean initialize() {
        try {
            if (!getConfigModel().load()) {
                getConfigModel().save();
            }
        } catch (IOException ignored) {
        }

        return true;
    }

    @Override
    protected boolean terminate() {
        ScannerConfig scannerConfig = new ScannerConfig();
        scannerConfig.selectedTabSet = getView().scanner_scanner_getSelectedTabSet();

        getConfigModel().setScannerConfig(scannerConfig);

        try {
            getConfigModel().save();
        } catch (IOException ignored) {
        }

        return true;
    }
}
