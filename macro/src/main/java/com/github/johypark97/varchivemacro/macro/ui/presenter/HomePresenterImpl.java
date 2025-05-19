package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;
import javafx.scene.Node;

public class HomePresenterImpl implements Home.HomePresenter {
    private final HomeStage homeStage;

    @MvpView
    public Home.HomeView view;

    public HomePresenterImpl(HomeStage homeStage) {
        this.homeStage = homeStage;
    }

    @Override
    public void startView() {
        homeStage.changeCenterView_modeSelector();
    }

    @Override
    public boolean stopView() {
        return true;
    }

    @Override
    public void requestStopStage() {
        homeStage.stopStage();
    }

    @Override
    public void setCenterView(Node value) {
        view.setCenterNode(value);
    }
}
