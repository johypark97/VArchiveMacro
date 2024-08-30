package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.macro.fxgui.model.LicenseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicensePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicenseView;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import javafx.collections.FXCollections;

public class OpenSourceLicensePresenterImpl implements OpenSourceLicensePresenter {
    private WeakReference<LicenseModel> licenseModelReference;

    @MvpView
    public OpenSourceLicenseView view;

    public void linkModel(LicenseModel licenseModel) {
        licenseModelReference = new WeakReference<>(licenseModel);
    }

    private LicenseModel getLicenseModel() {
        return licenseModelReference.get();
    }

    @Override
    public void onStartView() {
        List<String> libraryList = getLicenseModel().getLibraryList();
        view.setLibraryList(FXCollections.observableList(libraryList));
    }

    @Override
    public void showLicense(String library) {
        String licenseText;
        try {
            licenseText = getLicenseModel().getLicenseText(library);
        } catch (IOException e) {
            licenseText = "Resource IO Error";
        }

        view.setLicenseText(licenseText);
        view.setLicenseUrl(getLicenseModel().getLibraryUrl(library));
    }
}
