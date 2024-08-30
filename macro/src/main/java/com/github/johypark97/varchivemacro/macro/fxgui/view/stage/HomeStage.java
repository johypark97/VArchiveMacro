package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultConfigModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultDatabaseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultLicenseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultMacroModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultRecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.DefaultScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.LicenseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.MacroModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.RecordModel;
import com.github.johypark97.varchivemacro.macro.fxgui.model.ScannerModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.HomePresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.view.HomeViewImpl;
import com.github.johypark97.varchivemacro.macro.resource.BuildInfo;
import java.net.URL;
import java.nio.file.Path;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class HomeStage extends AbstractCommonStage {
    private static final String TITLE = "VArchive Macro";

    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    private final HomeViewImpl view = new HomeViewImpl(this);

    private final ConfigModel configModel = new DefaultConfigModel();
    private final DatabaseModel databaseModel = new DefaultDatabaseModel();
    private final LicenseModel licenseModel = new DefaultLicenseModel();
    private final RecordModel recordModel = new DefaultRecordModel();
    private final ScannerModel scannerModel = new DefaultScannerModel();
    private final MacroModel macroModel = new DefaultMacroModel();

    private AnalysisDataViewerStage analysisDataViewerStage;
    private CaptureViewerStage captureViewerStage;
    private LinkEditorStage linkEditorStage;
    private OpenSourceLicenseStage openSourceLicenseStage;

    public HomeStage() {
        super(null);

        setupView();
        setupStage();
    }

    public void showOpenSourceLicense() {
        if (openSourceLicenseStage == null) {
            openSourceLicenseStage = new OpenSourceLicenseStage(this, licenseModel, () -> {
                openSourceLicenseStage = null; // NOPMD
            });
        }

        openSourceLicenseStage.show();
    }

    public void showCaptureViewer(WritableImage image) {
        if (captureViewerStage == null) {
            captureViewerStage = new CaptureViewerStage(this, () -> {
                captureViewerStage = null; // NOPMD
            });
        }

        captureViewerStage.showStage(image);
    }

    public void showLinkEditor(Path cacheDirectoryPath, int songDataId, Runnable onUpdateLink) {
        if (linkEditorStage == null) {
            linkEditorStage = new LinkEditorStage(this, scannerModel, () -> {
                linkEditorStage = null; // NOPMD
            });
        }

        linkEditorStage.showStage(cacheDirectoryPath, songDataId, onUpdateLink);
    }

    public void showAnalysisDataViewer(Path cacheDirectoryPath, int analysisDataId) {
        if (analysisDataViewerStage == null) {
            analysisDataViewerStage = new AnalysisDataViewerStage(this, scannerModel, () -> {
                analysisDataViewerStage = null; // NOPMD
            });
        }

        analysisDataViewerStage.showStage(cacheDirectoryPath, analysisDataId);
    }

    private void setupView() {
        HomePresenterImpl presenter = new HomePresenterImpl();
        presenter.linkModel(configModel, databaseModel, recordModel, scannerModel, macroModel);
        Mvp.linkViewAndPresenter(view, presenter);
    }

    private void setupStage() {
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        Scene scene = new Scene(view);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        setScene(scene);

        getIcons().add(new Image(GlobalResource.getIcon().toString()));
        setTitle(String.format("%s v%s", TITLE, BuildInfo.version));

        setHeight(STAGE_HEIGHT);
        setWidth(STAGE_WIDTH);

        setMinHeight(STAGE_HEIGHT);
        setMinWidth(STAGE_WIDTH);

        setOnShown(event -> view.startView());
    }

    @Override
    public void stopStage() {
        if (captureViewerStage != null) {
            captureViewerStage.hide();
        }

        if (linkEditorStage != null) {
            linkEditorStage.hide();
        }

        view.stopView();

        super.stopStage();
    }
}
