package com.github.johypark97.varchivemacro.macro.ui.stage.base;

import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import java.awt.Toolkit;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public abstract class AbstractBaseStage extends AbstractTreeableStage implements BaseStage {
    public AbstractBaseStage(AbstractTreeableStage parent) {
        super(parent);
    }

    public AbstractBaseStage(Stage stage) {
        super(stage);
    }

    @Override
    public final void focusStage() {
        stage.requestFocus();
    }

    @Override
    public final void showError(String content, Throwable throwable) {
        showError(null, content, throwable);
    }

    @Override
    public final void showError(String header, String content, Throwable throwable) {
        Alert alert = AlertBuilder.error().setOwner(stage).setContentText(content)
                .setThrowable(throwable).alert;

        if (header != null) {
            alert.setHeaderText(header);
        }

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public final void showWarning(String content) {
        showWarning(null, content);
    }

    @Override
    public final void showWarning(String header, String content) {
        Alert alert = AlertBuilder.warning().setOwner(stage).setContentText(content).alert;

        if (header != null) {
            alert.setHeaderText(header);
        }

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public final void showInformation(String content) {
        showInformation(null, content);
    }

    @Override
    public final void showInformation(String header, String content) {
        Alert alert = AlertBuilder.information().setOwner(stage).setContentText(content).alert;

        if (header != null) {
            alert.setHeaderText(header);
        }

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public final boolean showConfirmation(String content) {
        return showConfirmation(null, content);
    }

    @Override
    public final boolean showConfirmation(String header, String content) {
        Alert alert = AlertBuilder.confirmation().setOwner(stage).setContentText(content).alert;

        if (header != null) {
            alert.setHeaderText(header);
        }

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();

        return ButtonType.OK.equals(alert.getResult());
    }
}
