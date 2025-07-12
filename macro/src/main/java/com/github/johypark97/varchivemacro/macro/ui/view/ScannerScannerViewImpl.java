package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.common.SimpleTransition;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ScannerScanner;
import com.github.johypark97.varchivemacro.macro.ui.viewmodel.ScannerScannerViewModel;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ScannerScannerViewImpl extends StackPane implements ScannerScanner.View {
    private static final String FXML_PATH = "/fxml/ScannerScanner.fxml";

    private static final Duration PROGRESS_TRANSITION_DURATION = Duration.millis(500);
    private static final int PROGRESS_TRANSITION_BLUR_RADIUS = 4;

    private final GaussianBlur scannerBoxBlur = new GaussianBlur(0);

    @FXML
    private HBox scannerBox;

    @FXML
    private TextField accountFileTextField;

    @FXML
    private Tooltip accountFileTextFieldTooltip;

    @FXML
    private CheckBox accountFileShowCheckBox;

    @FXML
    private TextField cacheDirectoryTextField;

    @FXML
    private Tooltip cacheDirectoryTextFieldTooltip;

    @FXML
    private CheckBox cacheDirectoryShowCheckBox;

    @FXML
    private Label autoAnalysisLabel;

    @FXML
    private Label startKeyLabel;

    @FXML
    private Label stopKeyLabel;

    @FXML
    private CheckBox debugCheckBox;

    @FXML
    private Button checkButton;

    @FXML
    private ListView<ScannerScannerViewModel.CategoryData> categoryListView;

    @FXML
    private Label categoryCountLabel;

    @FXML
    private Button toggleAllCategoryButton;

    @FXML
    private VBox progressBox;

    @MvpPresenter
    public ScannerScanner.Presenter presenter;

    private StringBinding categoryCountStringBinding;

    public ScannerScannerViewImpl() {
        URL fxmlUrl = ScannerScannerViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        scannerBox.setEffect(scannerBoxBlur);

        accountFileTextFieldTooltip.textProperty().bind(accountFileTextField.textProperty());
        cacheDirectoryTextFieldTooltip.textProperty().bind(cacheDirectoryTextField.textProperty());
        debugCheckBox.setVisible(false);

        checkButton.setOnAction(event -> presenter.checkDisplayAndResolution());

        categoryCountStringBinding = Bindings.createStringBinding(() -> {
            List<ScannerScannerViewModel.CategoryData> list = categoryListView.getItems();
            long selectedCount = list.stream().filter(x -> x.selected.get()).count();
            return String.format("%d / %d", selectedCount, list.size());
        });

        categoryListView.setCellFactory(CheckBoxListCell.forListView(param -> {
            param.selected.addListener((observable, oldValue, newValue) -> Platform.runLater(
                    categoryCountStringBinding::invalidate));
            return param.selected;
        }));
        categoryCountLabel.textProperty().bind(categoryCountStringBinding);
        toggleAllCategoryButton.setOnAction(event -> {
            List<ScannerScannerViewModel.CategoryData> list = categoryListView.getItems();
            boolean value = list.stream().filter(x -> x.selected.get()).count() != list.size();
            list.forEach(x -> x.selected.set(value));
        });

        progressBox.setVisible(false);
    }

    @Override
    public void bindAccountFileText(ObservableStringValue value) {
        accountFileTextField.textProperty()
                .bind(Bindings.when(accountFileShowCheckBox.selectedProperty()).then(value)
                        .otherwise("********"));
    }

    @Override
    public void bindCacheDirectoryText(ObservableStringValue value) {
        cacheDirectoryTextField.textProperty()
                .bind(Bindings.when(cacheDirectoryShowCheckBox.selectedProperty()).then(value)
                        .otherwise("********"));
    }

    @Override
    public void setAutoAnalysis(boolean value) {
        autoAnalysisLabel.setText(value ? "ON" : "OFF");
    }

    @Override
    public void setStartKeyText(String value) {
        startKeyLabel.setText(value);
    }

    @Override
    public void setStopKeyText(String value) {
        stopKeyLabel.setText(value);
    }

    @Override
    public void setDebugCheckBoxVisible(boolean value) {
        debugCheckBox.setVisible(value);
    }

    @Override
    public boolean getDebugCheckBoxValue() {
        return debugCheckBox.isSelected();
    }

    @Override
    public void setCategoryList(List<ScannerScannerViewModel.CategoryData> value) {
        categoryListView.setItems(FXCollections.observableList(value));
        Platform.runLater(categoryCountStringBinding::invalidate);
    }

    @Override
    public List<ScannerScannerViewModel.CategoryData> getSelectedCategoryList() {
        return categoryListView.getItems().stream().filter(x -> x.selected.get()).toList();
    }

    @Override
    public void setSelectedCategory(Set<String> value) {
        categoryListView.getItems().forEach(x -> {
            if (value.contains(x.category.name())) {
                x.selected.set(true);
            }
        });
    }

    @Override
    public void showProgressBox() {
        scannerBox.setDisable(true);

        progressBox.setVisible(true);

        new SimpleTransition(PROGRESS_TRANSITION_DURATION, x -> {
            scannerBox.setOpacity(1 - 0.8 * x);
            progressBox.setOpacity(x);
            scannerBoxBlur.setRadius(x * PROGRESS_TRANSITION_BLUR_RADIUS);
        }).play();
    }

    @Override
    public void hideProgressBox() {
        scannerBox.setDisable(false);

        SimpleTransition transition = new SimpleTransition(PROGRESS_TRANSITION_DURATION, x -> {
            scannerBox.setOpacity(0.2 + 0.8 * x);
            progressBox.setOpacity(1 - x);
            scannerBoxBlur.setRadius(PROGRESS_TRANSITION_BLUR_RADIUS * (1 - x));
        });
        transition.setOnFinished(event -> progressBox.setVisible(false));

        transition.play();
    }
}
