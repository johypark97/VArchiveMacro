package com.github.johypark97.varchivemacro.macro.common.converter;

import com.github.johypark97.varchivemacro.lib.scanner.Enums;
import com.github.johypark97.varchivemacro.macro.domain.scanner.model.RecordButton;

public class RecordButtonConverter {
    public static RecordButton toDomain(Enums.Button button) {
        return switch (button) {
            case _4 -> RecordButton.B4;
            case _5 -> RecordButton.B5;
            case _6 -> RecordButton.B6;
            case _8 -> RecordButton.B8;
        };
    }

    public static Enums.Button toLib(RecordButton button) {
        return switch (button) {
            case B4 -> Enums.Button._4;
            case B5 -> Enums.Button._5;
            case B6 -> Enums.Button._6;
            case B8 -> Enums.Button._8;
        };
    }
}
