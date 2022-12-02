package com.github.johypark97.varchivemacro.gui.presenter;

public interface ILicense {
    interface Presenter {
        void start();

        void showLicense(String key);

        void viewOpened();
    }

    interface View {
        void setPresenter(Presenter presenter);

        void showView();

        void setList(String[] list);

        void showText(String text);
    }
}
