package com.github.johypark97.varchivemacro.macro.fxgui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.model.LicenseModel;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicensePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicenseView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.StartData;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import javafx.collections.FXCollections;

public class OpenSourceLicensePresenterImpl
        extends AbstractMvpPresenter<OpenSourceLicensePresenter, OpenSourceLicenseView>
        implements OpenSourceLicensePresenter {
    private WeakReference<LicenseModel> licenseModelReference;

    private StartData startData;

    public void linkModel(LicenseModel licenseModel) {
        licenseModelReference = new WeakReference<>(licenseModel);
    }

    private LicenseModel getLicenseModel() {
        return licenseModelReference.get();
    }

    @Override
    public StartData getStartData() {
        return startData;
    }

    @Override
    public void setStartData(StartData value) {
        startData = value;
    }

    @Override
    public void onViewShown() {
        List<String> libraryList = getLicenseModel().getLibraryList();
        getView().setLibraryList(FXCollections.observableList(libraryList));
    }

    @Override
    public String onShowLicenseText(String library) {
        try {
            return getLicenseModel().getLicenseText(library);
        } catch (IOException e) {
            return "Resource IO Error";
        }
    }

    @Override
    public String onShowLibraryUrl(String library) {
        return getLicenseModel().getLibraryUrl(library);
    }

    @Override
    protected OpenSourceLicensePresenter getInstance() {
        return this;
    }
}
