package com.github.johypark97.varchivemacro.dbmanager.fxgui.view;

import com.github.johypark97.varchivemacro.dbmanager.core.NativeKeyEventData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterPresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.StartData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component.LiveTesterComponent;
import com.github.johypark97.varchivemacro.lib.common.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.common.mvp.AbstractMvpView;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.net.URL;
import java.util.Objects;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LiveTesterViewImpl extends AbstractMvpView<LiveTesterPresenter, LiveTesterView>
        implements LiveTesterView {
    private static final String TITLE = "OCR Live Tester";

    private static final String GLOBAL_CSS_FILENAME = "global.css";

    private LiveTesterComponent liveTesterComponent;

    private final NativeKeyListener nativeKeyListener;

    private StartData startData;

    public LiveTesterViewImpl() {
        nativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);
                if (data.isOtherMod()) {
                    return;
                }

                if (!data.isCtrl() && !data.isAlt() && !data.isShift()) {
                    if (data.isPressed(NativeKeyEvent.VC_ENTER)) {
                        recognize();
                    }
                }
            }
        };
    }

    @Override
    public void setStartData(StartData value) {
        startData = value;
    }

    @Override
    public void recognize() {
        LiveTester.RecognizedData data = getPresenter().onRecognize();
        if (data == null) {
            return;
        }

        liveTesterComponent.imageView.setImage(data.image);
        liveTesterComponent.ocrTextField.setText(data.text);
        liveTesterComponent.recognizedSongTextField.setText(data.recognized);
    }

    @Override
    protected LiveTesterView getInstance() {
        return this;
    }

    @Override
    protected Stage newStage() {
        URL globalCss = LiveTesterViewImpl.class.getResource(GLOBAL_CSS_FILENAME);
        Objects.requireNonNull(globalCss);

        liveTesterComponent = new LiveTesterComponent();
        Scene scene = new Scene(liveTesterComponent);
        scene.getStylesheets().add(globalCss.toExternalForm());

        Stage stage = new Stage();
        stage.setScene(scene);

        stage.setAlwaysOnTop(true);
        stage.setTitle(TITLE);

        stage.setOnShown(event -> {
            double height = stage.getHeight();
            double width = stage.getWidth();

            stage.setHeight(height);
            stage.setWidth(width);

            stage.setMinHeight(height);
            stage.setMinWidth(width);
        });

        stage.setOnShowing(event -> FxHookWrapper.addKeyListener(nativeKeyListener));
        stage.setOnHiding(event -> FxHookWrapper.removeKeyListener(nativeKeyListener));

        return stage;
    }

    @Override
    protected boolean onStartView() {
        Objects.requireNonNull(startData);

        return getPresenter().initialize(startData);
    }

    @Override
    protected boolean onStopView() {
        if (!getPresenter().terminate()) {
            return false;
        }

        liveTesterComponent = null; // NOPMD

        return true;
    }
}
