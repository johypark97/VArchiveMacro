package com.github.johypark97.varchivemacro.macro.ui.event;

import com.github.johypark97.varchivemacro.macro.integration.context.ScannerContext;

public record ScannerScanDoneUiEvent(ScannerContext scannerContext) implements UiEvent {
}
