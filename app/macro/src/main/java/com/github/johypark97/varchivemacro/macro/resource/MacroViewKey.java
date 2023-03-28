package com.github.johypark97.varchivemacro.macro.resource;

import java.util.Arrays;
import java.util.List;

public enum MacroViewKey {
    // @formatter:off
    ABOUT_DATE("about.date"),
    ABOUT_SOURCE_CODE("about.sourceCode"),
    ABOUT_VERSION("about.version"),
    ANALYZE_SCANNER_TASK_BUTTON("analyzeScannerTaskButton"),
    CONTROL_START_SCANNING("control.startScanning"),
    CONTROL_STOP("control.stop"),
    CONTROL_TITLE("control.title"),
    DIALOG_DJ_NAME("dialog.djName"),
    DLC_CHECKBOX_TITLE("dlcCheckboxTitle"),
    LOAD_CACHED_IMAGES_BUTTON("loadCachedImagesButton"),
    LOAD_REMOTE_RECORD_BUTTON("loadRemoteRecordButton"),
    MENU_FILE("menu.file"),
    MENU_FILE_EXIT("menu.file.exit"),
    MENU_INFO("menu.info"),
    MENU_INFO_ABOUT("menu.info.about"),
    MENU_INFO_OSL("menu.info.osl"),
    MENU_LANGUAGE("menu.language"),
    REFRESH_SCANNER_RESULT_BUTTON("refreshScannerResultButton"),
    SELECT_ALL_BUTTON("selectAllButton"),
    SETTING_SCANNER_ACCOUNT_FILE("setting.scanner.accountFile"),
    SETTING_SCANNER_CACHE_DIRECTORY("setting.scanner.cacheDirectory"),
    SETTING_SCANNER_KEY_INPUT_DURATION("setting.scanner.keyInputDuration"),
    SETTING_SCANNER_RECORD_UPLOAD_DELAY("setting.scanner.recordUploadDelay"),
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
