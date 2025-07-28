package com.github.johypark97.varchivemacro.macro.ui.mvp.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.mvp.UpdateCheck;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

public class UpdateCheckViewImpl extends VBox implements UpdateCheck.View {
    private static final String FXML_PATH = "/fxml/UpdateCheck.fxml";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox contentBox;

    @FXML
    private Button checkAgainButton;

    @FXML
    private Button closeButton;

    @MvpPresenter
    public UpdateCheck.Presenter presenter;

    public UpdateCheckViewImpl() {
        URL fxmlUrl = UpdateCheckViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        contentBox.heightProperty().addListener(
                (observable, oldValue, newValue) -> scrollPane.setVvalue(newValue.doubleValue()));

        checkAgainButton.setOnAction(event -> presenter.checkUpdate());

        closeButton.setOnAction(event -> presenter.requestStopStage());
    }

    private HBox createTimeLine() {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(10);

        Separator leftSeparator = new Separator();
        HBox.setHgrow(leftSeparator, Priority.ALWAYS);
        box.getChildren().add(leftSeparator);

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
        box.getChildren().add(new Text(LocalTime.now().format(formatter)));

        Separator rightSeparator = new Separator();
        HBox.setHgrow(rightSeparator, Priority.ALWAYS);
        box.getChildren().add(rightSeparator);

        return box;
    }

    private void addContent(Consumer<Function<Node, Boolean>> adder) {
        VBox vBox = new VBox();
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(0, 0, 20, 0));

        vBox.getChildren().add(createTimeLine());
        adder.accept(vBox.getChildren()::add);

        contentBox.getChildren().add(vBox);
    }

    @Override
    public void clearAllMessages() {
        contentBox.getChildren().clear();
    }

    @Override
    public void addMessage(String... message) {
        addContent(adder -> {
            for (String s : message) {
                adder.apply(new Label(s));
            }
        });
    }

    @Override
    public void addErrorMessage(String message, Throwable throwable) {
        addContent(adder -> {
            Text messageText = new Text(message);
            messageText.setFill(Color.RED);
            adder.apply(messageText);

            Text exceptionText = new Text(throwable.getMessage());
            exceptionText.setFill(Color.RED);
            exceptionText.setFont(Font.font("Monospaced", FontPosture.ITALIC, 14));
            adder.apply(exceptionText);

            Button detailButton =
                    new Button(Language.INSTANCE.getString("updateCheck.button.detail"));
            detailButton.setOnAction(event -> presenter.showError(message, throwable));
            adder.apply(detailButton);
        });
    }

    @Override
    public void addNewVersionReleasedMessage(String currentVersion, String latestVersion,
            String url) {
        addContent(adder -> {
            Language language = Language.INSTANCE;

            adder.apply(new Text(language.getString("updateCheck.newVersionReleased")));
            adder.apply(new Text(
                    language.getFormatString("updateCheck.versionCompare", currentVersion,
                            latestVersion)));

            Hyperlink hyperlink = new Hyperlink(url);
            hyperlink.setOnAction(event -> presenter.openBrowser(url));
            adder.apply(hyperlink);
        });
    }

    @Override
    public void addProgramDataUpdatedMessage(long currentVersion, long latestVersion) {
        addContent(adder -> {
            Language language = Language.INSTANCE;

            adder.apply(new Text(language.getString("updateCheck.programDataUpdated")));
            adder.apply(new Text(
                    language.getFormatString("updateCheck.versionCompare", currentVersion,
                            latestVersion)));

            Button updateButton = new Button(language.getString("updateCheck.button.update"));
            updateButton.setOnAction(event -> {
                updateButton.setDisable(true);
                presenter.updateProgramData();
            });
            adder.apply(updateButton);
        });
    }

    @Override
    public UpdateCheck.DataUpdateProgressController addProgramDataUpdateProgressMessage() {

        ProgressBar progressBar = new ProgressBar();
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setMinHeight(16);

        Label label = new Label();

        UpdateCheck.DataUpdateProgressController controller =
                new DataUpdateProgressControllerImpl(progressBar, label);

        addContent(adder -> {
            adder.apply(new Label(Language.INSTANCE.getString("updateCheck.updatingProgramData")));
            adder.apply(progressBar);
            adder.apply(label);
        });

        return controller;
    }

    public static class DataUpdateProgressControllerImpl
            implements UpdateCheck.DataUpdateProgressController {
        private final WeakReference<Label> labelReference;
        private final WeakReference<ProgressBar> progressBarReference;

        public DataUpdateProgressControllerImpl(ProgressBar progressBar, Label label) {
            this.labelReference = new WeakReference<>(label);
            this.progressBarReference = new WeakReference<>(progressBar);
        }

        @Override
        public void setProgress(double value) {
            Optional.ofNullable(progressBarReference.get()).ifPresent(x -> x.setProgress(value));
        }

        @Override
        public void setMessage(String value) {
            Optional.ofNullable(labelReference.get()).ifPresent(x -> x.setText(value));
        }
    }
}
