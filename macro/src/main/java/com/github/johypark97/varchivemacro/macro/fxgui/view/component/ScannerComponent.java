package com.github.johypark97.varchivemacro.macro.fxgui.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView;
import com.github.johypark97.varchivemacro.macro.fxgui.presenter.Home.HomeView.ViewerTreeData;
import java.lang.ref.WeakReference;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;

public class ScannerComponent extends TabPane {
    private static final String FXML_FILE_NAME = "Scanner.fxml";

    private final WeakReference<HomeView> viewReference;

    @FXML
    public TextField viewer_filterTextField;

    @FXML
    public Button viewer_filterResetButton;

    @FXML
    public TreeView<ViewerTreeData> viewer_treeView;

    public ScannerComponent(HomeView view) {
        viewReference = new WeakReference<>(view);

        URL url = ScannerComponent.class.getResource(FXML_FILE_NAME);
        MvpFxml.loadRoot(this, url);
    }

    @FXML
    public void initialize() {
        setVisible(false);

        setupViewer();
    }

    private HomeView getView() {
        return viewReference.get();
    }

    private void setupViewer() {
        viewer_filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String value = newValue.trim();
            if (!value.equals(oldValue.trim())) {
                getView().scanner_viewer_showSongTree(newValue.trim());
            }
        });

        viewer_filterResetButton.setOnAction(event -> viewer_filterTextField.setText(""));

        setupViewer_treeView();
    }

    private void setupViewer_treeView() {
        viewer_treeView.setShowRoot(false);

        viewer_treeView.setCellFactory(param -> new TreeCell<>() {
            @Override
            protected void updateItem(ViewerTreeData item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    return;
                }

                if (item.name != null) {
                    setText(item.name);
                    return;
                }

                setText(String.format("%s ...... %s", item.song.title, item.song.composer));
            }
        });
    }
}
