package com.github.johypark97.varchivemacro.macro.core.scanner;

import com.github.johypark97.varchivemacro.lib.common.ocr.PixError;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixWrapper;

public class PixPreprocessor {
    public static void preprocessTitle(PixWrapper pix) throws PixError {
        pix.convertRGBToLuminance();
        pix.gammaTRC(1.0f, 128, 255);
        pix.invert();
    }
}
