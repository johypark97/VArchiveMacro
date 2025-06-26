package com.github.johypark97.varchivemacro.macro.ui.stage;

import com.github.johypark97.varchivemacro.lib.jfx.AlertBuilder;
import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.integration.context.ContextManager;
import com.github.johypark97.varchivemacro.macro.integration.provider.FactoryProvider;
import com.github.johypark97.varchivemacro.macro.ui.event.GlobalEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.GlobalEventBus;
import com.github.johypark97.varchivemacro.macro.ui.presenter.Setting;
import com.github.johypark97.varchivemacro.macro.ui.presenter.SettingPresenterImpl;
import com.github.johypark97.varchivemacro.macro.ui.resource.UiResource;
import com.github.johypark97.varchivemacro.macro.ui.stage.base.AbstractTreeableStage;
import com.github.johypark97.varchivemacro.macro.ui.view.SettingViewImpl;
import java.awt.Toolkit;
import java.io.File;
import java.nio.file.Path;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

public class SettingStageImpl extends AbstractTreeableStage implements SettingStage {
    private static final int STAGE_HEIGHT = 540;
    private static final int STAGE_WIDTH = 960;

    private final Runnable onStop;

    private Setting.SettingPresenter presenter;

    public SettingStageImpl(AbstractTreeableStage parent, Runnable onStop) {
        super(parent);

        this.onStop = onStop;

        setupStage();
    }

    private void setupStage() {
        stage.getIcons().add(new Image(UiResource.ICON.url().toString()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(Language.INSTANCE.getString("setting.windowTitle"));

        stage.setHeight(STAGE_HEIGHT);
        stage.setWidth(STAGE_WIDTH);

        stage.setMinHeight(STAGE_HEIGHT / 2.0);
        stage.setMinWidth(STAGE_WIDTH / 2.0);
    }

    @Override
    public void startStage() {
        presenter = new SettingPresenterImpl(this, ContextManager.INSTANCE.getGlobalContext(),
                FactoryProvider.createCaptureImageCacheFactory());

        SettingViewImpl view = new SettingViewImpl();
        Mvp.linkViewAndPresenter(view, presenter);

        Scene scene = new Scene(view);
        scene.getStylesheets().add(UiResource.GLOBAL_CSS.url().toExternalForm());

        stage.setScene(scene);
        stage.setOnShown(event -> presenter.startView());

        stage.show();

        GlobalEventBus.INSTANCE.fire(GlobalEvent.SETTING_WINDOW_OPENED);
    }

    @Override
    public void showError(String content, Throwable throwable) {
        showError(null, content, throwable);
    }

    @Override
    public void showError(String header, String content, Throwable throwable) {
        Alert alert = AlertBuilder.error().setOwner(stage).setContentText(content)
                .setThrowable(throwable).alert;

        if (header != null) {
            alert.setHeaderText(header);
        }

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();
    }

    @Override
    public CloseDialogChoice showCloseDialog() {
        Language language = Language.INSTANCE;

        String title = language.getString("setting.dialog.close.title");
        String header = language.getString("setting.dialog.close.header");
        String content = language.getString("setting.dialog.close.content");

        Alert alert =
                AlertBuilder.confirmation().setOwner(stage).setTitle(title).setHeaderText(header)
                        .setContentText(content).alert;

        alert.getButtonTypes().setAll(ButtonType.APPLY, ButtonType.CLOSE, ButtonType.CANCEL);

        ButtonBar buttonBar = (ButtonBar) alert.getDialogPane().lookup(".button-bar");
        buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);

        Toolkit.getDefaultToolkit().beep();
        alert.showAndWait();

        ButtonType result = alert.getResult();
        if (ButtonType.APPLY.equals(result)) {
            return CloseDialogChoice.APPLY;
        } else if (ButtonType.CLOSE.equals(result)) {
            return CloseDialogChoice.CLOSE;
        } else {
            return CloseDialogChoice.CANCEL;
        }
    }

    @Override
    public File showAccountFileSelector() {
        FileChooser chooser = new FileChooser();

        chooser.setInitialDirectory(Path.of("").toAbsolutePath().toFile());
        chooser.setTitle(Language.INSTANCE.getString("setting.dialog.accountFileSelectorTitle"));

        chooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Account file (*.txt)", "*.txt"));

        return chooser.showOpenDialog(stage);
    }

    @Override
    public File showCacheDirectorySelector() {
        DirectoryChooser chooser = new DirectoryChooser();

        chooser.setInitialDirectory(Path.of("").toAbsolutePath().toFile());
        chooser.setTitle(Language.INSTANCE.getString("setting.dialog.cacheDirectorySelectorTitle"));

        return chooser.showDialog(stage);
    }

    @Override
    protected boolean onStopStage() {
        if (!presenter.stopView()) {
            return false;
        }

        onStop.run();

        GlobalEventBus.INSTANCE.fire(GlobalEvent.SETTING_WINDOW_CLOSED);

        return true;
    }
}
