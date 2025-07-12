package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.ScannerScannerViewModel;
import java.util.List;
import java.util.Set;
import javafx.beans.value.ObservableStringValue;

public interface ScannerScanner {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        boolean stopView();

        void checkDisplayAndResolution();
    }


    interface View extends Mvp.MvpView<View, Presenter> {
        void bindAccountFileText(ObservableStringValue value);

        void bindCacheDirectoryText(ObservableStringValue value);

        void setAutoAnalysis(boolean value);

        void setStartKeyText(String value);

        void setStopKeyText(String value);

        void setDebugCheckBoxVisible(boolean value);

        boolean getDebugCheckBoxValue();

        void setCategoryList(List<ScannerScannerViewModel.CategoryData> value);

        List<ScannerScannerViewModel.CategoryData> getSelectedCategoryList();

        void setSelectedCategory(Set<String> value);

        void showProgressBox();

        void hideProgressBox();
    }
}
