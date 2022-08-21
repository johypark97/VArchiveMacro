package com.github.johypark97.varchivemacro.presenter;

public interface ILicense {
    public interface Presenter {
        void showLicense(String key);

        void viewOpened();
    }

    public interface View {
        void setList(String[] list);

        void showText(String text);
    }
}
