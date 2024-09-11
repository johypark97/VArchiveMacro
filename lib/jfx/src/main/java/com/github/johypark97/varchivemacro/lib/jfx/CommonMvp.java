package com.github.johypark97.varchivemacro.lib.jfx;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp.MvpView;
import javafx.stage.Window;

public interface CommonMvp {
    interface CommonPresenter<V extends MvpView<V, P>, P extends MvpPresenter<V, P>>
            extends MvpPresenter<V, P> {
        void onStartView();

        void onStopView();
    }


    interface CommonView<V extends MvpView<V, P>, P extends MvpPresenter<V, P>>
            extends MvpView<V, P> {
        Window getWindow();
    }
}
