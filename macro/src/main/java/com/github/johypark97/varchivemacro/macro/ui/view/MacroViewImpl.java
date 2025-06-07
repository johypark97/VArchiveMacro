package com.github.johypark97.varchivemacro.macro.ui.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Macro;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class MacroViewImpl extends BorderPane implements Macro.MacroView {
    private static final String FXML_PATH = "/fxml/Macro.fxml";

    @FXML
    private Button countDecrease10Button;

    @FXML
    private Button countDecrease1Button;

    @FXML
    private TextField countTextField;

    @FXML
    private Button countIncrease1Button;

    @FXML
    private Button countIncrease10Button;

    @FXML
    private Slider countSlider;

    @FXML
    private Label clientModeLabel;

    @FXML
    private Label uploadKeyLabel;

    @FXML
    private Label startUpKeyLabel;

    @FXML
    private Label startDownKeyLabel;

    @FXML
    private Label stopKeyLabel;

    @FXML
    private Button homeButton;

    @MvpPresenter
    public Macro.MacroPresenter presenter;

    public MacroViewImpl() {
        URL fxmlUrl = MacroViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
    }
}
