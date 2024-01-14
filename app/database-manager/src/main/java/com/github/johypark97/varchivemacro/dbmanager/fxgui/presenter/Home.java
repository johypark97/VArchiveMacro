package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongDataProperty;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

public interface Home {
    interface HomePresenter extends MvpPresenter<HomeView> {
        void onLinkViewerTable(TableView<SongData> tableView);

        void onSetViewerTableFilterColumn(ComboBox<SongDataProperty> comboBox);

        void onUpdateViewerTableFilter(String regex, SongDataProperty property);
    }


    interface HomeView extends MvpView<HomePresenter> {
    }
}
