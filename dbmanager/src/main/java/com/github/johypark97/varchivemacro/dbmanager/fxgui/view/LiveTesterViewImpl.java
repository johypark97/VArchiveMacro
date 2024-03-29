package com.github.johypark97.varchivemacro.dbmanager.fxgui.view;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterPresenter;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.LiveTesterView;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.component.LiveTesterComponent;
import com.github.johypark97.varchivemacro.dbmanager.fxgui.view.stage.LiveTesterStage;
import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.hook.NativeKeyEventData;
import com.github.johypark97.varchivemacro.lib.jfx.mvp.AbstractMvpView;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.lang.ref.WeakReference;
import javafx.stage.Stage;

public class LiveTesterViewImpl extends AbstractMvpView<LiveTesterPresenter, LiveTesterView>
        implements LiveTesterView {
    private final NativeKeyListener nativeKeyListener;

    private WeakReference<LiveTesterComponent> liveTesterComponentReference;

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
    public void focusView() {
        getStage().requestFocus();
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
    protected Stage newStage() {
        LiveTesterStage stage = new LiveTesterStage();

        liveTesterComponentReference = new WeakReference<>(stage.liveTesterComponent);

        stage.setOnShowing(event -> FxHookWrapper.addKeyListener(nativeKeyListener));

        stage.setOnShown(event -> {
            Stage source = ((Stage) event.getSource());

            double height = source.getHeight();
            double width = source.getWidth();

            source.setHeight(height);
            source.setWidth(width);

            source.setMinHeight(height);
            source.setMinWidth(width);
        });

        stage.setOnHiding(event -> FxHookWrapper.removeKeyListener(nativeKeyListener));

        return stage;
    }
}
