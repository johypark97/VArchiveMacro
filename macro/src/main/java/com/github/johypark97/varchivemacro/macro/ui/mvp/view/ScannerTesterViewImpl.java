package com.github.johypark97.varchivemacro.macro.ui.mvp.view;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.ui.mvp.ScannerTester;
import com.github.johypark97.varchivemacro.macro.ui.mvp.view.component.ImageViewerBox;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

public class ScannerTesterViewImpl extends BorderPane implements ScannerTester.View {
    private static final String FXML_PATH = "/fxml/ScannerTester.fxml";

    private final ImageViewerBox imageViewerBox = new ImageViewerBox();

    @FXML
    private Button closeButton;

    @MvpPresenter
    public ScannerTester.Presenter presenter;

    public ScannerTesterViewImpl() {
        URL fxmlUrl = ScannerTesterViewImpl.class.getResource(FXML_PATH);

        try {
            Mvp.loadFxml(this, fxmlUrl, x -> x.setResources(Language.INSTANCE.getResourceBundle()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void initialize() {
        setCenter(imageViewerBox);

        closeButton.setOnAction(event -> presenter.requestStopStage());
    }

    @Override
    public void setImage(Image image) {
        imageViewerBox.setImage(image);
    }
}
