package com.github.johypark97.varchivemacro.macro.gui.presenter;

public interface ILicense {
    interface Presenter {
        void start();

        void stop();

        void showLicense(String key);

        void viewOpened();
    }


    interface View {
        void setPresenter(Presenter presenter);

        void showView();

        void disposeView();

        void setList(String... list);

        void showText(String text);
    }
}
