package com.github.johypark97.varchivemacro.macro.gui.presenter;

import com.github.johypark97.varchivemacro.macro.gui.model.LicenseModel;
import com.github.johypark97.varchivemacro.macro.gui.presenter.ILicense.Presenter;
import com.github.johypark97.varchivemacro.macro.gui.presenter.ILicense.View;
import javax.swing.JFrame;

public class LicensePresenter implements Presenter {
    // model
    private final LicenseModel licenseModel = new LicenseModel();

    // view
    private final Class<? extends View> viewClass;
    public View view;

    public LicensePresenter(Class<? extends View> viewClass) {
        this.viewClass = viewClass;
    }

    private void newView(JFrame parent) {
        try {
            view = viewClass.getConstructor(JFrame.class).newInstance(parent);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }

        view.setPresenter(this);
    }

    @Override
    public synchronized void start(JFrame parent) {
        if (view != null) {
            return;
        }
        newView(parent);

        view.setLicenseList(licenseModel.getList());

        view.showView();
    }

    @Override
    public synchronized void viewClosed() {
        if (view != null) {
            view = null; // NOPMD
        }
    }

    @Override
    public void getLicense(String key) {
        try {
            view.setLicenseText(licenseModel.getText(key));
            view.setLicenseUrl(licenseModel.getUrl(key));
        } catch (Exception e) {
            view.setLicenseText(e.getMessage());
            view.setLicenseUrl("");
        }
    }
}
