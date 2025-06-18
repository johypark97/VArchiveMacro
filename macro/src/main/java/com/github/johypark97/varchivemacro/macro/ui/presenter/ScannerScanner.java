package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.ScannerScannerViewModel;
import java.util.List;
import java.util.Set;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.image.Image;

public interface ScannerScanner {
    interface ScannerScannerPresenter
            extends Mvp.MvpPresenter<ScannerScannerView, ScannerScannerPresenter> {
        void startView();

        boolean stopView();

        void checkDisplayAndResolution();
    }


    interface ScannerScannerView extends Mvp.MvpView<ScannerScannerView, ScannerScannerPresenter> {
        void bindAccountFileText(ObservableStringValue value);

        void bindCacheDirectoryText(ObservableStringValue value);

        void setAutoAnalysis(boolean value);

        void setStartKeyText(String value);

        void setStopKeyText(String value);

        void setCheckerImageViewImage(Image value);

        void setCategoryList(List<ScannerScannerViewModel.CategoryData> value);

        List<ScannerScannerViewModel.CategoryData> getSelectedCategoryList();

        void setSelectedCategory(Set<String> value);

        void showProgressBox();

        void hideProgressBox();
    }
}
