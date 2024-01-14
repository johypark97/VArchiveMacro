package com.github.johypark97.varchivemacro.dbmanager.fxgui.view;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomePresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component.HomeComponent;
import com.github.johypark97.varchivemacro.lib.common.mvp.AbstractMvpView;
import java.net.URL;
import java.util.Objects;
import javafx.scene.Scene;

public class HomeViewImpl extends AbstractMvpView<HomePresenter, HomeView> implements HomeView {
    private static final String TITLE = "Database Manager";

    private static final String GLOBAL_CSS_FILENAME = "global.css";

    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    private final HomeComponent homeComponent = new HomeComponent(this);

    public HomeViewImpl() {
        URL globalCss = HomeViewImpl.class.getResource(GLOBAL_CSS_FILENAME);
        Objects.requireNonNull(globalCss);

        Scene scene = new Scene(homeComponent);
        scene.getStylesheets().add(globalCss.toExternalForm());
        getStage().setScene(scene);

        getStage().setTitle(TITLE);

        getStage().setHeight(STAGE_HEIGHT);
        getStage().setWidth(STAGE_WIDTH);

        getStage().setMinHeight(STAGE_HEIGHT);
        getStage().setMinWidth(STAGE_WIDTH);

        getStage().setOnShowing(event -> {
            getPresenter().onLinkViewerTable(homeComponent.viewerTableView);
            getPresenter().onSetViewerTableFilterColumn(homeComponent.viewerFilterComboBox);
        });
    }

    public void updateViewerTableFilter() {
        String regex = homeComponent.viewerFilterTextField.getText();
        SongDataProperty property = homeComponent.viewerFilterComboBox.getValue();
        getPresenter().onUpdateViewerTableFilter(regex, property);
    }
}
