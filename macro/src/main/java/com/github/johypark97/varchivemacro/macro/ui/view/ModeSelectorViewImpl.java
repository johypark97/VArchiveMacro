package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.common.SimpleTransition;
import com.github.johypark97.varchivemacro.macro.ui.presenter.ModeSelector;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import java.io.IOException;
import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ModeSelectorViewImpl extends GridPane implements ModeSelector.View {
    private static final Duration ANIMATION_DURATION = Duration.millis(200);
    private static final String FXML_PATH = "/fxml/ModeSelector.fxml";

    @FXML
    private StackPane leftStackPane;

    @FXML
    private Pane leftBackgroundImagePane;

    @FXML
    private Label leftHeader;

    @FXML
    private Label leftDescription;

    @FXML
    private Button leftButton;

    @FXML
    private StackPane rightStackPane;

    @FXML
    private Pane rightBackgroundImagePane;

    @FXML
    private Label rightHeader;

    @FXML
    private Label rightDescription;

    @FXML
    private Button rightButton;

    @MvpPresenter
    public ModeSelector.Presenter presenter;

    public ModeSelectorViewImpl() {
        URL fxmlUrl = ModeSelectorViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getStylesheets().add(UiResource.MODE_SELECTOR_CSS.url().toExternalForm());
    }

    @FXML
    public void initialize() {
        setupClip(leftStackPane);
        setupClip(rightStackPane);

        setupAnimation(leftButton, leftBackgroundImagePane, leftHeader, leftDescription);
        setupAnimation(rightButton, rightBackgroundImagePane, rightHeader, rightDescription);

        setOnKeyPressed(event -> {
            if (KeyCode.LEFT.equals(event.getCode())) {
                leftButton.requestFocus();
            } else if (KeyCode.RIGHT.equals(event.getCode())) {
                rightButton.requestFocus();
            }
        });

        leftButton.setOnMouseEntered(event -> leftButton.requestFocus());
        leftButton.setOnMouseExited(event -> requestFocus());

        rightButton.setOnMouseEntered(event -> rightButton.requestFocus());
        rightButton.setOnMouseExited(event -> requestFocus());

        leftButton.setOnAction(event -> presenter.showFreestyleMacroView());
        rightButton.setOnAction(event -> presenter.showCollectionScannerView());
    }

    private void setupClip(Region parent) {
        Rectangle clip = new Rectangle();

        clip.widthProperty().bind(parent.widthProperty());
        clip.heightProperty().bind(parent.heightProperty());

        parent.setClip(clip);
    }

    private void setupAnimation(Button button, Pane imagePane, Label headerLabel,
            Label descriptionLabel) {
        // background image animations
        GaussianBlur imageBlur = new GaussianBlur();

        ColorAdjust imageColor = new ColorAdjust();
        imageColor.setInput(imageBlur);

        imagePane.setEffect(imageColor);

        SimpleTransition imageBlurTransition =
                new SimpleTransition(ANIMATION_DURATION, x -> imageBlur.setRadius(16 * (1 - x)));

        SimpleTransition imageColorTransition = new SimpleTransition(ANIMATION_DURATION, x -> {
            imageColor.setBrightness(-0.8 + x * 0.4);
            imageColor.setSaturation(x - 1);
        });

        // header animations
        DropShadow headerDropShadow = new DropShadow();
        headerDropShadow.setOffsetX(2);
        headerDropShadow.setOffsetY(2);

        Bloom headerBloom = new Bloom(0.2);
        headerBloom.setInput(headerDropShadow);

        headerLabel.setEffect(headerBloom);

        SimpleTransition headerDropShadowTransition =
                new SimpleTransition(ANIMATION_DURATION, x -> {
                    headerDropShadow.setRadius(16 * x);
                    headerDropShadow.setSpread(0.8 * x);
                });

        // description animations
        descriptionLabel.setOpacity(0);

        DropShadow descriptionDropShadow = new DropShadow();
        descriptionDropShadow.setRadius(16);
        descriptionDropShadow.setSpread(0.8);
        descriptionDropShadow.setOffsetX(2);
        descriptionDropShadow.setOffsetY(2);

        descriptionLabel.setEffect(descriptionDropShadow);

        FadeTransition descriptionFadeTransition =
                new FadeTransition(ANIMATION_DURATION, descriptionLabel);
        descriptionFadeTransition.setFromValue(0);
        descriptionFadeTransition.setToValue(1);

        TranslateTransition descriptionTranslateTransition =
                new TranslateTransition(ANIMATION_DURATION, descriptionLabel);
        descriptionTranslateTransition.setFromY(50);
        descriptionTranslateTransition.setToY(70);

        // apply transitions
        ParallelTransition transition =
                new ParallelTransition(imageBlurTransition, imageColorTransition,
                        headerDropShadowTransition, descriptionFadeTransition,
                        descriptionTranslateTransition);

        button.focusedProperty().addListener((observable, oldValue, newValue) -> {
            transition.setRate(newValue ? 1 : -1);
            transition.play();
        });
    }
}
