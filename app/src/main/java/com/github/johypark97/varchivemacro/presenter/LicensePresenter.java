package com.github.johypark97.varchivemacro.presenter;

import java.io.IOException;
import com.github.johypark97.varchivemacro.model.LicenseModel;

public class LicensePresenter implements ILicense.Presenter {
    public ILicense.View view;
    public LicenseModel model;

    @Override
    public void showLicense(String key) {
        String text;

        try {
            text = model.getText(key);
        } catch (IOException e) {
            text = e.getMessage();
        }


        view.showText(text);
    }

    @Override
    public void viewOpened() {
        view.setList(model.getList());
    }
}
