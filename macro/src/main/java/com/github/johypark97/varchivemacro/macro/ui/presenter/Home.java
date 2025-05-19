package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import javafx.scene.Node;

public interface Home {
    interface HomePresenter extends Mvp.MvpPresenter<HomeView, HomePresenter> {
        void startView();

        boolean stopView();

        void requestStopStage();

        void setCenterView(Node value);
    }


    interface HomeView extends Mvp.MvpView<HomeView, HomePresenter> {
        void setCenterNode(Node value);
    }
}
