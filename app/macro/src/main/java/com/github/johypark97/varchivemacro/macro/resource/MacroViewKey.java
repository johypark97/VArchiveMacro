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
    SETTING_SCANNER_SELECT("setting.scanner.select"),
    SHOW_EXPECTED_BUTTON("showExpectedButton"),
    SHOW_SCANNER_TASK_BUTTON("showScannerTaskButton"),
    TAB_MACRO("tab.macro"),
    TAB_RECORD_VIEWER("tab.recordViewer"),
    TAB_SCANNER("tab.scanner"),
    TAB_SCANNER_RESULT("tab.scanner.result"),
    TAB_SCANNER_SETTING("tab.scanner.setting"),
    TAB_SCANNER_TASK("tab.scanner.task"),
    TOGGLE_ALL_EXACT_BUTTON("toggleAllExact"),
    TOGGLE_ALL_SIMILAR_BUTTON("toggleAllSimilar"),
    UNSELECT_ALL_BUTTON("unselectAllButton"),
    UPLOAD_RECORD_BUTTON("uploadRecordButton"),

    TAB_SCANNER_TASK_TABLE_ACCURACY("tab.scanner.task.table.accuracy"),
    TAB_SCANNER_TASK_TABLE_COMPOSER("tab.scanner.task.table.composer"),
    TAB_SCANNER_TASK_TABLE_DISTANCE("tab.scanner.task.table.distance"),
    TAB_SCANNER_TASK_TABLE_DLC("tab.scanner.task.table.dlc"),
    TAB_SCANNER_TASK_TABLE_SCANNED_TITLE("tab.scanner.task.table.scannedTitle"),
    TAB_SCANNER_TASK_TABLE_SELECTED("tab.scanner.task.table.selected"),
    TAB_SCANNER_TASK_TABLE_STATUS("tab.scanner.task.table.status"),
    TAB_SCANNER_TASK_TABLE_TAB("tab.scanner.task.table.tab"),
    TAB_SCANNER_TASK_TABLE_TASK_NUMBER("tab.scanner.task.table.taskNumber"),
    TAB_SCANNER_TASK_TABLE_TITLE("tab.scanner.task.table.title"),

    TAB_SCANNER_TASK_TABLE_STATUS_ANALYZED("tab.scanner.task.table.status.analyzed"),
    TAB_SCANNER_TASK_TABLE_STATUS_ANALYZING("tab.scanner.task.table.status.analyzing"),
    TAB_SCANNER_TASK_TABLE_STATUS_DUPLICATED("tab.scanner.task.table.status.duplicated"),
    TAB_SCANNER_TASK_TABLE_STATUS_EXCEPTION("tab.scanner.task.table.status.exception"),
    TAB_SCANNER_TASK_TABLE_STATUS_FOUND("tab.scanner.task.table.status.found"),
    TAB_SCANNER_TASK_TABLE_STATUS_NOT_FOUND("tab.scanner.task.table.status.notFound"),
    TAB_SCANNER_TASK_TABLE_STATUS_WAITING("tab.scanner.task.table.status.waiting"),

    TAB_SCANNER_RESULT_TABLE_BUTTON("tab.scanner.result.table.button"),
    TAB_SCANNER_RESULT_TABLE_COMPOSER("tab.scanner.result.table.composer"),
    TAB_SCANNER_RESULT_TABLE_DELTA_RATE("tab.scanner.result.table.delta"),
    TAB_SCANNER_RESULT_TABLE_DLC("tab.scanner.result.table.dlc"),
    TAB_SCANNER_RESULT_TABLE_NEW_MAX_COMBO("tab.scanner.result.table.nMax"),
    TAB_SCANNER_RESULT_TABLE_NEW_RATE("tab.scanner.result.table.new"),
    TAB_SCANNER_RESULT_TABLE_OLD_MAX_COMBO("tab.scanner.result.table.oMax"),
    TAB_SCANNER_RESULT_TABLE_OLD_RATE("tab.scanner.result.table.old"),
    TAB_SCANNER_RESULT_TABLE_PATTERN("tab.scanner.result.table.pattern"),
    TAB_SCANNER_RESULT_TABLE_RESULT_NUMBER("tab.scanner.result.table.resultNo"),
    TAB_SCANNER_RESULT_TABLE_STATUS("tab.scanner.result.table.status"),
    TAB_SCANNER_RESULT_TABLE_TASK_NUMBER("tab.scanner.result.table.taskNo"),
    TAB_SCANNER_RESULT_TABLE_TITLE("tab.scanner.result.table.title"),
    TAB_SCANNER_RESULT_TABLE_UPLOAD("tab.scanner.result.table.upload"),

    TAB_SCANNER_RESULT_TABLE_STATUS_CANCELED("task.scanner.result.table.status.canceled"),
    TAB_SCANNER_RESULT_TABLE_STATUS_HIGHER_RECORD_EXISTS("task.scanner.result.table.status.higherRecordExists"),
    TAB_SCANNER_RESULT_TABLE_STATUS_SUSPENDED("task.scanner.result.table.status.suspended"),
    TAB_SCANNER_RESULT_TABLE_STATUS_UPLOADED("task.scanner.result.table.status.uploaded"),
    TAB_SCANNER_RESULT_TABLE_STATUS_UPLOADING("task.scanner.result.table.status.uploading"),
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
