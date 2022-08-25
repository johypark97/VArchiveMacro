package com.github.johypark97.varchivemacro.presenter;

import java.io.IOException;
import com.github.johypark97.varchivemacro.model.LicenseModel;

public class LicensePresenter implements ILicense.Presenter {
    // model
    public LicenseModel licenseModel;

    // view
    public ILicense.View licenseView;

    @Override
    public void showLicense(String key) {
        String text;

        try {
            text = licenseModel.getText(key);
        } catch (IOException e) {
            text = e.getMessage();
        }

        licenseView.showText(text);
    }

    @Override
    public void viewOpened() {
        licenseView.setList(licenseModel.getList());
    }
}
