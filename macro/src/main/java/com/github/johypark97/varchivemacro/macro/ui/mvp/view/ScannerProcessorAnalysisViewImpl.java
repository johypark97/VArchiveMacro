package com.github.johypark97.varchivemacro.macro.ui.mvp.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.common.SimpleTransition;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorAnalysis;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerProcessorFrame;
import com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel.ScannerAnalysisViewModel;
import java.io.IOException;
import java.net.URL;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ScannerProcessorAnalysisViewImpl extends StackPane
        implements ScannerProcessorAnalysis.View {
    private static final String FXML_PATH = "/fxml/ScannerProcessorAnalysis.fxml";

    private static final Duration RESULT_TRANSITION_DURATION = Duration.millis(200);

    @FXML
    private VBox progressBox;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private Label progressLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private Button functionButton;

    @FXML
    private Button showResultButton;

    @FXML
    private Pane coverPane;

    @FXML
    private TitledPane resultTitledPane;

    @FXML
    private TableView<ScannerAnalysisViewModel.AnalysisResult> resultTableView;

    @FXML
    private TableColumn<ScannerAnalysisViewModel.AnalysisResult, Integer>
            resultTableColumnCaptureId;

    @FXML
    private TableColumn<ScannerAnalysisViewModel.AnalysisResult, String> resultTableColumnStatus;

    @FXML
    private TableColumn<ScannerAnalysisViewModel.AnalysisResult, Exception>
            resultTableColumnExceptionMessage;

    @FXML
    private TableColumn<ScannerAnalysisViewModel.AnalysisResult, Exception>
            resultTableColumnExceptionShow;

    @FXML
    private Button closeResultButton;

    @MvpPresenter
    public ScannerProcessorAnalysis.Presenter presenter;

    public ScannerProcessorAnalysisViewImpl() {
        URL fxmlUrl = ScannerProcessorAnalysisViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        setupProgressBox();
        setupResultTitledPane();
    }

    private void setupProgressBox() {
        showResultButton.setDisable(true);
        showResultButton.setOnAction(event -> presenter.showResult());
    }

    private void setupResultTitledPane() {
        coverPane.setVisible(false);

        resultTitledPane.setDisable(true);
        resultTitledPane.setVisible(false);

        resultTableColumnCaptureId.setCellValueFactory(
                new PropertyValueFactory<>("captureEntryId"));

        resultTableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        resultTableColumnExceptionMessage.setCellValueFactory(
                new PropertyValueFactory<>("exception"));

        resultTableColumnExceptionShow.setCellValueFactory(new PropertyValueFactory<>("exception"));
        resultTableColumnExceptionShow.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Exception item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                Hyperlink hyperlink = new Hyperlink("\uD83D\uDDD6");
                hyperlink.setOnAction(event -> presenter.showResultException(item));
                hyperlink.setStyle("-fx-scale-x: 1.8; -fx-scale-y: 1.8;");

                setGraphic(hyperlink);
            }
        });

        closeResultButton.setOnAction(event -> presenter.hideResult());
    }

    @Override
    public void setProgress(double value) {
        progressIndicator.setProgress(value);
        progressLabel.setText(String.format("%.2f%%", value * 100));
    }

    @Override
    public void setMessageText(String value) {
        messageLabel.setText(value);
    }

    @Override
    public void setFunctionButton(String text, Runnable onAction) {
        functionButton.setOnAction(event -> onAction.run());
        functionButton.setText(text);
    }

    @Override
    public void enableShowResultButton(boolean value) {
        showResultButton.setDisable(!value);
    }

    @Override
    public void showResult() {
        progressBox.setDisable(true);
        resultTitledPane.setDisable(false);

        coverPane.setVisible(true);
        resultTitledPane.setVisible(true);

        new SimpleTransition(RESULT_TRANSITION_DURATION, x -> {
            coverPane.setOpacity(x / 2);
            resultTitledPane.setOpacity(x);
        }).play();
    }

    @Override
    public void hideResult() {
        progressBox.setDisable(false);
        resultTitledPane.setDisable(true);

        SimpleTransition transition = new SimpleTransition(RESULT_TRANSITION_DURATION, x -> {
            coverPane.setOpacity((1 - x) / 2);
            resultTitledPane.setOpacity(1 - x);
        });

        transition.setOnFinished(event -> {
            coverPane.setVisible(false);
            resultTitledPane.setVisible(false);
        });

        transition.play();
    }

    @Override
    public void setResultTableItemList(
            ObservableList<ScannerAnalysisViewModel.AnalysisResult> value) {
        resultTableView.setItems(value);
    }

    @Override
    public ScannerProcessorFrame.ButtonFunction getLeftButtonFunction() {
        return new ScannerProcessorFrame.ButtonFunction(
                Language.INSTANCE.getString("scanner.processor.analysis.frameButton.back"),
                event -> presenter.showReviewView());
    }

    @Override
    public ScannerProcessorFrame.ButtonFunction getRightButtonFunction() {
        return new ScannerProcessorFrame.ButtonFunction(
                Language.INSTANCE.getString("scanner.processor.analysis.frameButton.upload"),
                event -> presenter.showUploadView());
    }
}
