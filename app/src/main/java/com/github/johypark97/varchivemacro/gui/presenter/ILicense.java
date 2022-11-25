package com.github.johypark97.varchivemacro.gui.presenter;

public interface ILicense {
    interface Presenter {
        void showLicense(String key);

        void viewOpened();
    }

    interface View {
        void setList(String[] list);

        void showText(String text);
    }
}
