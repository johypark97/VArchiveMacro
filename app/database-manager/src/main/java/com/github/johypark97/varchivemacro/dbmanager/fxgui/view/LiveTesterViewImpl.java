package com.github.johypark97.varchivemacro.dbmanager.fxgui.view;

import com.github.johypark97.varchivemacro.dbmanager.core.NativeKeyEventData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterPresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.StartData;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.stage.LiveTesterStage;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component.LiveTesterComponent;
import com.github.johypark97.varchivemacro.lib.common.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.common.mvp.AbstractMvpView;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.lang.ref.WeakReference;
import java.util.Objects;
import javafx.stage.Stage;

public class LiveTesterViewImpl extends AbstractMvpView<LiveTesterPresenter, LiveTesterView>
        implements LiveTesterView {
    private final NativeKeyListener nativeKeyListener;

    private WeakReference<LiveTesterComponent> liveTesterComponentReference;

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

    private LiveTesterComponent getLiveTesterComponent() {
        return liveTesterComponentReference.get();
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

        getLiveTesterComponent().imageView.setImage(data.image);
        getLiveTesterComponent().ocrTextField.setText(data.text);
        getLiveTesterComponent().recognizedSongTextField.setText(data.recognized);
    }

    @Override
    protected LiveTesterView getInstance() {
        return this;
    }

    @Override
    protected Stage newStage() {
        LiveTesterStage stage = new LiveTesterStage();

        liveTesterComponentReference = new WeakReference<>(stage.liveTesterComponent);

        stage.setOnShowing(event -> FxHookWrapper.addKeyListener(nativeKeyListener));

        stage.setOnShown(event -> {
            double height = stage.getHeight();
            double width = stage.getWidth();

            stage.setHeight(height);
            stage.setWidth(width);

            stage.setMinHeight(height);
            stage.setMinWidth(width);
        });

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
        return getPresenter().terminate();
    }
}
