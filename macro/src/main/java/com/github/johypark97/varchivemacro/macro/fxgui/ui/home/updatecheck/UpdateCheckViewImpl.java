package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck.UpdateCheck.DataUpdateProgressController;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.awt.Desktop;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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

public class UpdateCheckViewImpl extends VBox implements UpdateCheck.UpdateCheckView {
    private static final String FXML_PATH = "/fxml/UpdateCheck.fxml";

    @MvpPresenter
    public UpdateCheck.UpdateCheckPresenter presenter;

    @FXML
    public ScrollPane scrollPane;

    @FXML
    public VBox contentBox;

    @FXML
    public Button checkAgainButton;

    public UpdateCheckViewImpl() {
        URL url = UpdateCheckViewImpl.class.getResource(FXML_PATH);
        MvpFxml.loadRoot(this, url, Language.getInstance().getResourceBundle());
    }

    @FXML
    public void initialize() {
        contentBox.setStyle("-fx-background-color: white;");

        contentBox.heightProperty().addListener(
                (observable, oldValue, newValue) -> scrollPane.setVvalue(newValue.doubleValue()));

        checkAgainButton.setOnAction(event -> presenter.checkUpdate());
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
    public void startView() {
        presenter.onStartView();
    }

    @Override
    public void clearAllMessages() {
        contentBox.getChildren().clear();
    }

    @Override
    public void addMessage(String message) {
        addContent(adder -> adder.apply(new Text(message)));
    }

    @Override
    public void addErrorMessage(String message, Throwable throwable) {
        addContent(adder -> {
            Text messageText = new Text(message);
            messageText.setFill(Color.RED);
            adder.apply(messageText);

            Text exceptionText = new Text(throwable.toString());
            exceptionText.setFill(Color.RED);
            exceptionText.setFont(Font.font("Monospaced", FontPosture.ITALIC, 14));
            adder.apply(exceptionText);

            Button detailButton =
                    new Button(Language.getInstance().getString("home.updateCheck.error.detail"));
            detailButton.setOnAction(event -> presenter.showError(message, throwable));
            adder.apply(detailButton);
        });
    }

    @Override
    public void addProgramUpdatedMessage(String currentVersion, String latestVersion, String url) {
        addContent(adder -> {
            Language language = Language.getInstance();

            adder.apply(new Text(language.getString("home.updateCheck.updated.program")));
            adder.apply(new Text(
                    language.getFormatString("home.updateCheck.updated.version", currentVersion,
                            latestVersion)));

            Hyperlink hyperlink = new Hyperlink(url);
            hyperlink.setOnAction(event -> {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception e) {
                    presenter.showError("Open browser error", e);
                }
            });
            adder.apply(hyperlink);
        });
    }

    @Override
    public void addDataUpdatedMessage(long currentVersion, long latestVersion) {
        addContent(adder -> {
            Language language = Language.getInstance();

            adder.apply(new Text(language.getString("home.updateCheck.updated.data")));
            adder.apply(new Text(
                    language.getFormatString("home.updateCheck.updated.version", currentVersion,
                            latestVersion)));

            Button updateButton = new Button(language.getString("home.updateCheck.button.update"));
            updateButton.setOnAction(event -> presenter.updateData(updateButton::setDisable));
            adder.apply(updateButton);
        });
    }

    @Override
    public DataUpdateProgressController addDataUpdateProgressMessage() {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setMinHeight(16);

        Label label = new Label();

        DataUpdateProgressController controller =
                new DataUpdateProgressControllerImpl(progressBar, label);

        addContent(adder -> {
            Language language = Language.getInstance();

            adder.apply(new Label(language.getString("home.updateCheck.updatingData")));
            adder.apply(progressBar);
            adder.apply(label);
        });

        return controller;
    }

    @Override
    public void addDataUpdateCompleteMessage(String header, String message) {
        addContent(adder -> {
            Label headerLabel = new Label(header);
            adder.apply(headerLabel);

            Label messageLabel = new Label(message);
            adder.apply(messageLabel);
        });
    }

    public static class DataUpdateProgressControllerImpl implements DataUpdateProgressController {
        private final WeakReference<Label> labelReference;
        private final WeakReference<ProgressBar> progressBarReference;

        public DataUpdateProgressControllerImpl(ProgressBar progressBar, Label label) {
            this.labelReference = new WeakReference<>(label);
            this.progressBarReference = new WeakReference<>(progressBar);
        }

        @Override
        public void setProgress(double value) {
            ProgressBar progressBar = progressBarReference.get();
            if (progressBar != null) {
                progressBar.setProgress(value);
            }
        }

        @Override
        public void setMessage(String value) {
            Label label = labelReference.get();
            if (label != null) {
                label.setText(value);
            }
        }
    }
}
