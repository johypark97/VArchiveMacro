package com.github.johypark97.varchivemacro.macro.resource;

import java.util.Arrays;
import java.util.List;

public enum MacroPresenterKey {
    // @formatter:off
    CHANGE_LANGUAGE("changeLanguage"),
    COMMAND_IS_NOT_RUNNING("commandIsNotRunning"),
    COMMAND_IS_RUNNING("commandIsRunning"),
    COMMAND_IS_RUNNING_CANNOT_EXIT("commandIsRunning.cannotExit"),
    LOADING_CONFIG_ERROR("loadingConfig.error"),
    LOADING_CONFIG_EXCEPTION("loadingConfig.exception"),
    LOADING_RECORD_ERROR("loadingRecord.error"),
    LOADING_RECORD_EXCEPTION("loadingRecord.exception"),
    LOADING_RECORD_FILE_NOT_FOUND("loadingRecord.fileNotFound"),
    LOADING_RECORD_LOADED("loadingRecord.loaded"),
    LOADING_RECORD_PLEASE_LOAD("loadingRecord.pleaseLoad"),
    LOADING_TASK_DATA_EXCEPTION("loadingTaskData.exception"),
    LOADING_TASK_DATA_OCCURRED("loadingTaskData.occurred"),
    START_COMMAND("startCommand"),
    WHEN_CANCELED("when.canceled"),
    WHEN_DONE("when.done"),
    WHEN_START_ANALYZE("when.start.analyze"),
    WHEN_START_CAPTURE("when.start.capture"),
    WHEN_START_COLLECT_RESULT("when.start.collectResult"),
    WHEN_START_LOAD_REMOTE("when.start.loadRemote"),
    WHEN_START_MACRO("when.start.macro"),
    WHEN_START_UPLOAD_RECORD("when.start.uploadRecord"),
    WHEN_THROWN("when.thrown"),
    ;
    // @formatter:on

    private static final String PREFIX = "macro.presenter.";

    private final String value;

    MacroPresenterKey(String s) {
        value = s;
    }

    public static List<String> valueList() {
        return Arrays.stream(values()).map(MacroPresenterKey::toString).toList();
    }

    @Override
    public String toString() {
        return PREFIX + value;
    }
}
