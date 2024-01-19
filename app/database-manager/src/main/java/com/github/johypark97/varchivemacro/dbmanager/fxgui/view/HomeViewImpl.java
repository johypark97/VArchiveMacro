package com.github.johypark97.varchivemacro.dbmanager.fxgui.view;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component.HomeComponent;
import com.github.johypark97.varchivemacro.lib.common.mvp.AbstractMvpView;
import java.net.URL;
import java.util.Objects;
import javafx.application.Platform;
import javafx.scene.Scene;

public class HomeViewImpl extends AbstractMvpView<HomePresenter, HomeView> implements HomeView {
    private static final String TITLE = "Database Manager";

    private static final String GLOBAL_CSS_FILENAME = "global.css";
    private static final String TABLE_COLOR_CSS_FILENAME = "table-color.css";

    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    private final HomeComponent homeComponent = new HomeComponent(this);

    public HomeViewImpl() {
        URL globalCss = HomeViewImpl.class.getResource(GLOBAL_CSS_FILENAME);
        Objects.requireNonNull(globalCss);

        URL tableColorCss = HomeViewImpl.class.getResource(TABLE_COLOR_CSS_FILENAME);
        Objects.requireNonNull(tableColorCss);

        Scene scene = new Scene(homeComponent);
        scene.getStylesheets().add(globalCss.toExternalForm());
        scene.getStylesheets().add(tableColorCss.toExternalForm());
        getStage().setScene(scene);

        getStage().setTitle(TITLE);

        getStage().setHeight(STAGE_HEIGHT);
        getStage().setWidth(STAGE_WIDTH);

        getStage().setMinHeight(STAGE_HEIGHT);
        getStage().setMinWidth(STAGE_WIDTH);

        getStage().setOnShowing(event -> {
            getPresenter().onLinkViewerTable(homeComponent.viewerTableView);
            getPresenter().onSetViewerTableFilterColumn(homeComponent.viewerFilterComboBox);

            getPresenter().onLinkOcrTesterTable(homeComponent.ocrTesterTableView);
        });
    }

    public void updateViewerTableFilter() {
        String regex = homeComponent.viewerFilterTextField.getText();
        SongDataProperty property = homeComponent.viewerFilterComboBox.getValue();
        getPresenter().onUpdateViewerTableFilter(regex, property);
    }

    public void validateDatabase() {
        getPresenter().onValidateDatabase();
    }

    public void compareDatabaseWithRemote() {
        getPresenter().onCompareDatabaseWithRemote();
    }

    public void showOcrTesterCacheDirectorySelector() {
        getPresenter().onShowOcrTesterCacheDirectorySelector(getStage());
    }

    public void showOcrTesterTessdataDirectorySelector() {
        getPresenter().onShowOcrTesterTessdataDirectorySelector(getStage());
    }

    public void startOcrTester() {
        String cacheDirectory = homeComponent.ocrTesterCacheDirectoryTextField.getText();
        String tessdataDirectory = homeComponent.ocrTesterTessdataDirectoryTextField.getText();
        String tessdataLanguage = homeComponent.ocrTesterTessdataLanguageTextField.getText();

        getPresenter().onStartOcrTester(cacheDirectory, tessdataDirectory, tessdataLanguage);
    }

    public void stopOcrTester() {
        getPresenter().onStopOcrTester();
    }

    @Override
    public void setCheckerTextAreaText(String value) {
        Platform.runLater(() -> homeComponent.checkerTextArea.setText(value));
    }

    @Override
    public void setOcrTesterCacheDirectoryText(String value) {
        Platform.runLater(() -> homeComponent.ocrTesterCacheDirectoryTextField.setText(value));
    }

    @Override
    public void setOcrTesterTessdataDirectoryText(String value) {
        Platform.runLater(() -> homeComponent.ocrTesterTessdataDirectoryTextField.setText(value));
    }

    @Override
    public void updateOcrTesterProgressIndicator(double value) {
        Platform.runLater(() -> {
            homeComponent.ocrTesterProgressBar.setProgress(value);
            homeComponent.ocrTesterProgressLabel.setText(
                    (value >= 0 && value <= 1) ? String.format("%.2f%%", value * 100) : "");
        });
    }
}
