package com.github.johypark97.varchivemacro.macro.ui.mvp.presenter;

import com.github.johypark97.varchivemacro.lib.common.PathHelper;
import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.github.johypark97.varchivemacro.macro.common.config.AppConfigManager;
import com.github.johypark97.varchivemacro.macro.common.config.AppConfigService;
import com.github.johypark97.varchivemacro.macro.common.config.model.AppConfig;
import com.github.johypark97.varchivemacro.macro.common.config.model.InputKeyCombination;
import com.github.johypark97.varchivemacro.macro.common.config.model.MacroClientMode;
import com.github.johypark97.varchivemacro.macro.common.converter.InputKeyConverter;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import com.github.johypark97.varchivemacro.macro.common.validator.AccountFileValidator;
import com.github.johypark97.varchivemacro.macro.common.validator.DiskCaptureImageCacheDirectoryValidator;
import com.github.johypark97.varchivemacro.macro.common.validator.PathValidator;
import com.github.johypark97.varchivemacro.macro.core.scanner.api.infra.exception.InvalidAccountFileException;
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

    private final SimpleBooleanProperty changed = new SimpleBooleanProperty();

    private AppConfig.Editor appConfigEditor;
    private boolean invalidAccountFile;
    private boolean invalidCacheDirectory;

    @MvpView
    public Setting.View view;

    public SettingPresenterImpl(SettingStage settingStage) {
        this.settingStage = settingStage;
    }

    private void showConfig() {
        appConfigEditor = AppConfigManager.INSTANCE.getAppConfigService().getConfig().edit();

        showConfig_macro();
        showConfig_scanner();
        showConfig_program();

        changed.set(false);
        invalidAccountFile = false;
        invalidCacheDirectory = false;
    }

    private void showConfig_macro() {
        view.setMacroClientMode(appConfigEditor.getMacroConfig().clientMode().value());
        view.setMacroUploadKeyText(appConfigEditor.getMacroConfig().uploadKey().value().toString());

        view.setMacroStartUpKeyText(
                appConfigEditor.getMacroConfig().startUpKey().value().toString());
        view.setMacroStartDownKeyText(
                appConfigEditor.getMacroConfig().startDownKey().value().toString());
        view.setMacroStopKeyText(appConfigEditor.getMacroConfig().stopKey().value().toString());

        view.setupMacroSongSwitchingTimeSlider(
                appConfigEditor.getMacroConfig().songSwitchingTime().value(),
                appConfigEditor.getMacroConfig().songSwitchingTime().defaultValue(),
                appConfigEditor.getMacroConfig().songSwitchingTime().min(),
                appConfigEditor.getMacroConfig().songSwitchingTime().max());

        view.setupMacroPostCaptureDelaySlider(
                appConfigEditor.getMacroConfig().postCaptureDelay().value(),
                appConfigEditor.getMacroConfig().postCaptureDelay().defaultValue(),
                appConfigEditor.getMacroConfig().postCaptureDelay().min(),
                appConfigEditor.getMacroConfig().postCaptureDelay().max());

        view.setupMacroKeyHoldTimeSlider(appConfigEditor.getMacroConfig().keyHoldTime().value(),
                appConfigEditor.getMacroConfig().keyHoldTime().defaultValue(),
                appConfigEditor.getMacroConfig().keyHoldTime().min(),
                appConfigEditor.getMacroConfig().keyHoldTime().max());
    }

    private void showConfig_scanner() {
        view.setScannerAccountFileText(appConfigEditor.getScannerConfig().accountFile().value());

        view.setScannerStartKeyText(
                appConfigEditor.getScannerConfig().startKey().value().toString());
        view.setScannerStopKeyText(appConfigEditor.getScannerConfig().stopKey().value().toString());

        view.setScannerCacheDirectoryText(
                appConfigEditor.getScannerConfig().cacheDirectory().value());

        view.setScannerAutoAnalysis(appConfigEditor.getScannerConfig().autoAnalysis().value());

        view.setupScannerAnalyzerThreadCountSlider(
                appConfigEditor.getScannerConfig().analyzerThreadCount().value(),
                appConfigEditor.getScannerConfig().analyzerThreadCount().defaultValue(),
                appConfigEditor.getScannerConfig().analyzerThreadCount().min(),
                appConfigEditor.getScannerConfig().analyzerThreadCount().max());

        view.setupScannerCaptureDelaySlider(
                appConfigEditor.getScannerConfig().captureDelay().value(),
                appConfigEditor.getScannerConfig().captureDelay().defaultValue(),
                appConfigEditor.getScannerConfig().captureDelay().min(),
                appConfigEditor.getScannerConfig().captureDelay().max());

        view.setupScannerKeyHoldTimeSlider(appConfigEditor.getScannerConfig().keyHoldTime().value(),
                appConfigEditor.getScannerConfig().keyHoldTime().defaultValue(),
                appConfigEditor.getScannerConfig().keyHoldTime().min(),
                appConfigEditor.getScannerConfig().keyHoldTime().max());
    }

    private void showConfig_program() {
        view.setProgramPrereleaseNotification(
                appConfigEditor.getProgramConfig().prereleaseNotification().value());
    }

    private boolean applyConfig() {
        if (invalidAccountFile) {
            if (!validateAccountFile(appConfigEditor.getScannerConfig().accountFile().value())) {
                return false;
            }
            invalidAccountFile = false;
        }

        if (invalidCacheDirectory) {
            if (!validateCacheDirectory(
                    appConfigEditor.getScannerConfig().cacheDirectory().value())) {
                return false;
            }
            invalidCacheDirectory = false;
        }

        AppConfigService appConfigService = AppConfigManager.INSTANCE.getAppConfigService();

        appConfigService.editConfig(x -> appConfigEditor);

        try {
            appConfigService.save();
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
        appConfigEditor.editMacroConfig(x -> x.setClientMode(value));
        changed.set(true);
    }

    @Override
    public void macro_onChangeUploadKey(KeyEvent event) {
        updateKey(event, key -> appConfigEditor.editMacroConfig(x -> x.setUploadKey(key)),
                view::setMacroUploadKeyText);
    }

    @Override
    public void macro_onChangeStartUpKey(KeyEvent event) {
        updateKey(event, key -> appConfigEditor.editMacroConfig(x -> x.setStartUpKey(key)),
                view::setMacroStartUpKeyText);
    }

    @Override
    public void macro_onChangeStartDownKey(KeyEvent event) {
        updateKey(event, key -> appConfigEditor.editMacroConfig(x -> x.setStartDownKey(key)),
                view::setMacroStartDownKeyText);
    }

    @Override
    public void macro_onChangeStopKey(KeyEvent event) {
        updateKey(event, key -> appConfigEditor.editMacroConfig(x -> x.setStopKey(key)),
                view::setMacroStopKeyText);
    }

    @Override
    public void macro_onChangeSongSwitchingTime(int value) {
        appConfigEditor.editMacroConfig(x -> x.setSongSwitchingTime(value));
        changed.set(true);
    }

    @Override
    public void macro_onChangePostCaptureDelay(int value) {
        appConfigEditor.editMacroConfig(x -> x.setPostCaptureDelay(value));
        changed.set(true);
    }

    @Override
    public void macro_onChangeKeyHoldTime(int value) {
        appConfigEditor.editMacroConfig(x -> x.setKeyHoldTime(value));
        changed.set(true);
    }

    @Override
    public void scanner_onChangeAccountFile(String value) {
        appConfigEditor.editScannerConfig(x -> x.setAccountFile(value));
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
        updateKey(event, key -> appConfigEditor.editScannerConfig(x -> x.setStartKey(key)),
                view::setScannerStartKeyText);
    }

    @Override
    public void scanner_onChangeStopKey(KeyEvent event) {
        updateKey(event, key -> appConfigEditor.editScannerConfig(x -> x.setStopKey(key)),
                view::setScannerStopKeyText);
    }

    @Override
    public void scanner_onChangeCacheDirectory(String value) {
        appConfigEditor.editScannerConfig(x -> x.setCacheDirectory(value));
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
        appConfigEditor.editScannerConfig(x -> x.setAutoAnalysis(value));
        changed.set(true);
    }

    @Override
    public void scanner_onChangeAnalyzerThreadCount(int value) {
        appConfigEditor.editScannerConfig(x -> x.setAnalyzerThreadCount(value));
        changed.set(true);
    }

    @Override
    public void scanner_onChangeCaptureDelay(int value) {
        appConfigEditor.editScannerConfig(x -> x.setCaptureDelay(value));
        changed.set(true);
    }

    @Override
    public void scanner_onChangeKeyHoldTime(int value) {
        appConfigEditor.editScannerConfig(x -> x.setKeyHoldTime(value));
        changed.set(true);
    }

    @Override
    public void program_onChangePrereleaseNotification(boolean value) {
        appConfigEditor.editProgramConfig(x -> x.setPrereleaseNotification(value));
        changed.set(true);
    }
}
