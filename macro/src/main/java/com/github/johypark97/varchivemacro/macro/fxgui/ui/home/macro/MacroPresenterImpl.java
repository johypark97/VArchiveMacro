package com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro;

import com.github.johypark97.varchivemacro.lib.hook.FxHookWrapper;
import com.github.johypark97.varchivemacro.lib.hook.NativeKeyEventData;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro.Macro.MacroPresenter;
import com.github.johypark97.varchivemacro.macro.fxgui.ui.home.macro.Macro.MacroView;
import com.github.johypark97.varchivemacro.macro.model.AnalysisKey;
import com.github.johypark97.varchivemacro.macro.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.service.MacroService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import java.util.function.BiConsumer;
import javafx.geometry.VerticalDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacroPresenterImpl implements MacroPresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MacroPresenterImpl.class);

    private final NativeKeyListener macroNativeKeyListener;

    private final ConfigRepository configRepository;
    private final MacroService macroService;

    private final BiConsumer<String, Throwable> showError;

    @MvpView
    public MacroView view;

    public MacroPresenterImpl(ConfigRepository configRepository, MacroService macroService,
            BiConsumer<String, Throwable> showError) {
        this.configRepository = configRepository;
        this.macroService = macroService;
        this.showError = showError;

        macroNativeKeyListener = new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);

                if (data.isOtherMod()) {
                    return;
                }

                if (data.isPressed(NativeKeyEvent.VC_BACKSPACE)) {
                    stop();
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
                NativeKeyEventData data = new NativeKeyEventData(nativeEvent);

                if (data.isOtherMod()) {
                    return;
                }

                if (!data.isCtrl() && data.isAlt() && !data.isShift()) {
                    if (data.isPressed(NativeKeyEvent.VC_OPEN_BRACKET)) {
                        start_up();
                    } else if (data.isPressed(NativeKeyEvent.VC_CLOSE_BRACKET)) {
                        start_down();
                    }
                }
            }
        };
    }

    @Override
    public void onStartView() {
        macroService.setupService(throwable -> {
            String header = "Macro service exception";

            LOGGER.atError().setCause(throwable).log(header);
            showError.accept(header, throwable);
        });

        MacroConfig macroConfig = configRepository.getMacroConfig();

        view.setAnalysisKey(macroConfig.analysisKey);

        view.setupCountSlider(MacroConfig.COUNT_DEFAULT, MacroConfig.COUNT_MAX,
                MacroConfig.COUNT_MIN, macroConfig.count);

        view.setupCaptureDelaySlider(MacroConfig.CAPTURE_DELAY_DEFAULT,
                MacroConfig.CAPTURE_DELAY_MAX, MacroConfig.CAPTURE_DELAY_MIN,
                macroConfig.captureDelay);

        view.setupCaptureDurationSlider(MacroConfig.CAPTURE_DURATION_DEFAULT,
                MacroConfig.CAPTURE_DURATION_MAX, MacroConfig.CAPTURE_DURATION_MIN,
                macroConfig.captureDuration);

        view.setupKeyInputDurationSlider(MacroConfig.KEY_INPUT_DURATION_DEFAULT,
                MacroConfig.KEY_INPUT_DURATION_MAX, MacroConfig.KEY_INPUT_DURATION_MIN,
                macroConfig.keyInputDuration);

        FxHookWrapper.addKeyListener(macroNativeKeyListener);
    }

    @Override
    public void onStopView() {
        FxHookWrapper.removeKeyListener(macroNativeKeyListener);

        MacroConfig macroConfig = new MacroConfig();

        macroConfig.analysisKey = view.getAnalysisKey();
        macroConfig.count = view.getCount();
        macroConfig.captureDelay = view.getCaptureDelay();
        macroConfig.captureDuration = view.getCaptureDuration();
        macroConfig.keyInputDuration = view.getKeyInputDuration();

        configRepository.setMacroConfig(macroConfig);
    }

    @Override
    public void start_up() {
        AnalysisKey analysisKey = view.getAnalysisKey();
        int count = view.getCount();
        int captureDelay = view.getCaptureDelay();
        int captureDuration = view.getCaptureDuration();
        int keyInputDuration = view.getKeyInputDuration();

        macroService.startMacro(analysisKey, count, captureDelay, captureDuration, keyInputDuration,
                VerticalDirection.UP);
    }

    @Override
    public void start_down() {
        AnalysisKey analysisKey = view.getAnalysisKey();
        int count = view.getCount();
        int captureDelay = view.getCaptureDelay();
        int captureDuration = view.getCaptureDuration();
        int keyInputDuration = view.getKeyInputDuration();

        macroService.startMacro(analysisKey, count, captureDelay, captureDuration, keyInputDuration,
                VerticalDirection.DOWN);
    }

    @Override
    public void stop() {
        macroService.stopMacro();
    }
}
