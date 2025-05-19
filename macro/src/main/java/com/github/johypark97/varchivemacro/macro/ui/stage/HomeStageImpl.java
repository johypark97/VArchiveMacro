package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.infrastructure.resource.BuildInfo;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Home;
import com.github.johypark97.varchivemacro.macro.ui.presenter.HomePresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ModeSelector;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ModeSelectorPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.view.HomeViewImpl;
import com.github.johypark97.varchivemacro.macro.ui.view.ModeSelectorViewImpl;
import java.awt.Toolkit;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class HomeStageImpl implements HomeStage {
    private static final String TITLE = "VArchive Macro";

    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    private final Stage stage;

    private Home.HomePresenter homePresenter;
    private ModeSelector.ModeSelectorPresenter modeSelectorPresenter;

    public HomeStageImpl(Stage stage) {
        this.stage = stage;

        setupStage();
    }

    private void setupStage() {
        stage.getIcons().add(new Image(UiResource.ICON.url().toString()));
        stage.setTitle(String.format("%s v%s", TITLE, BuildInfo.version));

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT);
        stage.setMinWidth(STAGE_WIDTH);
    }

    @Override
    public void startStage() {
        HomeViewImpl homeView = new HomeViewImpl();

        homePresenter = new HomePresenterImpl(this);
        Mvp.linkViewAndPresenter(homeView, homePresenter);

        Scene scene = new Scene(homeView);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> homePresenter.startView());
        Mvp.hookWindowCloseRequest(stage, event -> stopStage());

        stage.show();
    }

    @Override
    public void stopStage() {
        if (modeSelectorPresenter != null && !modeSelectorPresenter.stopView()) {
            return;
        }

        if (!homePresenter.stopView()) {
            return;
        }

        stage.hide();
    }

    @Override
    public void showError(String header, Throwable throwable) {
        Alert alert = AlertBuilder.error().setOwner(stage).setHeaderText(header)
                .setContentText(throwable.toString()).setThrowable(throwable).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public void showInformation(String header, String content) {
        Alert alert = AlertBuilder.information().setOwner(stage).setHeaderText(header)
                .setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public boolean showConfirmation(String header, String content) {
        Alert alert = AlertBuilder.confirmation().setOwner(stage).setHeaderText(header)
                .setContentText(content).alert;

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();

        return ButtonType.OK.equals(alert.getResult());
    }

    @Override
    public void changeCenterView_modeSelector() {
        ModeSelectorViewImpl view = new ModeSelectorViewImpl();

        modeSelectorPresenter = new ModeSelectorPresenterImpl(this);
        Mvp.linkViewAndPresenter(view, modeSelectorPresenter);

        homePresenter.setCenterView(view);

        modeSelectorPresenter.startView();
    }
}
