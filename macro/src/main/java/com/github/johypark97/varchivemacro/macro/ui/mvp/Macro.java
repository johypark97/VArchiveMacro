package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;

public interface Macro {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        boolean stopView();

        void updateCount(int value);

        void decreaseCount10();

        void decreaseCount1();

        void increaseCount1();

        void increaseCount10();

        void showHome();
    }


    interface View extends Mvp.MvpView<View, Presenter> {
        void setupCountSlider(int value, int defaultValue, int min, int max);

        void setCount(int value);

        void setClientModeText(String value);

        void setUploadKeyText(String value);

        void setStartUpKeyText(String value);

        void setStartDownKeyText(String value);

        void setStopKeyText(String value);

        void showProgressBox();

        void hideProgressBox();

        void setProgress(int value, int max);
    }
}
