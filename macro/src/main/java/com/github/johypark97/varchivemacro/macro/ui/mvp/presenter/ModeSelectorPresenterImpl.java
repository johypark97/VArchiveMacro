package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.macro.ui.mvp.ModeSelector;
import com.github.johypark97.varchivemacro.macro.ui.stage.HomeStage;

public class ModeSelectorPresenterImpl implements ModeSelector.Presenter {
    private final HomeStage homeStage;

    @MvpView
    public ModeSelector.View view;

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
