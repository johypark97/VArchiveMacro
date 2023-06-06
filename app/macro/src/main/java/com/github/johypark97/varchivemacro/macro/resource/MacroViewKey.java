package com.github.johypark97.varchivemacro.macro.resource;

import java.util.Arrays;
import java.util.List;

public enum MacroViewKey {
    // @formatter:off
    ABOUT_DATE("about.date"),
    ABOUT_SOURCE_CODE("about.sourceCode"),
    ABOUT_VERSION("about.version"),
    ANALYZE_SCANNER_TASK_BUTTON("analyzeScannerTaskButton"),
    CONTROL_START_MACRO_DOWN("control.startMacro.down"),
    CONTROL_START_MACRO_UP("control.startMacro.up"),
    CONTROL_START_SCANNING("control.startScanning"),
    CONTROL_STOP("control.stop"),
    CONTROL_TITLE("control.title"),
    DIALOG_DJ_NAME("dialog.djName"),
    DIALOG_UPLOAD_CANCELED("dialog.upload.canceled"),
    DIALOG_UPLOAD_CHECKBOX("dialog.upload.checkbox"),
    DIALOG_UPLOAD_MESSAGE0("dialog.upload.message0"),
    DIALOG_UPLOAD_MESSAGE1("dialog.upload.message1"),
    DIALOG_UPLOAD_MESSAGE2("dialog.upload.message2"),
    DIALOG_UPLOAD_TITLE("dialog.upload.title"),
    DLC_CHECKBOX_TITLE("dlcCheckboxTitle"),
    LOAD_REMOTE_RECORD_BUTTON("loadRemoteRecordButton"),
    MENU_FILE("menu.file"),
    MENU_FILE_EXIT("menu.file.exit"),
    MENU_INFO("menu.info"),
    MENU_INFO_ABOUT("menu.info.about"),
    MENU_INFO_OSL("menu.info.osl"),
    MENU_LANGUAGE("menu.language"),
    MENU_LANGUAGE_ENG("menu.language.eng"),
    MENU_LANGUAGE_KOR("menu.language.kor"),
    REFRESH_SCANNER_RESULT_BUTTON("refreshScannerResultButton"),
    SELECT_ABOVE_100_BUTTON("selectAbove100Button"),
    SELECT_ALL_BUTTON("selectAllButton"),
    SETTING_CAPTURE_DELAY("setting.captureDelay"),
    SETTING_KEY_INPUT_DURATION("setting.keyInputDuration"),
    SETTING_MACRO_ANALYZE_KEY("setting.macro.analyzeKey"),
    SETTING_MACRO_CAPTURE_DURATION("setting.macro.captureDuration"),
    SETTING_MACRO_COUNT("setting.macro.count"),
    SETTING_SCANNER_ACCOUNT_FILE("setting.scanner.accountFile"),
    SETTING_SCANNER_CACHE_DIRECTORY("setting.scanner.cacheDirectory"),
    SETTING_SCANNER_RECORD_UPLOAD_DELAY("setting.scanner.recordUploadDelay"),
    SETTING_SCANNER_SAFE_MODE_CHECKBOX("setting.scanner.safeMode.checkBox"),
    SETTING_SCANNER_SAFE_MODE_DIALOG_MESSAGE0("setting.scanner.safeMode.dialog.message0"),
    SETTING_SCANNER_SAFE_MODE_DIALOG_MESSAGE1("setting.scanner.safeMode.dialog.message1"),
    SETTING_SCANNER_SAFE_MODE_DIALOG_MESSAGE2("setting.scanner.safeMode.dialog.message2"),
    SETTING_SCANNER_SAFE_MODE_DIALOG_MESSAGE3("setting.scanner.safeMode.dialog.message3"),
    SETTING_SCANNER_SAFE_MODE_DIALOG_TITLE("setting.scanner.safeMode.dialog.title"),
    SETTING_SCANNER_SAFE_MODE_LABEL("setting.scanner.safeMode.label"),
    SETTING_SCANNER_SAFE_MODE_LOG_TO_OFF("setting.scanner.safeMode.logMessage.toOff"),
    SETTING_SCANNER_SAFE_MODE_LOG_TO_ON("setting.scanner.safeMode.logMessage.toOn"),
    SETTING_SCANNER_SELECT("setting.scanner.select"),
    SHOW_EXPECTED_BUTTON("showExpectedButton"),
    SHOW_SCANNER_TASK_BUTTON("showScannerTaskButton"),
    TAB_MACRO("tab.macro"),
    TAB_RECORD_VIEWER("tab.recordViewer"),
    TAB_SCANNER("tab.scanner"),
    TAB_SCANNER_RESULT("tab.scanner.result"),
    TAB_SCANNER_SETTING("tab.scanner.setting"),
    TAB_SCANNER_TASK("tab.scanner.task"),
    UNSELECT_ALL_BUTTON("unselectAllButton"),
    UPLOAD_RECORD_BUTTON("uploadRecordButton"),
    ;
    // @formatter:on

    private static final String PREFIX = "macro.view.";

    private final String value;

    MacroViewKey(String s) {
        value = s;
    }

    public static List<String> valueList() {
        return Arrays.stream(values()).map(MacroViewKey::toString).toList();
    }

    @Override
    public String toString() {
        return PREFIX + value;
    }
}
