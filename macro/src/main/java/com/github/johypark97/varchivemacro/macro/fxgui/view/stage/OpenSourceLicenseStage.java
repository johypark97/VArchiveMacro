package com.github.johypark97.varchivemacro.macro.fxgui.view.stage;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.fxgui.model.LicenseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicensePresenterImpl;
import com.github.johypark97.varchivemacro.macro.fxgui.view.OpenSourceLicenseViewImpl;
import java.net.URL;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Window;

public class OpenSourceLicenseStage extends AbstractCommonStage {
    private static final String TITLE = "Open Source License";

    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    private final OpenSourceLicenseViewImpl view = new OpenSourceLicenseViewImpl();

    public OpenSourceLicenseStage(Window owner, LicenseModel licenseModel, Runnable onStopStage) {
        super(onStopStage);

        setupView(licenseModel);
        setupStage(owner);
    }

    private void setupView(LicenseModel licenseModel) {
        OpenSourceLicensePresenterImpl presenter = new OpenSourceLicensePresenterImpl();
        presenter.linkModel(licenseModel);
        Mvp.linkViewAndPresenter(view, presenter);
    }

    private void setupStage(Window owner) {
        URL globalCss = GlobalResource.getGlobalCss();
        URL tableColorCss = GlobalResource.getTableColorCss();

        Scene scene = new Scene(view);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        setScene(scene);

        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);

        getIcons().add(new Image(GlobalResource.getIcon().toString()));
        setTitle(TITLE);

        setHeight(STAGE_HEIGHT);
        setWidth(STAGE_WIDTH);

        setMinHeight(STAGE_HEIGHT);
        setMinWidth(STAGE_WIDTH);

        setOnShown(event -> view.startView());
    }
}
