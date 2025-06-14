package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;

public class ModeSelectorPresenterImpl implements ModeSelector.ModeSelectorPresenter {
    private final HomeStage homeStage;

    @MvpView
    public ModeSelector.ModeSelectorView view;

    public ModeSelectorPresenterImpl(HomeStage homeStage) {
        this.homeStage = homeStage;
    }

    @Override
    public void startView() {
    }

    @Override
    public boolean stopView() {
        return true;
    }

    @Override
    public void showCollectionScannerView() {
        homeStage.changeCenterView_collectionScanner();
    }

    @Override
    public void showFreestyleMacroView() {
        homeStage.changeCenterView_freestyleMacro();
    }
}
