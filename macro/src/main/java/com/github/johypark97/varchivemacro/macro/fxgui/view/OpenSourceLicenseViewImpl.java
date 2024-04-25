package com.github.johypark97.varchivemacro.macro.fxgui.view;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicensePresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.OpenSourceLicense.OpenSourceLicenseView;
import com.github.johypark97.varchivemacro.macro.fxgui.view.component.OpenSourceLicenseComponent;
import com.github.johypark97.varchivemacro.macro.fxgui.view.stage.OpenSourceLicenseStage;
import java.lang.ref.WeakReference;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class OpenSourceLicenseViewImpl
        extends AbstractMvpView<OpenSourceLicensePresenter, OpenSourceLicenseView>
        implements OpenSourceLicenseView {
    private WeakReference<OpenSourceLicenseComponent> openSourceLicenseComponentReference;

    private OpenSourceLicenseComponent getOpenSourceLicenseComponent() {
        return openSourceLicenseComponentReference.get();
    }

    @Override
    public void setLibraryList(ObservableList<String> list) {
        getOpenSourceLicenseComponent().setLibraryList(list);
    }

    @Override
    public void showLicenseText(String library) {
        String text = getPresenter().onShowLicenseText(library);
        getOpenSourceLicenseComponent().setLicenseText(text);
    }

    @Override
    public void showLibraryUrl(String library) {
        String url = getPresenter().onShowLibraryUrl(library);
        getOpenSourceLicenseComponent().setLibraryUrl(url);
    }

    @Override
    protected Stage newStage() {
        OpenSourceLicenseStage stage = new OpenSourceLicenseStage(this);

        openSourceLicenseComponentReference = new WeakReference<>(stage.openSourceLicenseComponent);

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(getPresenter().getStartData().ownerWindow);

        stage.setOnShown(event -> getPresenter().onViewShown());

        return stage;
    }
}
