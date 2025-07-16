package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.InputKeyCombination;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroClientMode;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.MacroConfig;
import com.github.johypark97.varchivemacro.macro.common.config.domain.model.ScannerConfig;
import com.github.johypark97.varchivemacro.macro.common.converter.InputKeyConverter;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.validator.AccountFileValidator;
import com.github.johypark97.varchivemacro.macro.common.validator.DiskCaptureImageCacheDirectoryValidator;
import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.exception.InvalidAccountFileException;
import com.github.johypark97.varchivemacro.macro.integration.context.GlobalContext;
import com.github.johypark97.varchivemacro.macro.ui.event.SettingUpdatedUiEvent;
import com.github.johypark97.varchivemacro.macro.ui.event.UiEventBus;
import com.github.johypark97.varchivemacro.macro.ui.mvp.Setting;
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

public class SettingPresenterImpl implements Setting.Presenter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingPresenterImpl.class);

    private static final EnumSet<InputKey> INVALID_INPUT_KEY_SET =
            EnumSet.of(InputKey.UNDEFINED, InputKey.CONTROL, InputKey.ALT, InputKey.SHIFT);

    private final SettingStage settingStage;

    private final GlobalContext globalContext;

    private final SimpleBooleanProperty changed = new SimpleBooleanProperty();

    private MacroConfig.Builder macroConfigBuilder;
    private ScannerConfig.Builder scannerConfigBuilder;
    private boolean invalidAccountFile;
    private boolean invalidCacheDirectory;

    @MvpView
    public Setting.View view;

    public SettingPresenterImpl(SettingStage settingStage, GlobalContext globalContext) {
        this.settingStage = settingStage;

        this.globalContext = globalContext;
    }

    private void showConfig() {
        showConfig_macro();
        showConfig_scanner();

        changed.set(false);
        invalidAccountFile = false;
        invalidCacheDirectory = false;
    }

    private void showConfig_macro() {
        macroConfigBuilder = globalContext.configService.findMacroConfig().toBuilder();

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
        scannerConfigBuilder = globalContext.configService.findScannerConfig().toBuilder();

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

        globalContext.configService.saveMacroConfig(macroConfigBuilder.build());
        globalContext.configService.saveScannerConfig(scannerConfigBuilder.build());

        try {
            globalContext.configStorageService.save();
        } catch (Exception e) {
            LOGGER.atError().setCause(e).log("Config flush exception.");
            settingStage.showError(Language.INSTANCE.getString("setting.dialog.applyException"), e);
        }

        UiEventBus.INSTANCE.fire(new SettingUpdatedUiEvent());

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
            AccountFileValidator.validate(path);
        } catch (NoSuchFileException e) {
            settingStage.showError(language.getString("setting.dialog.accountFile.header"),
                    language.getString("setting.dialog.accountFile.notExists"), e);

            return false;
        } catch (InvalidAccountFileException e) {
            settingStage.showError(language.getString("setting.dialog.accountFile.header"),
                    language.getString("setting.dialog.accountFile.invalidFile"), e);

            return false;
        } catch (Exception e) {
            String message = "Account file validation exception.";
            LOGGER.atError().setCause(e).log(message);
            settingStage.showError(message, e);

            return false;
        }

        return true;
    }

    private boolean validateCacheDirectory(String value) {
        Language language = Language.INSTANCE;

        Path cacheDirectoryPath;

        try {
            cacheDirectoryPath = PathValidator.validateAndConvert(value);
        } catch (IOException e) {
            settingStage.showError(language.getString("setting.dialog.cacheDirectory.header"),
                    language.getString("setting.dialog.cacheDirectory.invalidPath"), e);

            return false;
        }

        try {
            DiskCaptureImageCacheDirectoryValidator.validate(cacheDirectoryPath);
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
