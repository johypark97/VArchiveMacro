package com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongProperty;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpPresenter;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

public interface Home {
    interface HomePresenter extends MvpPresenter<HomeView> {
        void linkViewerTable(TableView<SongData> tableView);

        void setFilterableColumn(ComboBox<SongProperty> comboBox);

        void updateViewerTableFilter(String regex, SongProperty property);
    }


    interface HomeView extends MvpView<HomePresenter> {
    }
}
