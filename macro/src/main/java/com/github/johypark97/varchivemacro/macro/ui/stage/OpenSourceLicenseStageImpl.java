package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.license.app.OpenSourceLicenseService;
import com.github.johypark97.varchivemacro.macro.common.license.app.OpenSourceLicenseStorageService;
import com.github.johypark97.varchivemacro.macro.common.license.domain.repository.OpenSourceLicenseRepository;
import com.github.johypark97.varchivemacro.macro.integration.provider.RepositoryProvider;
import com.github.johypark97.varchivemacro.macro.integration.provider.ServiceProvider;
import com.github.johypark97.varchivemacro.macro.ui.presenter.OpenSourceLicense;
import com.github.johypark97.varchivemacro.macro.ui.presenter.OpenSourceLicensePresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import com.github.johypark97.varchivemacro.macro.ui.view.OpenSourceLicenseViewImpl;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class OpenSourceLicenseStageImpl extends AbstractTreeableStage
        implements OpenSourceLicenseStage {
    private static final int STAGE_HEIGHT = 720;
    private static final int STAGE_WIDTH = 1280;

    private final Runnable onStop;

    private OpenSourceLicense.OpenSourceLicensePresenter presenter;

    public OpenSourceLicenseStageImpl(AbstractTreeableStage parent, Runnable onStop) {
        super(parent);

        this.onStop = onStop;

        setupStage();
    }

    private void setupStage() {
        stage.getIcons().add(new Image(UiResource.ICON.url().toString()));
        stage.setTitle(Language.INSTANCE.getString("osl.windowTitle"));

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT / 2.0);
        stage.setMinWidth(STAGE_WIDTH / 2.0);
    }

    @Override
    public void startStage() {
        OpenSourceLicenseRepository repository =
                RepositoryProvider.INSTANCE.createOpenSourceLicenseRepository();

        OpenSourceLicenseService openSourceLicenseService =
                ServiceProvider.INSTANCE.getOpenSourceLicenseService(repository);
        OpenSourceLicenseStorageService openSourceLicenseStorageService =
                ServiceProvider.INSTANCE.getOpenSourceLicenseStorageService(repository);

        presenter = new OpenSourceLicensePresenterImpl(openSourceLicenseService,
                openSourceLicenseStorageService, ServiceProvider.INSTANCE.getWebBrowserService());

        OpenSourceLicenseViewImpl view = new OpenSourceLicenseViewImpl();
        Mvp.linkViewAndPresenter(view, presenter);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> presenter.startView());

        stage.show();
    }

    @Override
    public void focusStage() {
        stage.requestFocus();
    }

    @Override
    protected boolean onStopStage() {
        onStop.run();

        return true;
    }
}
