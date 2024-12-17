package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.updatecheck;

import com.github.johypark97.varchivemacro.lib.jfx.mvp.MvpFxml;
import com.github.johypark97.varchivemacro.macro.resource.Language;
import java.awt.Desktop;
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
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;

public class UpdateCheckViewImpl extends VBox implements UpdateCheck.UpdateCheckView {
    private static final String FXML_PATH = "/fxml/home/updatecheck/UpdateCheck.fxml";

    @MvpPresenter
    public UpdateCheck.UpdateCheckPresenter presenter;

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

        checkAgainButton.setOnAction(event -> presenter.checkAgain());
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
    public void addUpdatedMessage(String currentVersion, String latestVersion, String url) {
        addContent(adder -> {
            Language language = Language.getInstance();

            adder.apply(new Text(language.getString("home.updateCheck.updated.message")));
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
    public void clearAllMessages() {
        contentBox.getChildren().clear();
    }
}
