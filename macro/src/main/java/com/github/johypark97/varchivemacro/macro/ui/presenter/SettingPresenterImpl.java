package com.github.johypark97.varchivemacro.macro.ui.presenter;

import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.github.johypark97.varchivemacro.macro.application.event.GlobalEvent;
import com.github.johypark97.varchivemacro.macro.application.event.GlobalEventBus;
import com.github.johypark97.varchivemacro.macro.application.scanner.factory.CaptureImageCacheFactory;
import com.github.johypark97.varchivemacro.macro.common.converter.InputKeyConverter;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.infrastructure.api.loader.AccountFileLoader;
import com.github.johypark97.varchivemacro.macro.infrastructure.cache.CaptureImageCache;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.InputKeyCombination;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.MacroClientMode;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.infrastructure.config.repository.ConfigRepository;
import com.github.johypark97.varchivemacro.macro.ui.stage.SettingStage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.function.Consumer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingPresenterImpl implements Setting.SettingPresenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingPresenterImpl.class);

    private static final EnumSet<InputKey> INVALID_INPUT_KEY_SET =
            EnumSet.of(InputKey.UNDEFINED, InputKey.CONTROL, InputKey.ALT, InputKey.SHIFT);

    private final ConfigRepository configRepository;
    private final CaptureImageCacheFactory captureImageCacheFactory;

    private final SettingStage settingStage;

    private final SimpleBooleanProperty changed = new SimpleBooleanProperty();

    private MacroConfig.Builder macroConfigBuilder;
    private ScannerConfig.Builder scannerConfigBuilder;
    private boolean invalidAccountFile;
    private boolean invalidCacheDirectory;

    @MvpView
    public Setting.SettingView view;

    public SettingPresenterImpl(SettingStage settingStage, ConfigRepository configRepository,
            CaptureImageCacheFactory captureImageCacheFactory) {
        this.captureImageCacheFactory = captureImageCacheFactory;
        this.configRepository = configRepository;
        this.settingStage = settingStage;
    }

    private void showConfig() {
        showConfig_macro();
        showConfig_scanner();

        changed.set(false);
        invalidAccountFile = false;
        invalidCacheDirectory = false;
    }

    private void showConfig_macro() {
        macroConfigBuilder = MacroConfig.Builder.from(configRepository.findMacroConfig());

        view.setMacroClientMode(macroConfigBuilder.clientMode);
        view.setMacroUploadKeyText(macroConfigBuilder.uploadKey.toString());

        view.setMacroStartUpKeyText(macroConfigBuilder.startUpKey.toString());
        view.setMacroStartDownKeyText(macroConfigBuilder.startDownKey.toString());
        view.setMacroStopKeyText(macroConfigBuilder.stopKey.toString());

        view.setupMacroSongSwitchingTimeSlider(macroConfigBuilder.songSwitchingTime,
                MacroConfig.SONG_SWITCHING_TIME_DEFAULT, MacroConfig.SONG_SWITCHING_TIME_MIN,
                MacroConfig.SONG_SWITCHING_TIME_MAX);

        view.setupMacroPostCaptureDelaySlider(macroConfigBuilder.postCaptureDelay,
                MacroConfig.POST_CAPTURE_DELAY_DEFAULT, MacroConfig.POST_CAPTURE_DELAY_MIN,
                MacroConfig.POST_CAPTURE_DELAY_MAX);

        view.setupMacroKeyHoldTimeSlider(macroConfigBuilder.keyHoldTime,
                MacroConfig.KEY_HOLD_TIME_DEFAULT, MacroConfig.KEY_HOLD_TIME_MIN,
                MacroConfig.KEY_HOLD_TIME_MAX);
    }

    private void showConfig_scanner() {
        scannerConfigBuilder = ScannerConfig.Builder.from(configRepository.findScannerConfig());

        view.setScannerAccountFileText(scannerConfigBuilder.accountFile);

        view.setScannerStartKeyText(scannerConfigBuilder.startKey.toString());
        view.setScannerStopKeyText(scannerConfigBuilder.stopKey.toString());

        view.setScannerCacheDirectoryText(scannerConfigBuilder.cacheDirectory);

        view.setScannerAutoAnalysis(scannerConfigBuilder.autoAnalysis);

        view.setupScannerAnalyzerThreadCountSlider(scannerConfigBuilder.analyzerThreadCount,
                ScannerConfig.ANALYZER_THREAD_COUNT_DEFAULT,
                ScannerConfig.ANALYZER_THREAD_COUNT_MIN, ScannerConfig.ANALYZER_THREAD_COUNT_MAX);

        view.setupScannerCaptureDelaySlider(scannerConfigBuilder.captureDelay,
                ScannerConfig.CAPTURE_DELAY_DEFAULT, ScannerConfig.CAPTURE_DELAY_MIN,
                ScannerConfig.CAPTURE_DELAY_MAX);

        view.setupScannerKeyHoldTimeSlider(scannerConfigBuilder.keyHoldTime,
                ScannerConfig.KEY_HOLD_TIME_DEFAULT, ScannerConfig.KEY_HOLD_TIME_MIN,
                ScannerConfig.KEY_HOLD_TIME_MAX);
    }

    private boolean applyConfig() {
        if (invalidAccountFile) {
            if (!validateAccountFile(scannerConfigBuilder.accountFile)) {
                return false;
            }
            invalidAccountFile = false;
        }

        if (invalidCacheDirectory) {
            if (!validateCacheDirectory(scannerConfigBuilder.cacheDirectory)) {
                return false;
            }
            invalidCacheDirectory = false;
        }

        configRepository.saveMacroConfig(macroConfigBuilder.build());
        configRepository.saveScannerConfig(scannerConfigBuilder.build());

        try {
            configRepository.flush();
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log("Config flush exception.");
            settingStage.showError(Language.INSTANCE.getString("setting.dialog.applyException"), e);
        }

        GlobalEventBus.INSTANCE.fire(GlobalEvent.SETTING_UPDATED);

        return true;
    }

    private void updateKey(KeyEvent event, Consumer<InputKeyCombination> configKeySetter,
            Consumer<String> viewTextSetter) {
        InputKey key = InputKeyConverter.from(event.getCode());
        if (INVALID_INPUT_KEY_SET.contains(key) || !InputKeyConverter.isInteroperable(key)) {
            return;
        }

        InputKeyCombination combination =
                new InputKeyCombination(key, event.isControlDown(), event.isAltDown(),
                        event.isShiftDown());

        configKeySetter.accept(combination);
        viewTextSetter.accept(combination.toString());

        changed.set(true);
    }

    private boolean validateAccountFile(String value) {
        Language language = Language.INSTANCE;

        Path path;

        try {
            path = PathValidator.validateAndConvert(value);
        } catch (IOException e) {
            settingStage.showError(language.getString("setting.dialog.accountFile.header"),
                    language.getString("setting.dialog.accountFile.invalidPath"), e);

            return false;
        }

        try {
            new AccountFileLoader(path).load();
        } catch (NoSuchFileException e) {
            settingStage.showError(language.getString("setting.dialog.accountFile.header"),
                    language.getString("setting.dialog.accountFile.notExists"), e);

            return false;
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log("Account file validation exception.");

            settingStage.showError(language.getString("setting.dialog.accountFile.header"),
                    language.getString("setting.dialog.accountFile.invalidFile"), e);

            return false;
        }

        return true;
    }

    private boolean validateCacheDirectory(String value) {
        Language language = Language.INSTANCE;

        CaptureImageCache cache;

        try {
            cache = captureImageCacheFactory.create(value);
        } catch (IOException e) {
            settingStage.showError(language.getString("setting.dialog.cacheDirectory.header"),
                    language.getString("setting.dialog.cacheDirectory.invalidPath"), e);

            return false;
        }

        try {
            cache.validate();
        } catch (NotDirectoryException e) {
            settingStage.showError(language.getString("setting.dialog.cacheDirectory.header"),
                    language.getString("setting.dialog.cacheDirectory.notDirectory"), e);

            return false;
        } catch (DirectoryNotEmptyException e) {
            settingStage.showError(language.getString("setting.dialog.cacheDirectory.header"),
                    language.getString("setting.dialog.cacheDirectory.notEmptyDirectory"), e);

            return false;
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log("Cache directory validation exception.");

            settingStage.showError(language.getString("setting.dialog.cacheDirectory.header"),
                    language.getString("setting.dialog.cacheDirectory.invalidDirectory"), e);

            return false;
        }

        return true;
    }

    private Path toRelativePath(Path path) {
        return new PathHelper(path).toRelativeOfOrNot(Path.of("").toAbsolutePath());
    }

    @Override
    public void startView() {
        view.bindApplyButtonEnable(changed);
        view.bindRevertButtonEnable(changed);

        showConfig();
    }

    @Override
    public boolean stopView() {
        if (!changed.get()) {
            return true;
        }

        return switch (settingStage.showCloseDialog()) {
            case APPLY -> applyConfig();
            case CANCEL -> false;
            case CLOSE -> true;
        };
    }

    @Override
    public void apply() {
        if (applyConfig()) {
            showConfig();
        }
    }

    @Override
    public void revert() {
        showConfig();
    }

    @Override
    public void close() {
        settingStage.stopStage();
    }

    @Override
    public void macro_onChangeClientMode(MacroClientMode value) {
        macroConfigBuilder.clientMode = value;
        changed.set(true);
    }

    @Override
    public void macro_onChangeUploadKey(KeyEvent event) {
        updateKey(event, x -> macroConfigBuilder.uploadKey = x, view::setMacroUploadKeyText);
    }

    @Override
    public void macro_onChangeStartUpKey(KeyEvent event) {
        updateKey(event, x -> macroConfigBuilder.startUpKey = x, view::setMacroStartUpKeyText);
    }

    @Override
    public void macro_onChangeStartDownKey(KeyEvent event) {
        updateKey(event, x -> macroConfigBuilder.startDownKey = x, view::setMacroStartDownKeyText);
    }

    @Override
    public void macro_onChangeStopKey(KeyEvent event) {
        updateKey(event, x -> macroConfigBuilder.stopKey = x, view::setMacroStopKeyText);
    }

    @Override
    public void macro_onChangeSongSwitchingTime(int value) {
        macroConfigBuilder.songSwitchingTime = value;
        changed.set(true);
    }

    @Override
    public void macro_onChangePostCaptureDelay(int value) {
        macroConfigBuilder.postCaptureDelay = value;
        changed.set(true);
    }

    @Override
    public void macro_onChangeKeyHoldTime(int value) {
        macroConfigBuilder.keyHoldTime = value;
        changed.set(true);
    }

    @Override
    public void scanner_onChangeAccountFile(String value) {
        scannerConfigBuilder.accountFile = value;
        invalidAccountFile = true;
        changed.set(true);
    }

    @Override
    public void scanner_showAccountFileSelector() {
        File accountFile = settingStage.showAccountFileSelector();
        if (accountFile == null) {
            return;
        }

        String pathString = toRelativePath(accountFile.toPath()).toString();
        if (!validateAccountFile(pathString)) {
            return;
        }

        view.setScannerAccountFileText(pathString);
        changed.set(true);
    }

    @Override
    public void scanner_onChangeStartKey(KeyEvent event) {
        updateKey(event, x -> scannerConfigBuilder.startKey = x, view::setScannerStartKeyText);
    }

    @Override
    public void scanner_onChangeStopKey(KeyEvent event) {
        updateKey(event, x -> scannerConfigBuilder.stopKey = x, view::setScannerStopKeyText);
    }

    @Override
    public void scanner_onChangeCacheDirectory(String value) {
        scannerConfigBuilder.cacheDirectory = value;
        invalidCacheDirectory = true;
        changed.set(true);
    }

    @Override
    public void scanner_showCacheDirectorySelector() {
        File cacheDirectory = settingStage.showCacheDirectorySelector();
        if (cacheDirectory == null) {
            return;
        }

        String pathString = toRelativePath(cacheDirectory.toPath()).toString();
        if (!validateCacheDirectory(pathString)) {
            return;
        }

        view.setScannerCacheDirectoryText(pathString);
        changed.set(true);
    }

    @Override
    public void scanner_onChangeAutoAnalysis(boolean value) {
        scannerConfigBuilder.autoAnalysis = value;
        changed.set(true);
    }

    @Override
    public void scanner_onChangeAnalyzerThreadCount(int value) {
        scannerConfigBuilder.analyzerThreadCount = value;
        changed.set(true);
    }

    @Override
    public void scanner_onChangeCaptureDelay(int value) {
        scannerConfigBuilder.captureDelay = value;
        changed.set(true);
    }

    @Override
    public void scanner_onChangeKeyHoldTime(int value) {
        scannerConfigBuilder.keyHoldTime = value;
        changed.set(true);
    }
}
