package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.macro.gui.model.LicenseModel;
import java.io.IOException;

public class LicensePresenter implements ILicense.Presenter {
    // model
    public LicenseModel licenseModel;

    // view
    public final ILicense.View view;

    public LicensePresenter(ILicense.View view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    @Override
    public void start() {
        view.showView();
    }

    @Override
    public void stop() {
        view.disposeView();
    }

    @Override
    public void showLicense(String key) {
        String text;

        try {
            text = licenseModel.getText(key);
        } catch (IOException e) {
            text = e.getMessage();
        }

        view.showText(text);
    }

    @Override
    public void viewOpened() {
        view.setList(licenseModel.getList());
    }
}
