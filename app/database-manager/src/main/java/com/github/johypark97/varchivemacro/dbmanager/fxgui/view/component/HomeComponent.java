package com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.HomeViewImpl;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpFxml;
import java.net.URL;
import javafx.scene.control.TabPane;

public class HomeComponent extends TabPane {
    private static final String FXML_FILENAME = "Home.fxml";

    public final HomeViewImpl view;

    public HomeComponent(HomeViewImpl view) {
        this.view = view;

        URL url = HomeComponent.class.getResource(FXML_FILENAME);
        MvpFxml.loadRoot(this, url);
    }
}
