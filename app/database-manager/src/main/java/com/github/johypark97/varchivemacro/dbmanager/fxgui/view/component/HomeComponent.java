package com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.model.data.SongData.SongProperty;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.HomeViewImpl;
import com.github.johypark97.varchivemacro.lib.common.database.comparator.TitleComparator;
import com.github.johypark97.varchivemacro.lib.common.mvp.MvpFxml;
import java.net.URL;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class HomeComponent extends TabPane {
    private static final String FXML_FILENAME = "Home.fxml";

    public final HomeViewImpl view;

    @FXML
    public TextField viewerFilterTextField;

    @FXML
    public ComboBox<SongProperty> viewerFilterComboBox;

    @FXML
    public Button viewerFilterResetButton;

    @FXML
    public TableView<SongData> viewerTableView;

    public HomeComponent(HomeViewImpl view) {
        this.view = view;

        URL url = HomeComponent.class.getResource(FXML_FILENAME);
        MvpFxml.loadRoot(this, url);
    }

    @FXML
    public void initialize() {
        setupViewerTableView();

        viewerFilterTextField.textProperty()
                .addListener((observable, oldValue, newValue) -> view.updateViewerFilter());
        viewerFilterComboBox.valueProperty()
                .addListener((observable, oldValue, newValue) -> view.updateViewerFilter());
        viewerFilterResetButton.setOnAction(event -> viewerFilterTextField.clear());
    }

    private void setupViewerTableView() {
        TableColumn<SongData, Integer> id = new TableColumn<>("Id");
        TableColumn<SongData, String> title = new TableColumn<>("Title");
        TableColumn<SongData, String> remoteTitle = new TableColumn<>("Remote Title");
        TableColumn<SongData, String> composer = new TableColumn<>("Composer");
        TableColumn<SongData, String> dlc = new TableColumn<>("Dlc");
        TableColumn<SongData, Integer> priority = new TableColumn<>("Priority");

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        remoteTitle.setCellValueFactory(new PropertyValueFactory<>("remoteTitle"));
        composer.setCellValueFactory(new PropertyValueFactory<>("composer"));
        dlc.setCellValueFactory(new PropertyValueFactory<>("dlc"));
        priority.setCellValueFactory(new PropertyValueFactory<>("priority"));

        title.setComparator(new TitleComparator());
        remoteTitle.setComparator(new TitleComparator());

        viewerTableView.getSortOrder().setAll(List.of(title, priority));

        viewerTableView.getColumns()
                .setAll(List.of(id, title, remoteTitle, composer, dlc, priority));
    }
}
