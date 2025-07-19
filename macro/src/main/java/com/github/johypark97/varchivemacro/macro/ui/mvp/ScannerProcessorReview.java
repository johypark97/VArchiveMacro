package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerReviewViewModel;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

public interface ScannerProcessorReview {
    enum LinkTableToggleType {
        EDITED,
        EXACT,
        SIMILAR,
        UNSELECT_ALL
    }


    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void runLinking();

        void updateLinkTableViewFilter();

        void toggleLinkTableSelected(LinkTableToggleType type);

        void showLinkEditor(int songId);

        void hideLinkEditor();

        void showLinkEditorImage(int captureEntryId);

        void updateLinkEditorCaptureImageFilter();

        void linkSongAndCapture(int captureEntryId);

        void unlinkSongAndCapture();

        void showAnalysisView();
    }


    interface View
            extends Mvp.MvpView<View, Presenter>, ScannerProcessorFrame.ViewButtonController {
        /**
         * @param value A non-filtered list. If not, the element counts displayed on the filter
         *              checkboxes will not be set properly.
         */
        void setLinkTableItemList(ObservableList<ScannerReviewViewModel.LinkTableData> value);

        void updateLinkTableSelectedCountText(int exactSelected, int exactTotal,
                int similarSelected, int similarTotal, int editedSelected, int editedTotal);

        boolean getLinkTableFilter_exact();

        boolean getLinkTableFilter_duplicated();

        boolean getLinkTableFilter_similar();

        boolean getLinkTableFilter_conflict();

        boolean getLinkTableFilter_notDetected();

        void refreshLinkTable();

        void showLinkEditor();

        void hideLinkEditor();

        void resetLinkEditorImageAndFilter();

        String getLinkEditorFilterText();

        boolean isLinkEditorFindAllChecked();

        void setLinkEditorSelectedSongText(String value);

        void setLinkEditorCaptureImageItemList(
                ObservableList<ScannerReviewViewModel.CaptureData> value);

        void setLinkEditorImage(Image value);
    }
}
